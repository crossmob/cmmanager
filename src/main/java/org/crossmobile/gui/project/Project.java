/*
 * (c) 2020 by Panayotis Katsaloulis
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
import org.crossmobile.gui.parameters.DependenciesParameter;
import org.crossmobile.gui.parameters.LibrariesParameter;
import org.crossmobile.gui.parameters.ProjectParameter;
import org.crossmobile.gui.parameters.ScreenScaleParameter;
import org.crossmobile.gui.parameters.impl.*;
import org.crossmobile.gui.utils.Paths;
import org.crossmobile.gui.utils.Profile;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.*;
import org.crossmobile.utils.func.Opt;
import org.crossmobile.utils.images.ImageHound;

import javax.swing.border.EmptyBorder;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.crossmobile.gui.project.ProjectInfo.OLD_ANT;
import static org.crossmobile.gui.project.ProjectInfo.OLD_XMLVM;
import static org.crossmobile.prefs.Config.MATERIALS_PATH;
import static org.crossmobile.utils.ParamsCommon.*;
import static org.crossmobile.utils.TemplateUtils.updateProperties;
import static org.crossmobile.utils.func.ScopeUtils.with;

public class Project {

    private final File basedir;
    private final ParamList params;
    private List<PropertySheet> sheets;
    private final ImageHound imageHound;
    //    private final Collection<Image> appicons;
    private final boolean asOldCrossmobile;
    private final boolean isPlugin;
    ProjectPlugins plugins;
    private boolean asOldXMLVMProject;
    private Profile profile;
    private String debugProfile = DEBUG_PROFILE.tag().deflt;
    private final GlobalParamListener listener = new GlobalParamListener();
    private Consumer<Project> saveCallback;

    @SuppressWarnings("LeakingThisInConstructor")
    public Project(ProjectInfo projinf) throws ProjectException {
        basedir = projinf.getPath();
        params = new ParamList();
        params.updateFromProperties(new File(basedir, OLD_XMLVM));
        params.updateFromProperties(new File(basedir, OLD_ANT));
        params.updateFromProperties(new File(basedir, "nbproject/project.properties"));
        params.updateFromProperties(new File(basedir, "ant.properties"));
        params.updateFromProperties(new File(basedir, "local.properties"));
        boolean correctPom = params.updateFromPom(new File(basedir, "pom.xml"));
        ProjectUpdator.updateOldToNew(params.getProperties());    // just in case... should be last to properly support themes

        asOldXMLVMProject = new File(basedir, OLD_XMLVM).exists();
        asOldCrossmobile = !asOldXMLVMProject && new File(basedir, OLD_ANT).exists();
        if (!correctPom && !asOldCrossmobile)
            throw new ProjectException("Unable to parse POM file");

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

            if (asOldXMLVMProject || asOldCrossmobile) {
                OldSourceParser.updateSources(basedir, new File(basedir, params.dereferenceProperty("src.java.dir")), asOldCrossmobile ? "CrossMobile" : "XMLVM");
                asOldXMLVMProject = false;
            }
            ProjectUpdator.update(basedir, params);
            // Update project properties
            updatedPom = new Pom(new File(basedir, "pom.xml")).updatePomFromProperties(params.getParamset(), params.getProperties(), isPlugin);
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
        ProjectParameter projname = new DisplayNameParameter(params).addParameterListener(property -> listener.updateTitle(property.getValue()));
        listener.updateTitle(projname.getValue());
        csheet.add(projname);
        csheet.add(new ArtifactIdParameter(params));
        csheet.add(new GroupIdParameter(params));
        csheet.add(new VersionParameter(params));
        csheet.add(with(new ActiveLabel("Meta properties"), it -> it.setBorder(new EmptyBorder(24, 0, 0, 0))));
        csheet.add(new DescriptionParameter(params));
        csheet.add(new VendorParameter(params));
        csheet.add(new URLParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("Libraries", listener);
        csheet.add(new LibrariesParameter(params, basedir));
        sheets.add(csheet);
    }

    private void createProjectSheets() {
        sheets = new ArrayList<>();
        PropertySheet csheet;

        csheet = new PropertySheet("General", listener);
        ProjectParameter projname = new DisplayNameParameter(params).addParameterListener(property -> listener.updateTitle(property.getValue()));
        listener.updateTitle(projname.getValue());
        csheet.add(projname);
        csheet.add(new ArtifactIdParameter(params));
        csheet.add(new GroupIdParameter(params));
        csheet.add(new VersionParameter(params));
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
        csheet.add(new StatusBarHiddenParameter(params));
        csheet.add(new ViewControlledStatusBarParameter(params));
        csheet.add(new SplashDelayParameter(params));
        sheets.add(csheet);

        csheet = new PropertySheet("iOS", listener);
        csheet.add(new InjectedInfoParameter(params));
        csheet.add(new HideIncludesParameter(params));
        csheet.add(new FileSharingParameter(params));
        csheet.add(new SafeMembersParameter(params));
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
}
