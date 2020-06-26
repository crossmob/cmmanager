/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.gui.elements.NewProjectInfo;
import org.crossmobile.gui.init.InitializationWizard;
import org.crossmobile.gui.init.InitializationWizard.Card;
import org.crossmobile.gui.utils.CMMvnActions;
import org.crossmobile.gui.utils.NameConverter;
import org.crossmobile.utils.Commander;
import org.crossmobile.utils.FileUtils;
import org.crossmobile.utils.Pom;
import org.crossmobile.utils.ProjectException;
import org.crossmobile.utils.images.ImageHound;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.crossmobile.prefs.Config.*;
import static org.crossmobile.utils.ParamsCommon.ARTIFACT_ID;
import static org.crossmobile.utils.ParamsCommon.DISPLAY_NAME;

public class ProjectInfo {

    public static final String MAVEN_SIGNATURE = "pom.xml";
    public static final String OLD_ANT = "project.crossmobile";
    public static final String OLD_XMLVM = "xmlvm.properties";
    private final static String INITIAL_VERSION = "1.0.0.0";

    private ImageHound imageHound;
    private String name;
    private final File basedir;
    private boolean plugin = false;

    public static ProjectInfo load(String path) throws ProjectException {
        return new ProjectInfo(path, null);
    }

    public static ProjectInfo create(String path, NewProjectInfo newProjectInfo) throws ProjectException {
        return new ProjectInfo(path, newProjectInfo);
    }

    private static File findProjectDir(String givenPath, boolean baseLevel) throws ProjectException {
        File dir = new File(givenPath);
        if (!dir.exists())
            dir = dir.getParentFile();
        if (!dir.exists())
            throw new ProjectException("Unable to find project location");

        if (dir.isFile())
            dir = dir.getParentFile();
        dir = dir.getAbsoluteFile();
        if (!FileUtils.isWritable(dir))
            throw new ProjectException("Path " + dir.getPath() + " is not writable.");

        if (!new File(dir, MAVEN_SIGNATURE).isFile() && !new File(dir, OLD_XMLVM).isFile() && !new File(dir, OLD_ANT).isFile())
            if (baseLevel && dir.getName().toLowerCase().equals(dir.getParentFile().getName().toLowerCase()))
                dir = findProjectDir(dir.getParent(), false);
            else
                throw new ProjectException("Unrecognized project location", dir);
        return dir;
    }

    private ProjectInfo(String pathname, NewProjectInfo newProjectInfo) throws ProjectException {
        if (pathname == null)
            throw new ProjectException("Invalid path provided");

        if (newProjectInfo != null) {
            File projectDir = new File(pathname);
            if (projectDir.exists())
                throw new ProjectException("Project location already exists");
            projectDir.mkdirs();
            if (!projectDir.isDirectory())
                throw new ProjectException("Unable to create project location");
            InitializationWizard initW = new InitializationWizard(newProjectInfo);
            initW.setMainTitle("Creating project '" + newProjectInfo.getDisplayName() + "'");
            initW.setSubtitle("A new project will be created under: " + pathname);
            initW.gotoCard(Card.Info);
            initW.hideActionButton();
            initW.setRunning(true);
            AtomicBoolean fail = new AtomicBoolean(false);
            initW.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    Commander cmd = CMMvnActions.createProject(newProjectInfo.getTemplateType(), newProjectInfo.getDisplayName(), newProjectInfo.getApplicationName(), newProjectInfo.getId(), INITIAL_VERSION, newProjectInfo.getProjectPath());
                    cmd.setCharOutListener(initW.getStreamManager()::incomingOutChar);
                    cmd.setCharErrListener(initW.getStreamManager()::incomingOutChar);
                    cmd.setEndListener(code -> {
                        initW.setRunning(false);
                        if (!code.equals(0)) {
                            initW.setMainTitle("Error while creating project");
                            initW.gotoCard(Card.Details);
                            fail.set(true);
                        } else
                            initW.setVisible(false);
                    });
                    cmd.exec();
                }
            });
            initW.setVisible(true);
            if (fail.get())
                throw new ProjectException("Unable to create project");
        }
        this.basedir = findProjectDir(pathname, true);
        refresh(newProjectInfo);
    }

    public void refresh(NewProjectInfo newProjectInfo) {
        Properties props = new Properties();
        if (new File(basedir, MAVEN_SIGNATURE).exists()) {
            Pom pom = new Pom(new File(basedir, MAVEN_SIGNATURE));
            pom.updatePropertiesFromPom(props);
            plugin = pom.isPlugin();
        } else
            try {
                props.load(new InputStreamReader(new FileInputStream(new File(basedir, "nbproject" + File.separator + "project.properties")), StandardCharsets.UTF_8));
            } catch (IOException ignored) {
            }
        props.computeIfAbsent(ARTIFACT_ID.tag().name, k -> newProjectInfo != null ? newProjectInfo.getApplicationName() : NameConverter.unicodeToAsciiID(basedir.getAbsolutePath()));
        props.computeIfAbsent(DISPLAY_NAME.tag().name, k -> newProjectInfo != null ? newProjectInfo.getDisplayName() : basedir.getName());
        name = props.get(DISPLAY_NAME.tag().name).toString();

        imageHound = new ImageHound();
        imageHound.addForegroundImages("/images/logo-" + (plugin ? "plugin" : "icon") + "@2x.png", new File(basedir, FORE_ICONS), new File(basedir, ICON_DIR));
        imageHound.addBackgroundImages("/images/empty.png", new File(basedir, BACK_ICONS));
    }

    public ImageHound getImageHound() {
        return imageHound;
    }

    public String getName() {
        return name;
    }

    public File getPath() {
        return basedir;
    }

    public boolean isValid() {
        return new File(basedir, OLD_ANT).isFile() || new File(basedir, OLD_XMLVM).isFile() || new Pom(new File(basedir, MAVEN_SIGNATURE)).isValid();
    }

    public boolean isPlugin() {
        return plugin;
    }

    @Override
    public int hashCode() {
        return basedir.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProjectInfo other = (ProjectInfo) obj;
        return Objects.equals(this.basedir, other.basedir);
    }

}
