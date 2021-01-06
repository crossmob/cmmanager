/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.gui.elements.NewProjectInfo;
import org.crossmobile.gui.init.InitializationWizard;
import org.crossmobile.gui.init.InitializationWizard.Card;
import org.crossmobile.gui.utils.CMMvnActions;
import org.crossmobile.utils.Commander;
import org.crossmobile.utils.FileUtils;
import org.crossmobile.utils.Pom;
import org.crossmobile.utils.ProjectException;
import org.crossmobile.utils.images.ImageHound;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.crossmobile.prefs.Config.*;

public class ProjectInfo {

    public static final String MAVEN_SIGNATURE = "pom.xml";
    private final static String INITIAL_VERSION = "1.0.0.0";

    private final File basedir;
    private boolean plugin;

    private ImageHound imageHound;
    private String name;

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

        if (!new File(dir, MAVEN_SIGNATURE).isFile())
            if (baseLevel && dir.getName().toLowerCase().equals(dir.getParentFile().getName().toLowerCase()))
                dir = findProjectDir(dir.getParent(), false);
            else
                throw new ProjectException("Unrecognized project location", dir);
        return dir;
    }

    private ProjectInfo(final String pathname, NewProjectInfo newProjectInfo) throws ProjectException {
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
        if (!refresh())
            throw new ProjectException("Invalid project at location " + this.basedir.getAbsolutePath());
    }

    public boolean refresh() {
        Pom pom = Pom.read(new File(basedir, MAVEN_SIGNATURE));
        if (pom == null)
            return false;
        plugin = pom.isPlugin();
        name = pom.getNameFromPom();
        imageHound = new ImageHound();
        imageHound.addForegroundImages("/images/logo-" + (plugin ? "plugin" : "icon") + "@2x.png", new File(basedir, FORE_ICONS), new File(basedir, ICON_DIR));
        imageHound.addBackgroundImages("/images/empty.png", new File(basedir, BACK_ICONS));
        return true;
    }

    public boolean exists() {
        return new File(basedir, "pom.xml").isFile();
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
