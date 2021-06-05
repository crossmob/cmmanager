/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.Version;
import org.crossmobile.bridge.system.BaseUtils;
import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.gui.codehound.source.FileHit;
import org.crossmobile.gui.codehound.source.SourceParser;
import org.crossmobile.gui.codehound.source.SourcePattern;
import org.crossmobile.gui.codehound.source.SourcePatternFactory;
import org.crossmobile.gui.parameters.*;
import org.crossmobile.gui.parameters.impl.*;
import org.crossmobile.gui.utils.CMMvnActions.MavenExecutor;
import org.crossmobile.gui.utils.Paths;
import org.crossmobile.gui.utils.Profile;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.*;
import org.crossmobile.utils.func.Opt;
import org.crossmobile.utils.images.ImageHound;

import javax.swing.border.EmptyBorder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.crossmobile.prefs.Config.MATERIALS_PATH;
import static org.crossmobile.utils.ParamsCommon.DEBUG_PROFILE;
import static org.crossmobile.utils.ParamsCommon.MAIN_CLASS;
import static org.crossmobile.utils.TemplateUtils.updateProperties;
import static org.crossmobile.utils.func.ScopeUtils.with;

public class Project {

    private final File basedir;
    private final ParamList params;
    private List<PropertySheet> sheets;
    private final ImageHound imageHound;
    //    private final Collection<Image> appicons;
    private final boolean isPlugin;
    ProjectPlugins plugins;
    private Profile profile;
    private String debugProfile = DEBUG_PROFILE.tag().deflt;
    private final GlobalParamListener listener = new GlobalParamListener();
    private Consumer<Project> saveCallback;
    private MavenExecutor mavenExecutor;

    @SuppressWarnings("LeakingThisInConstructor")
    public Project(ProjectInfo projinf) throws ProjectException {
        basedir = projinf.getPath();
        Pom pom = Pom.read(new File(basedir, "pom.xml"));
        if (pom == null)
            throw new ProjectException("Unable to read project at " + basedir.getAbsolutePath());

        params = new ParamList();
        params.updateFromPom(pom);
        params.updateFromProperties(new File(basedir, "local.properties"));
        ProjectUpdater.updateOldToNew(params.getProperties());    // just in case... should be last to properly support themes

        profile = Profile.safeValueOf(Prefs.getLaunchType(basedir.getAbsolutePath()));
        plugins = new ProjectPlugins(params);
        imageHound = projinf.getImageHound();
        isPlugin = projinf.isPlugin();

        if (!isPlugin) {
            // Update main class
            SourceParser parser = new SourceParser(basedir.getAbsolutePath() + "/src/main/java");
            parser.setPattern(SourcePatternFactory.getMainClassPattern());
            List<SourcePattern> patterns = parser.parse();
            if (!patterns.isEmpty()) {
                SourcePattern sourcepattern = patterns.get(0);

                Set<String> found = new HashSet<>();
                for (FileHit hit : sourcepattern.getFileHits()) {
                    String classname = hit.getClassName();
                    if (!classname.startsWith("org.xmlvm.iphone.")
                            && !classname.startsWith("org.crossmobile.backend."))
                        found.add(classname);
                }
                if (found.isEmpty())
                    Log.warning("Main class could not be found");
                else {
                    String which = found.iterator().next();
                    params.put(MAIN_CLASS.tag(), which);
                    if (found.size() > 1)
                        Log.warning("More than one main classes found, using " + which);
                }
            }
        }
        Prefs.setCurrentDir(basedir.getParentFile());
    }

    public void setApplicationNameListener(BiConsumer<Boolean, String> listener) {
        this.listener.setApplicationNameListener(listener);
    }

    public String getProperty(ParamsCommon param) {
        return params.get(param.tag());
    }

    public File getPath() {
        return basedir;
    }

    public File getPom() {
        return new File(basedir, "pom.xml");
    }

    public ImageHound getIconHound() {
        return imageHound;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getDebugProfile() {
        return debugProfile;
    }

    public boolean isSaved() {
        return !listener.isDirty();
    }

    public boolean isPlugin() {
        return isPlugin;
    }

    public void save() throws ProjectException {
        Pom updatedPom = null;
        try {
            // Sheets have been initialized: we are able to update properties from user input. Otherwise ignore
            if (sheets != null)
                for (PropertySheet sheet : getSheets())
                    for (ProjectParameter prop : sheet.getProperties())
                        prop.updatePropertyList();
            // Should we update sources? Halt for now
            if (false)
                OldSourceParser.updateSources(basedir, new File(basedir, params.dereferenceProperty("src.java.dir")));
            ProjectUpdater.update(basedir, params);
            // Update project properties
            updatedPom = Pom.read(new File(basedir, "pom.xml"));
            if (updatedPom == null)
                throw new ProjectException("Unable to update pom file");
            updatedPom.updatePomFromProperties(params.getParamset(), params.getProperties(), isPlugin);
            updatedPom.setParentProject(Version.VERSION);
            updatedPom.saveTemp();
            if (!isPlugin)
                updateProperties("local.properties", new File(basedir, "local.properties"), params, null);
            listener.updateDefaults();
            Opt.of(saveCallback).ifExists(s -> s.accept(this));
        } catch (Throwable th) {
            updatedPom = null;
            if (th instanceof ProjectException)
                BaseUtils.throwException(th);
            else
                throw new ProjectException(th.toString(), th);
        } finally {
            if (updatedPom != null)
                updatedPom.putInPlace();
        }
    }

    public void setSaveCallback(Consumer<Project> callback) {
        this.saveCallback = callback;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Project other = (Project) obj;
        return this.basedir == other.basedir || (this.basedir != null && Paths.getPath(basedir, null).equals(Paths.getPath(other.basedir, null)));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.basedir != null ? this.basedir.getAbsolutePath().hashCode() : 0);
        return hash;
    }

    public Iterable<PropertySheet> getSheets() {
        if (sheets == null) {
            if (isPlugin)
                createPluginSheets();
            else
                createProjectSheets();
        }
        return sheets;
    }

    private void createPluginSheets() {
        sheets = new ArrayList<>();
        PropertySheet csheet;

        csheet = new PropertySheet("General", listener);
        ProjectParameter projname = new DisplayNameParameter(params, isPlugin).addParameterListener(property -> listener.updateTitle(property.getValue()));
        listener.updateTitle(projname.getValue());
        csheet.add(projname);
        csheet.add(new ArtifactIdParameter(params, isPlugin));
        csheet.add(new GroupIdParameter(params));
        csheet.add(new VersionParameter(params, isPlugin));
        csheet.add(with(new ActiveLabel("Meta properties"), it -> it.setBorder(new EmptyBorder(24, 0, 0, 0))));
        csheet.add(new DescriptionParameter(params));
        csheet.add(new VendorParameter(params));
        csheet.add(new URLParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("Libraries", listener);
        csheet.add(new LibrariesParameter(params, basedir, mavenExecutor));
        sheets.add(csheet);
    }

    private void createProjectSheets() {
        sheets = new ArrayList<>();
        PropertySheet csheet;

        csheet = new PropertySheet("General", listener);
        ProjectParameter projname = new DisplayNameParameter(params, isPlugin).addParameterListener(property -> listener.updateTitle(property.getValue()));
        listener.updateTitle(projname.getValue());
        csheet.add(projname);
        csheet.add(new ArtifactIdParameter(params, isPlugin));
        csheet.add(new GroupIdParameter(params));
        csheet.add(new VersionParameter(params, isPlugin));
        csheet.add(new MainClassParameter(params));
        csheet.add(new ProfileParameter(params, profile)
                .addParameterListener(prop -> Prefs.setLaunchType(basedir.getAbsolutePath(), (profile = Profile.safeValueOf(prop.getValue())).name().toLowerCase())));
        csheet.add(new JavacSourceParameter(params));
        csheet.add(new JavacTargetParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("Plugins", listener);
        csheet.add(new DependenciesParameter(params));
        sheets.add(csheet);

        InitialOrientationParameter init_orientation;
        SupportedOrientationsParameter supp_orientation;
        csheet = new PropertySheet("Visuals", listener);
        csheet.add(new StoryboardParameter(params, new File(basedir, MATERIALS_PATH)));
        csheet.add(new LaunchStoryboardParameter(params, new File(basedir, MATERIALS_PATH)));
        csheet.add(new ScreenScaleParameter(params));
        csheet.add(new ProjectTypeParameter(params));
        csheet.add(init_orientation = new InitialOrientationParameter(params));
        csheet.add(supp_orientation = new SupportedOrientationsParameter(params));
        supp_orientation.addParameterListener(p -> init_orientation.check(supp_orientation.getValue()));
        init_orientation.addParameterListener(p -> supp_orientation.setOrientation(p.getValue()));
        csheet.add(new SplashDelayParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("iOS", listener);
        csheet.add(new InjectedInfoParameter(params));
        csheet.add(new HideIncludesParameter(params));
        csheet.add(new FileSharingParameter(params));
        csheet.add(new SafeMembersParameter(params));
        csheet.add(new DeploymentTargetParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("Android", listener);
        AndroidKeyAliasParameter ka = new AndroidKeyAliasParameter(params);
        csheet.add(new AndroidKeyStoreParameter(params).addParameterListener(ka));
        csheet.add(ka);
        csheet.add(new AndroidKeystorePasswordParameter(params));
        csheet.add(new AndroidAliasPasswordParameter(params));
        csheet.add(new AndroidLogParameter(params)
                .addParameterListener(pl -> debugProfile = pl.getValue()));
        csheet.add(new AndroidPermissionsParameter(params, this));
        csheet.add(new AndroidSDKParameter(params));
        csheet.add(new AndroidTargetParameter(params));
        csheet.add(new AndroidTargetNumericParameter(params));
//        csheet.setBottomPanel(PrivateArtifactForm.getPanel());
        sheets.add(csheet);

        csheet = new PropertySheet("Aroma", listener);
        csheet.add(new DisplayInfoParameter("Aroma is", "...", "https://aroma-ui.com"));
        csheet.add(new AromaTargetParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("Desktop", listener);
        csheet.add(new SkinListParameter(params));
        csheet.add(new KeyboardSupportParameter(params));
        csheet.add(with(new ActiveLabel("Meta properties"), it -> it.setBorder(new EmptyBorder(24, 0, 0, 0))));
        csheet.add(new DescriptionParameter(params));
        csheet.add(new VendorParameter(params));
        csheet.add(new URLParameter(params));
//        csheet.setBottomPanel(SendStackTrace.getPanel());
        sheets.add(csheet);
    }

    public void setLaunchContext(MavenExecutor mavenExecutor) {
        this.mavenExecutor = mavenExecutor;
    }
}
