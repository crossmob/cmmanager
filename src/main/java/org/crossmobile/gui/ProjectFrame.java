/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui;

import com.panayotis.appenh.AFileChooser;
import com.panayotis.appenh.EnhancerManager;
import com.panayotis.hrgui.*;
import org.crossmobile.gui.actives.*;
import org.crossmobile.gui.elements.BottomPanel;
import org.crossmobile.gui.elements.DebugInfo;
import org.crossmobile.gui.elements.GradientPanel;
import org.crossmobile.gui.elements.Theme;
import org.crossmobile.gui.project.Project;
import org.crossmobile.gui.project.ProjectLauncher;
import org.crossmobile.gui.project.ProjectLoader;
import org.crossmobile.gui.project.PropertySheet;
import org.crossmobile.gui.utils.*;
import org.crossmobile.gui.utils.CMMvnActions.MavenExecInfo;
import org.crossmobile.gui.utils.Deguard.MagicWand;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.*;
import org.crossmobile.utils.func.Opt;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.crossmobile.gui.actives.ActiveContextPanel.Context.*;
import static org.crossmobile.gui.utils.Profile.OBFUSCATE;
import static org.crossmobile.prefs.Prefs.*;
import static org.crossmobile.utils.ParamsCommon.*;
import static org.crossmobile.utils.SystemDependent.Execs.ADB;
import static org.crossmobile.utils.SystemDependent.safeArg;

public final class ProjectFrame extends RegisteredFrame implements DebugInfo.Consumer, CMMvnActions.MavenExecutor {

    private static final int KILL_RESULT = 143;
    private static final int NOT_SAVED = 144;

    private static final HiResIcon RUN_I = new ActiveIcon("images/run");
    private static final HiResIcon EXPAND_I = new ActiveIcon("images/expand");
    private static final HiResIcon BUILD_I = new ActiveIcon("images/build");
    private static final HiResIcon STOP_I = new ActiveIcon("images/stop");
    private static final HiResIcon STOP_D = STOP_I.getDisabledIcon();
    private static final HiResIcon SAVE_I = new ActiveIcon("images/save");
    private static final HiResIcon SAVE_TI = new ActiveIcon("images/saveT");
    private static final HiResIcon CLEAN_I = new ActiveIcon("images/clean");
    private static final HiResIcon CLEANPROJ_I = new ActiveIcon("images/cleanproj");
    private static final HiResIcon CLEAN_TI = new ActiveIcon("images/cleanT");
    private static final HiResIcon PACK_I = new ActiveIcon("images/pack");
    private static final HiResIcon OPEN_I = new ActiveIcon("images/open");
    private static final HiResIcon DESKTOP_I = new ActiveIcon("images/desktop_small");
    private static final HiResIcon NETBEANS_I = new ActiveIcon("images/netbeans");
    private static final HiResIcon NETBEANS_D = NETBEANS_I.getDisabledIcon();
    private static final HiResIcon STUDIO_I = new ActiveIcon("images/studio");
    private static final HiResIcon STUDIO_D = STUDIO_I.getDisabledIcon();
    private static final HiResIcon INTELLIJ_I = new ActiveIcon("images/intellij");
    private static final HiResIcon INTELLIJ_D = INTELLIJ_I.getDisabledIcon();
    private static final HiResIcon XCODE_I = new ActiveIcon("images/xcode");
    private static final HiResIcon XCODE_D = XCODE_I.getDisabledIcon();
    private static final HiResIcon VSTUDIO_I = new ActiveIcon("images/vstudio");
    private static final HiResIcon VSTUDIO_D = VSTUDIO_I.getDisabledIcon();
    private static final HiResIcon JAR_I = new ActiveIcon("images/jar");
    private static final HiResIcon APK_I = new ActiveIcon("images/apk");
    private static final HiResIcon PROJECT_I = new ActiveIcon("images/project");
    private static final HiResIcon PROJECT_D = PROJECT_I.getDisabledIcon();
    private static final HiResIcon OUTPUT_I = new ActiveIcon("images/output");
    private static final HiResIcon OUTPUT_D = OUTPUT_I.getDisabledIcon();
    private static final HiResIcon STDERR_I = new ActiveIcon("images/stderr");
    private static final HiResIcon STDOUT_I = new ActiveIcon("images/stdout");

    private Project proj;
    private final Collection<JComponent> autoDisabled = new ArrayList<>();
    private boolean isProjectEnabled = false;
    private Consumer<Project> closeCallback;
    private Commander launch;
    private String taskName = null;
    private final AtomicReference<Runnable> solutionCallbackRef = new AtomicReference<>();
    private MagicWand magicWandB;

    @SuppressWarnings("LeakingThisInConstructor")
    public ProjectFrame(File path) {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Loading project " + path.getName() + "...");
        autoDisabled.add(iosT);
        autoDisabled.add(androidT);
        autoDisabled.add(desktopT);
        autoDisabled.add(uwpT);
        autoDisabled.add(cleanB);
        autoDisabled.add(expandCB);
        autoDisabled.add(expandRB);
        autoDisabled.add(projectB);
        autoDisabled.add(expandPB);
        autoDisabled.add(packB);
        autoDisabled.add(openB);
        magicWandB = Deguard.getWandButton(this);
        actionB.setEnabled(false);
        setProjectEnabled(false);
    }

    public void setCloseCallback(Consumer<Project> callBack) {
        this.closeCallback = callBack;
    }

    private void deactivateComponent(AbstractButton button) {
        button.setIcon(null);
        button.setEnabled(false);
        autoDisabled.remove(button);
    }

    public void initVisuals(Project p) {
        proj = p;
        p.setLaunchContext(this);
        setTitle(proj.getProperty(DISPLAY_NAME));
        getRootPane().putClientProperty("Window.documentFile", proj.getPath());
        EnhancerManager.getDefault().updateFrameIconsWithImages(this, proj.getIconHound().getDeclaredImages());
        if (!SystemDependent.canMakeUwp())
            vstudioM.setVisible(false);
        if (!SystemDependent.canMakeIos())
            xcodeM.setVisible(false);
        rightPanel.add(leftButtonPanel, BorderLayout.NORTH);
        ButtonGroup group = new ButtonGroup();
        ActionListener listener = (ActionEvent e) -> ((CardLayout) parameters.getLayout()).show(parameters, e.getActionCommand());
        for (PropertySheet sheet : proj.getSheets()) {
            HiResPanel boxed = new HiResPanel();
            boxed.setOpaque(false);
            boxed.setLayout(new BoxLayout(boxed, BoxLayout.Y_AXIS));
            for (HiResComponent item : sheet.getVisuals()) {
                if (item != null) {
                    HiResPanel cp = new HiResPanel(new BorderLayout());
                    cp.setOpaque(false);
                    cp.add(item.comp(), BorderLayout.CENTER);
                    boxed.add(cp);
                    cp.setBorder(new CompoundBorder(new HiResMatteBorder(0, 0, 1, 0, Theme.current().line), new HiResEmptyBorder(4, 0, 4, 0)));
                }
            }
            sheet.alignVisuals();
            boxed.setBorder(new HiResEmptyBorder(8, 8, 8, 8));

            HiResPanel overall = new GradientPanel();
            overall.setLayout(new BorderLayout());
            overall.add(boxed, BorderLayout.NORTH);
            HiResScrollPane scroll = new HiResScrollPane();
            scroll.setViewportView(overall);

            String sheetName = sheet.getName();
            ActivePanel bottomPanel = sheet.getBottomPanel();
            JComponent sheetVisuals;
            if (bottomPanel == null)
                sheetVisuals = scroll;
            else {
                if (bottomPanel instanceof BottomPanel)
                    ((BottomPanel) bottomPanel).setProjectFrame(this);
                ActivePanel container = new ActivePanel(new BorderLayout());
                container.add(scroll, BorderLayout.CENTER);
                container.add(bottomPanel, BorderLayout.SOUTH);
                scroll.setBorder(null);
                sheetVisuals = container;
            }
            parameters.add(sheetVisuals, sheetName);
            sheetVisuals.setBorder(new ActiveEtchedBorder());

            ActiveToggleButton button = new ActiveToggleButton(sheetName, new ActiveIcon("images/" + sheetName.toLowerCase()));
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setIconTextGap(0);
            button.setActionCommand(sheetName);
            button.addActionListener(listener);
            group.add(button);
            if (group.getButtonCount() == 1)
                button.setSelected(true);
            leftButtonPanel.add(button);
        }
        actionB.setActionCommand(Prefs.getSelectedLaunchAction(proj.getPath().getAbsolutePath()));
        updateLaunchVisuals();
        switch (Prefs.getSelectedLaunchTarget(proj.getPath().getAbsolutePath())) {
            case LAUNCH_TARGET_ANDROID:
                androidT.setSelected(true);
                break;
            case LAUNCH_TARGET_IOS:
                iosT.setSelected(true);
                break;
            case LAUNCH_TARGET_UWP:
                uwpT.setSelected(true);
                break;
            default:
            case LAUNCH_TARGET_DESKTOP:
                desktopT.setSelected(true);
                break;
        }
        if (proj.isPlugin()) {
            openM.remove(otherS);
            openM.remove(otherIDEs);
            deactivateComponent(expandRB);
            deactivateComponent(expandCB);
            deactivateComponent(iosT);
            deactivateComponent(androidT);
            deactivateComponent(uwpT);
            deactivateComponent(desktopT);
            actionB.setActionCommand(LAUNCH_ACTION_BUILD);
            packB.setVisible(false);
            updateLaunchVisuals();
        }

        actionB.setEnabled(true);
        setProjectEnabled(true);
        validate();
    }

    public DebugInfo getDebugInfo() {
        return new DebugInfo(outputTxt.getText(), proj.getProperty(ARTIFACT_ID));
    }

    @Override
    public void updateTo(String text) {
        ((ActiveTextPane) outputTxt).setText(text, StreamQuality.INFO);
    }

    private void displayOutput() {
        ((CardLayout) contentP.getLayout()).show(contentP, "output");
        outputB.setSelected(true);
        outputB.setText("Output");
    }

    private void displayProject() {
        ((CardLayout) contentP.getLayout()).show(contentP, "project");
        projectB.setSelected(true);
    }

    private void setProjectEnabled(boolean status) {
        isProjectEnabled = status;
        for (JComponent button : autoDisabled)
            button.setEnabled(status);
    }

    private synchronized void setLaunchButtonStatus(Integer result, String currentTaskName, String target) {
        Opt.of(solutionCallbackRef.get()).ifExists(r -> {
            solutionCallbackRef.set(null);
            SwingUtilities.invokeLater(r);
        });

        // treat specifically kill callback: already registered so ignore
        if (result != null && result == KILL_RESULT && currentTaskName == null && taskName == null)
            return;

        boolean running;
        if (result != null) {
            boolean success = result == 0;
            boolean wasKilled = result == KILL_RESULT;
            boolean notSaved = result == NOT_SAVED;
            String oldTaskName = taskName;
            String tn = oldTaskName == null ? "Operation" : oldTaskName;
            tn += " : " + (success ? "success" : (wasKilled ? "interrupted" : (notSaved ? "not saved" : "failed, error code " + result)));
            outResult.setText(tn);
            ((ActiveContextPanel) infoP).setContext(success ? SUCCESS : (wasKilled ? ActiveContextPanel.Context.ERROR : WARNING));
            outputB.setText(success ? "Output" : "Output*");
            if (success || wasKilled || notSaved)
                displayProject();
            running = false;
            currentTaskName = null;
        } else {
            displayOutput();
            outputB.setText("Output");
            outResult.setText(currentTaskName);
            ((ActiveContextPanel) infoP).setContext(RUNNING);
            running = true;
        }

        setProjectEnabled(!running);
        updateToolButtons(target);
        actionB.setText(running ? "Stop" : "Start");
        actionB.setIcon(running ? STOP_I : RUN_I);
        outputB.setEnabled(true);
        expandOB.setEnabled(outputB.isEnabled());
        if (!running)
            updateLaunchVisuals();

        taskName = currentTaskName;
    }

    private void updateToolButtons(String target) {
        idInfoP.removeAll();
        idInfoP.add(pidL, BorderLayout.CENTER);
        if (proj.getProfile() == OBFUSCATE) {
            File mapFile = "android".equals(target)
                    ? new File(proj.getPath(), "target/app/build/outputs/mapping/release/mapping.txt")
                    : "desktop".equals(target)
                    ? new File(proj.getPath(), "target/" + proj.getProperty(ARTIFACT_ID) + "-" + proj.getProperty(BUNDLE_VERSION) + ".map")
                    : null;
            if (mapFile != null) {
                magicWandB.setMapFile(mapFile);
                idInfoP.add(magicWandB, BorderLayout.EAST);
            }
        }
        idInfoP.validate();
    }

    private void updateLaunchVisuals() {
        actionB.setText(TextUtils.capitalize(actionB.getActionCommand()));
        switch (actionB.getActionCommand()) {
            case LAUNCH_ACTION_BUILD:
                actionB.setIcon(BUILD_I);
                break;
            default:
            case LAUNCH_ACTION_RUN:
                actionB.setIcon(RUN_I);
        }
        Prefs.setSelectedLaunchAction(proj.getPath().getAbsolutePath(), actionB.getActionCommand());
    }

    private boolean saveProjectWithErrorMessage() {
        try {
            proj.save();
            return true;
        } catch (ProjectException ex) {
            Log.error("Unable to update project", ex);
            return false;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible)
            ProjectLoader.unregisterProject(this);
        super.setVisible(visible);
    }

    private void saveOutPut() {
        File outfile = new AFileChooser().setFile("output.txt").save();
        if (outfile != null) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outfile), SystemDependent.getEncoding())) {
                writer.write(outputTxt.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Something went terribly wrong. :(  \n",
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getCurrentTarget() {
        return targetG.getSelection().getActionCommand();
    }

    private ActiveTextPane getTextPane() {
        return (ActiveTextPane) outputTxt;
    }

    private void initLaunchVisualsOut(MavenExecInfo execInfo) {
        setLaunchButtonStatus(null, execInfo.infoText, execInfo.target);
        ActiveTextPane out = (ActiveTextPane) outputTxt;
        out.setText("");
        pidL.setText("");
        if (!execInfo.consoleText.isEmpty())
            out.addLine("\n" + execInfo.consoleText + "\n", StreamQuality.INFO);
    }

    private void buildAndRun(String target) {
        buildAndRun(target, false,
                proj.getProfile().isRelease(),
                !LAUNCH_ACTION_BUILD.equals(actionB.getActionCommand()),
                null);
    }

    private void buildAndRun(String target, boolean distClean, boolean release, boolean run, Consumer<Integer> execCallback) {
        if (taskName != null)
            EventUtils.postAction(() -> {
                Opt.of(launch).ifExists(Commander::kill);
                setLaunchButtonStatus(KILL_RESULT, null, null);
            });
        else {
            initLaunchVisualsOut(new MavenExecInfo("Launch " + target + " target", "Building product", target));
            Runnable launcher = () -> launchMaven("install",
                    target
                            + (run ? ",run" : "")
                            + (release ? ",release" : "")
                            + (proj.getProfile() == OBFUSCATE ? ",obfuscate" : "")
                    , null, execCallback == null ? this::mavenFeedback : execCallback
                    , "-D" + DEBUG_PROFILE.tag().name + "=" + proj.getDebugProfile());
            EventUtils.postAction(() -> {
                if (saveProjectWithErrorMessage()) {
                    if (distClean)
                        launchMaven("clean", null, null, result -> {
                            if (result == 0)
                                launcher.run();
                        }, "-Pdistclean");
                    else
                        launcher.run();
                } else
                    setLaunchButtonStatus(NOT_SAVED, "Unable to save project", null);
            });
        }
    }


    private void openTarget(String target) {
        String info = "Request to launch project in " + target;
        initLaunchVisualsOut(new MavenExecInfo(info, "Open project in " + target, target));
        String preproc = OPEN_STUDIO.equals(target) ? LAUNCH_TARGET_ANDROID : (OPEN_XCODE.equals(target) ? LAUNCH_TARGET_IOS : (OPEN_VSTUDIO.equals(target) ? LAUNCH_TARGET_UWP : null));
        if (preproc != null)
            launchMaven("process-classes", preproc, new MavenExecInfo(info, "Open project in " + target, target),
                    onSuccess(() -> postProcessCode(getTextPane(), "", target)));
        else
            postProcessCode(getTextPane(), info, target);
    }

    private void postProcessCode(ActiveTextPane out, String info, String ide) {
        if (!info.isEmpty())
            out.addLine("\n" + info + "\n", StreamQuality.INFO);
        ExternalCommands.openCode(ide, proj, out, this::mavenFeedback);
    }

    private void updateInfo(AtomicLong pid, AtomicInteger port) {
        StringBuilder out = new StringBuilder();
        if (pid.get() != Long.MIN_VALUE)
            out.append("PID: ").append(pid.get());
        if (port.get() != Integer.MIN_VALUE) {
            if (out.length() > 0)
                out.append("    ");
            out.append("Debug port: ").append(port.get());
        }
        pidL.setText(out.toString());
    }

    @Override
    public void mavenFeedback(int result) {
        setLaunchButtonStatus(result, null, null);
        launch = null;
    }

    @Override
    public void launchMaven(String goal, String profiles, MavenExecInfo info, Consumer<Integer> launchCallback, String... params) {
        if (info != null)
            initLaunchVisualsOut(info);
        launch = execMavenInConsole(goal, profiles, proj, getTextPane(), launchCallback, params);
    }

    private Commander execMavenInConsole(String goal, String profiles, Project proj, ActiveTextPane outP, Consumer<Integer> launchCallback, String... params) {
        if (profiles != null && profiles.contains("uwp"))
            outP.addLine(" *** WARNING *** Universal Windows Platform support  is still in alpha stage\n", StreamQuality.ERROR);
        AtomicLong pid = new AtomicLong(Long.MIN_VALUE);
        AtomicInteger port = new AtomicInteger(Integer.MIN_VALUE);
        Commander commander = CMMvnActions.callMaven(goal, profiles, proj.getPath(), outP, pidF -> {
            pid.set(pidF);
            updateInfo(pid, port);
        }, portF -> {
            port.set(portF);
            updateInfo(pid, port);
        }, launchCallback, solutionCallbackRef, proj.getProfile(), params);
        if (profiles != null && profiles.contains("desktop")) {
            pid.set(commander.getPid());
            updateInfo(pid, port);
        }
        return commander;
    }

    private File getApkPath(boolean preferRelease) {
        File release_new = new File(proj.getPath(), "target/app/build/outputs/apk/release/" + proj.getPath().getName() + "-release.apk");
        File release_old = new File(proj.getPath(), "target/app/build/outputs/apk/" + proj.getPath().getName() + "-release.apk");
        File debug_new = new File(proj.getPath(), "target/app/build/outputs/apk/debug/" + proj.getPath().getName() + "-debug.apk");
        File debug_old = new File(proj.getPath(), "target/app/build/outputs/apk/" + proj.getPath().getName() + "-debug.apk");
        File pref1, pref2, pref3, pref4;
        if (preferRelease) {
            pref1 = release_new;
            pref2 = release_old;
            pref3 = debug_new;
            pref4 = debug_old;
        } else {
            pref1 = debug_new;
            pref2 = debug_old;
            pref3 = release_new;
            pref4 = release_old;
        }
        if (pref1.isFile())
            return pref1;
        else if (pref2.isFile())
            return pref2;
        else if (pref3.isFile())
            return pref3;
        else if (pref4.isFile())
            return pref4;
        else
            return null;
    }

    private File getJarPath() {
        File path = new File(proj.getPath(), "target" + File.separator + proj.getProperty(ARTIFACT_ID) + "-" + proj.getProperty(BUNDLE_VERSION) + "-desktop.jar");
        return path.exists() ? path : null;
    }

    private void createDesktopPackage(String os, boolean alsoInstaller) {
        buildAndRun("desktop", true, true, false, res -> Opt.of(getJarPath()).onError(Log::error).filter(File::isFile)
                .ifMissing(() -> mavenFeedback(res))
                .ifExists(jar -> {
                    ((ActiveTextPane) outputTxt).addLine("CREATING " + os.toUpperCase() + " " + (alsoInstaller ? "INSTALLER" : "PACKAGE") + "\n------------------------------------------------------------------------", StreamQuality.INFO);
                    File destDir = new File(jar.getParent(), os + "_package");
                    if (!destDir.mkdir()) {
                        Log.error("Unable to create folder " + destDir.getAbsolutePath());
                        mavenFeedback(-1);
                        return;
                    }
                    File resDir = ResourceInstaller.createResourceDir(proj.getPath(), proj.getIconHound(), os);
                    if (resDir == null) {
                        Log.error("Unable to create resources folder");
                        mavenFeedback(-1);
                        return;
                    }
                    ProjectLauncher.launch(proj.getPath(), (ActiveTextPane) outputTxt,
                            onSuccess(() -> SwingUtilities.invokeLater(() -> Opt.of(destDir).ifExists(d -> Desktop.getDesktop().open(d)))),
                            SystemDependent.getEnvWithFixedPaths(), null,
                            Paths.getMakeAppExec(), alsoInstaller ? "create" : "java",
                            "--os", os, "--name", proj.getProperty(DISPLAY_NAME), "--version", proj.getProperty(BUNDLE_VERSION),
                            "--jar", jar.getAbsolutePath(), "--output", destDir.getAbsolutePath(),
                            "--res", resDir.getAbsolutePath(),
                            "--descr", Opt.of(proj.getProperty(CM_DESCRIPTION)).filter(d -> !d.trim().isEmpty()).getOrElse(proj.getProperty(DISPLAY_NAME) + ": A CrossMobile application"),
                            "--id", proj.getProperty(GROUP_ID) + "." + proj.getProperty(ARTIFACT_ID),
                            "--vendor", Opt.of(proj.getProperty(CM_VENDOR)).filter(s -> !s.trim().isEmpty()).getOrElse(SystemDependent.getFullName()),
                            "--jdk=" + Prefs.getJDKLocation(), "--url", safeArg(proj.getProperty(CM_URL)),
                            alsoInstaller ? "--nosign" : null
                    );
                }));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        targetG = new javax.swing.ButtonGroup();
        projectG = new javax.swing.ButtonGroup();
        actionsM = new ActivePopupMenu();
        runM = new ActiveMenuItem();
        buildM = new ActiveMenuItem();
        projectM = new ActivePopupMenu();
        savePM = new ActiveMenuItem();
        outputM = new ActivePopupMenu();
        cleanOM = new ActiveMenuItem();
        saveOM = new ActiveMenuItem();
        openM = new ActivePopupMenu();
        desktopM = new ActiveMenuItem();
        jSeparator4 = new ActiveMenuSeparator();
        intellijM = new ActiveMenuItem();
        netbeansM = new ActiveMenuItem();
        otherS = new ActiveMenuSeparator();
        otherIDEs = new ActiveMenu();
        xcodeM = new ActiveMenuItem();
        vstudioM = new ActiveMenuItem();
        studioM = new ActiveMenuItem();
        packIM = new ActivePopupMenu();
        nosupportedIP = new ActiveMenuItem();
        packAM = new ActivePopupMenu();
        debugP = new ActiveMenuItem();
        releaseP = new ActiveMenuItem();
        packWM = new ActivePopupMenu();
        nosupportedWP = new ActiveMenuItem();
        packDMM = new ActivePopupMenu();
        filesOnlyM = new ActiveMenu();
        genericP = new ActiveMenuItem();
        linuxP = new ActiveMenuItem();
        linuxA32P = new ActiveMenuItem();
        linuxA64P = new ActiveMenuItem();
        win32P = new ActiveMenuItem();
        win64P = new ActiveMenuItem();
        macosP = new ActiveMenuItem();
        filesInstallerM = new ActiveMenu();
        genericI = new ActiveMenuItem();
        linuxI = new ActiveMenuItem();
        linuxA32I = new ActiveMenuItem();
        linuxA64I = new ActiveMenuItem();
        win32I = new ActiveMenuItem();
        win64I = new ActiveMenuItem();
        macosI = new ActiveMenuItem();
        packDEM = new ActivePopupMenu();
        genericEP = new ActiveMenuItem();
        packPl = new ActivePopupMenu();
        distribPP = new ActiveMenuItem();
        cleanM = new ActivePopupMenu();
        cleanAllPM = new ActiveMenuItem();
        actionsAndroidM = new ActivePopupMenu();
        runAM = new ActiveMenuItem();
        buildAM = new ActiveMenuItem();
        logAM = new ActiveMenuItem();
        Background = new GradientPanel();
        controlP = new javax.swing.JPanel();
        controlP_R = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        projectB = new ActiveToggleButton("", null);
        expandPB = new ActiveButton();
        jPanel1 = new javax.swing.JPanel();
        outputB = new ActiveToggleButton("", null);
        expandOB = new ActiveButton();
        packB = new ActiveButton();
        openB = new ActiveButton();
        controlP_L = new javax.swing.JPanel();
        targetP = new javax.swing.JPanel();
        iosT = new ActiveToggleButton("", new ActiveIcon("images/ios_small"));
        androidT = new ActiveToggleButton("", new ActiveIcon("images/android_small"));
        uwpT = new ActiveToggleButton("", new ActiveIcon("images/uwp_small"));
        desktopT = new ActiveToggleButton("", new ActiveIcon("images/desktop_small"));
        commandP = new javax.swing.JPanel();
        expandRB = new ActiveButton();
        actionB = new ActiveButton();
        cleanP = new javax.swing.JPanel();
        expandCB = new ActiveButton();
        cleanB = new ActiveButton();
        contentP = new HiResPanel();
        projectP = new HiResPanel();
        parameters = new GradientPanel();
        rightPanel = new javax.swing.JPanel();
        leftButtonPanel = new javax.swing.JPanel();
        javax.swing.JPanel outputP = new HiResPanel();
        outerrorP = new javax.swing.JPanel();
        scrollOutP = new javax.swing.JScrollPane();
        outputTxt = new ActiveTextPane();
        infoP = new ActiveContextPanel();
        outResult = new ActiveLabel();
        idInfoP = new ActivePanel();
        pidL = new ActiveLabel();

        runM.setIcon(RUN_I);
        runM.setText("Run");
        runM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMActionPerformed(evt);
            }
        });
        actionsM.add(runM);

        buildM.setIcon(BUILD_I);
        buildM.setText("Build");
        buildM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildMActionPerformed(evt);
            }
        });
        actionsM.add(buildM);

        savePM.setIcon(SAVE_I);
        savePM.setText("Save");
        savePM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePMActionPerformed(evt);
            }
        });
        projectM.add(savePM);

        cleanOM.setIcon(CLEAN_TI);
        cleanOM.setText("Clean");
        cleanOM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanOMActionPerformed(evt);
            }
        });
        outputM.add(cleanOM);

        saveOM.setIcon(SAVE_TI);
        saveOM.setText("Save");
        saveOM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveOMActionPerformed(evt);
            }
        });
        outputM.add(saveOM);

        desktopM.setIcon(DESKTOP_I);
        desktopM.setText(" in " + SystemDependent.getFileManagerName());
        desktopM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopMActionPerformed(evt);
            }
        });
        openM.add(desktopM);
        openM.add(jSeparator4);

        intellijM.setIcon(INTELLIJ_I);
        intellijM.setText(" in IntelliJ IDEA");
        intellijM.setActionCommand(OPEN_INTELLIJ);
        intellijM.setDisabledIcon(INTELLIJ_D);
        intellijM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCommand(evt);
            }
        });
        openM.add(intellijM);

        netbeansM.setIcon(NETBEANS_I);
        netbeansM.setText(" in Netbeans");
        netbeansM.setActionCommand(OPEN_NETBEANS);
        netbeansM.setDisabledIcon(NETBEANS_D);
        netbeansM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCommand(evt);
            }
        });
        openM.add(netbeansM);
        openM.add(otherS);

        otherIDEs.setText("Other IDEs...");

        xcodeM.setIcon(XCODE_I);
        xcodeM.setText(" Xcode");
        xcodeM.setActionCommand(OPEN_XCODE);
        xcodeM.setDisabledIcon(XCODE_D);
        xcodeM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCommand(evt);
            }
        });
        otherIDEs.add(xcodeM);

        vstudioM.setIcon(VSTUDIO_I);
        vstudioM.setText(" Visual Studio");
        vstudioM.setActionCommand(OPEN_VSTUDIO);
        vstudioM.setDisabledIcon(VSTUDIO_D);
        vstudioM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCommand(evt);
            }
        });
        otherIDEs.add(vstudioM);

        studioM.setIcon(STUDIO_I);
        studioM.setText(" Android Studio");
        studioM.setActionCommand(OPEN_STUDIO);
        studioM.setDisabledIcon(STUDIO_D);
        studioM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCommand(evt);
            }
        });
        otherIDEs.add(studioM);

        openM.add(otherIDEs);

        nosupportedIP.setText("No supported packages for iOS");
        nosupportedIP.setEnabled(false);
        packIM.add(nosupportedIP);

        debugP.setText("as debug APK");
        debugP.setActionCommand("debug");
        debugP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                androidPackage(evt);
            }
        });
        packAM.add(debugP);

        releaseP.setText("as release APK");
        releaseP.setActionCommand("release");
        releaseP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                androidPackage(evt);
            }
        });
        packAM.add(releaseP);

        nosupportedWP.setText("No supported packages for UWP");
        nosupportedWP.setEnabled(false);
        packWM.add(nosupportedWP);

        filesOnlyM.setText("Files only");

        genericP.setText("as self-contained JAR");
        genericP.setActionCommand("generic");
        genericP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(genericP);

        linuxP.setText("for Linux");
        linuxP.setActionCommand("linux64");
        linuxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(linuxP);

        linuxA32P.setText("for ARM Linux 32 bit");
        linuxA32P.setActionCommand("linuxarm32");
        linuxA32P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(linuxA32P);

        linuxA64P.setText("for ARM Linux 64 bit");
        linuxA64P.setActionCommand("linuxarm64");
        linuxA64P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(linuxA64P);

        win32P.setText("for Windows 32 bit");
        win32P.setActionCommand("win32");
        win32P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(win32P);

        win64P.setText("fot Windows 64 bit");
        win64P.setActionCommand("win64");
        win64P.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(win64P);

        macosP.setText("for macOS");
        macosP.setActionCommand("macos");
        macosP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        filesOnlyM.add(macosP);

        packDMM.add(filesOnlyM);

        filesInstallerM.setText("Files and Installer");

        genericI.setText("as self-contained JAR");
        genericI.setActionCommand("generic");
        genericI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(genericI);

        linuxI.setText("for Linux");
        linuxI.setActionCommand("linux64");
        linuxI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(linuxI);

        linuxA32I.setText("for ARM Linux 32 bit");
        linuxA32I.setActionCommand("linuxarm32");
        linuxA32I.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(linuxA32I);

        linuxA64I.setText("for ARM Linux 64 bit");
        linuxA64I.setActionCommand("linuxarm64");
        linuxA64I.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(linuxA64I);

        win32I.setText("for Windows 32 bit");
        win32I.setActionCommand("win32");
        win32I.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(win32I);

        win64I.setText("fot Windows 64 bit");
        win64I.setActionCommand("win64");
        win64I.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(win64I);

        macosI.setText("for macOS");
        macosI.setActionCommand("macos");
        macosI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopInstaller(evt);
            }
        });
        filesInstallerM.add(macosI);

        packDMM.add(filesInstallerM);

        genericEP.setText("as self-contained JAR");
        genericEP.setActionCommand("generic");
        genericEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desktopPackage(evt);
            }
        });
        packDEM.add(genericEP);

        distribPP.setText("Distribution package not supported yet");
        distribPP.setEnabled(false);
        distribPP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distribPPActionPerformed(evt);
            }
        });
        packPl.add(distribPP);

        cleanAllPM.setIcon(CLEANPROJ_I);
        cleanAllPM.setText("Clean Project files");
        cleanAllPM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanAllPMActionPerformed(evt);
            }
        });
        cleanM.add(cleanAllPM);

        runAM.setIcon(RUN_I);
        runAM.setText("Run");
        runAM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMActionPerformed(evt);
            }
        });
        actionsAndroidM.add(runAM);

        buildAM.setIcon(BUILD_I);
        buildAM.setText("Build");
        buildAM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildMActionPerformed(evt);
            }
        });
        actionsAndroidM.add(buildAM);

        logAM.setIcon(OUTPUT_I);
        logAM.setText("Device Log");
        logAM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logMActionPerformed(evt);
            }
        });
        actionsAndroidM.add(logAM);

        setMinimumSize(new java.awt.Dimension(840, 620));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        Background.setLayout(new java.awt.BorderLayout());

        controlP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.gray));
        controlP.setOpaque(false);
        controlP.setLayout(new java.awt.BorderLayout());

        controlP_R.setOpaque(false);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        projectG.add(projectB);
        projectB.setIcon(PROJECT_I);
        projectB.setSelected(true);
        projectB.setText("Project");
        projectB.setActionCommand(LAUNCH_TARGET_IOS);
        projectB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 8, 8));
        projectB.setDisabledIcon(PROJECT_D);
        projectB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectBtargetSelection(evt);
            }
        });
        jPanel2.add(projectB, java.awt.BorderLayout.CENTER);

        expandPB.setIcon(EXPAND_I);
        expandPB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 2));
        expandPB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                expandPBMousePressed(evt);
            }
        });
        jPanel2.add(expandPB, java.awt.BorderLayout.WEST);

        controlP_R.add(jPanel2);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout());

        projectG.add(outputB);
        outputB.setIcon(OUTPUT_I);
        outputB.setText("Output");
        outputB.setActionCommand(LAUNCH_TARGET_IOS);
        outputB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 8, 8));
        outputB.setDisabledIcon(OUTPUT_D);
        outputB.setEnabled(false);
        outputB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputBtargetSelection(evt);
            }
        });
        jPanel1.add(outputB, java.awt.BorderLayout.CENTER);

        expandOB.setIcon(EXPAND_I);
        expandOB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 2));
        expandOB.setEnabled(false);
        expandOB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                expandOBMousePressed(evt);
            }
        });
        jPanel1.add(expandOB, java.awt.BorderLayout.WEST);

        controlP_R.add(jPanel1);

        packB.setIcon(PACK_I);
        packB.setText("Pack");
        packB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 8));
        packB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                packBMousePressed(evt);
            }
        });
        controlP_R.add(packB);

        openB.setIcon(OPEN_I);
        openB.setText("Open");
        openB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 8));
        openB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openBMousePressed(evt);
            }
        });
        controlP_R.add(openB);

        controlP.add(controlP_R, java.awt.BorderLayout.EAST);

        controlP_L.setToolTipText("");
        controlP_L.setOpaque(false);

        targetP.setOpaque(false);
        targetP.setLayout(new java.awt.GridLayout(1, 0));

        targetG.add(iosT);
        iosT.setToolTipText("iOS Project");
        iosT.setActionCommand(LAUNCH_TARGET_IOS);
        iosT.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        iosT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetSelection(evt);
            }
        });
        targetP.add(iosT);

        targetG.add(androidT);
        androidT.setToolTipText("Android Project");
        androidT.setActionCommand(LAUNCH_TARGET_ANDROID);
        androidT.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        androidT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetSelection(evt);
            }
        });
        targetP.add(androidT);

        targetG.add(uwpT);
        uwpT.setToolTipText("Universal Windows Platform Project");
        uwpT.setActionCommand(LAUNCH_TARGET_UWP);
        uwpT.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        uwpT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetSelection(evt);
            }
        });
        targetP.add(uwpT);

        targetG.add(desktopT);
        desktopT.setToolTipText("Desktop Project");
        desktopT.setActionCommand(LAUNCH_TARGET_DESKTOP);
        desktopT.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        desktopT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetSelection(evt);
            }
        });
        targetP.add(desktopT);

        controlP_L.add(targetP);

        commandP.setOpaque(false);
        commandP.setLayout(new java.awt.BorderLayout());

        expandRB.setIcon(EXPAND_I);
        expandRB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 2));
        expandRB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                expandRBMousePressed(evt);
            }
        });
        commandP.add(expandRB, java.awt.BorderLayout.WEST);

        actionB.setIcon(RUN_I);
        actionB.setText("Run");
        actionB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 8, 8));
        actionB.setDisabledIcon(STOP_D);
        actionB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actOnProject(evt);
            }
        });
        commandP.add(actionB, java.awt.BorderLayout.CENTER);

        controlP_L.add(commandP);

        cleanP.setOpaque(false);
        cleanP.setLayout(new java.awt.BorderLayout());

        expandCB.setIcon(EXPAND_I);
        expandCB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 2));
        expandCB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                expandCBMousePressed(evt);
            }
        });
        cleanP.add(expandCB, java.awt.BorderLayout.WEST);

        cleanB.setIcon(CLEAN_I);
        cleanB.setText("Clean");
        cleanB.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 8, 8));
        cleanB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanBactOnProject(evt);
            }
        });
        cleanP.add(cleanB, java.awt.BorderLayout.CENTER);

        controlP_L.add(cleanP);

        controlP.add(controlP_L, java.awt.BorderLayout.WEST);

        Background.add(controlP, java.awt.BorderLayout.NORTH);

        contentP.setOpaque(false);
        contentP.setLayout(new java.awt.CardLayout());

        projectP.setOpaque(false);
        projectP.setLayout(new java.awt.BorderLayout());

        parameters.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.lightGray));
        parameters.setOpaque(false);
        parameters.setLayout(new java.awt.CardLayout());
        projectP.add(parameters, java.awt.BorderLayout.CENTER);

        rightPanel.setOpaque(false);
        rightPanel.setLayout(new java.awt.BorderLayout());

        leftButtonPanel.setOpaque(false);
        leftButtonPanel.setLayout(new java.awt.GridLayout(0, 1));
        rightPanel.add(leftButtonPanel, java.awt.BorderLayout.CENTER);

        projectP.add(rightPanel, java.awt.BorderLayout.EAST);

        contentP.add(projectP, "project");

        outputP.setOpaque(false);
        outputP.setLayout(new java.awt.BorderLayout());

        outerrorP.setLayout(new java.awt.BorderLayout());

        scrollOutP.setName("out"); // NOI18N

        outputTxt.setEditable(false);
        scrollOutP.setViewportView(outputTxt);

        outerrorP.add(scrollOutP, java.awt.BorderLayout.CENTER);

        outputP.add(outerrorP, java.awt.BorderLayout.CENTER);

        infoP.setLayout(new java.awt.BorderLayout());

        outResult.setBorder(new com.panayotis.hrgui.HiResEmptyBorder(4, 8, 4, 0));
        infoP.add(outResult, java.awt.BorderLayout.CENTER);

        idInfoP.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 8));
        idInfoP.setOpaque(false);
        idInfoP.setLayout(new java.awt.BorderLayout());
        idInfoP.add(pidL, java.awt.BorderLayout.EAST);

        infoP.add(idInfoP, java.awt.BorderLayout.EAST);

        outputP.add(infoP, java.awt.BorderLayout.SOUTH);

        contentP.add(outputP, "output");

        Background.add(contentP, java.awt.BorderLayout.CENTER);

        getContentPane().add(Background, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void actOnProject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actOnProject
        buildAndRun(getCurrentTarget());
    }//GEN-LAST:event_actOnProject

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        String name = "--unknown--";
        if (proj != null)
            try {
                name = proj.getProperty(DISPLAY_NAME);
                if (proj != null) {
                    if (!proj.isSaved() && JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Project " + name + " is not saved.\nDo you want to save it before proceeding?",
                            name + " Project",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
                        try {
                            proj.save();
                        } catch (ProjectException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage(), "Project " + name + " error", JOptionPane.ERROR_MESSAGE);
                        }
                    Opt.of(launch).ifExists(Commander::kill);
                    Opt.of(closeCallback).ifExists(c -> c.accept(proj));
                }
                closeCallback = null;
            } catch (Throwable th) {
                String projectName = proj == null ? "" : " for project " + name;
                Log.error("A serious runtime error has occurred" + projectName, th);
            }
    }//GEN-LAST:event_formWindowClosing

    private void buildMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildMActionPerformed
        actionB.setActionCommand(LAUNCH_ACTION_BUILD);
        updateLaunchVisuals();
        buildAndRun(getCurrentTarget());
    }//GEN-LAST:event_buildMActionPerformed

    private void runMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runMActionPerformed
        actionB.setActionCommand(LAUNCH_ACTION_RUN);
        updateLaunchVisuals();
        buildAndRun(getCurrentTarget());
    }//GEN-LAST:event_runMActionPerformed

    private void targetSelection(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetSelection
        Prefs.setSelectedLaunchTarget(proj.getPath().getAbsolutePath(), evt.getActionCommand());
    }//GEN-LAST:event_targetSelection

    private void expandRBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expandRBMousePressed
        if (expandRB.isEnabled()) {
            switch (getCurrentTarget()) {
                case "android":
                    actionsAndroidM.show(expandRB, 0, expandRB.getHeight());
                    break;
                default:
                    actionsM.show(expandRB, 0, expandRB.getHeight());
                    break;
            }
        }
    }//GEN-LAST:event_expandRBMousePressed

    private void openBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openBMousePressed
        if (openB.isEnabled()) {
            netbeansM.setEnabled(!Prefs.getNetbeansLocation().isEmpty());
            intellijM.setEnabled(!Prefs.getIntelliJLocation().isEmpty());
            studioM.setEnabled(isProjectEnabled && !Prefs.getAndroidStudioLocation().isEmpty());
            vstudioM.setEnabled(isProjectEnabled && !Prefs.getVisualStudioLocation().isEmpty());
            xcodeM.setEnabled(isProjectEnabled && SystemDependent.canMakeIos());
            openM.show(openB, 0, openB.getHeight());
        }
    }//GEN-LAST:event_openBMousePressed

    private void openCommand(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCommand
        openTarget(evt.getActionCommand());
    }//GEN-LAST:event_openCommand

    private void projectBtargetSelection(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectBtargetSelection
        displayProject();
    }//GEN-LAST:event_projectBtargetSelection

    private void outputBtargetSelection(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputBtargetSelection
        displayOutput();
    }//GEN-LAST:event_outputBtargetSelection

    private void cleanOMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanOMActionPerformed
        outputTxt.setText("");
    }//GEN-LAST:event_cleanOMActionPerformed

    private void saveOMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveOMActionPerformed
        saveOutPut();
    }//GEN-LAST:event_saveOMActionPerformed

    private void savePMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePMActionPerformed
        saveProjectWithErrorMessage();
    }//GEN-LAST:event_savePMActionPerformed

    private void expandOBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expandOBMousePressed
        if (expandOB.isEnabled())
            outputM.show(expandOB, 0, expandOB.getHeight());
    }//GEN-LAST:event_expandOBMousePressed

    private void expandPBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expandPBMousePressed
        if (expandPB.isEnabled())
            projectM.show(expandPB, 0, expandPB.getHeight());
    }//GEN-LAST:event_expandPBMousePressed

    private void desktopMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desktopMActionPerformed
        try {
            Desktop.getDesktop().open(proj.getPath());
        } catch (IOException ignored) {
        }
    }//GEN-LAST:event_desktopMActionPerformed

    private void cleanBactOnProject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanBactOnProject
        launchMaven("clean", null, new MavenExecInfo("Clean up project", "Cleaning project", null), this::mavenFeedback);
    }//GEN-LAST:event_cleanBactOnProject

    private void expandCBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expandCBMousePressed
        if (expandCB.isEnabled())
            cleanM.show(expandCB, 0, expandCB.getHeight());
    }//GEN-LAST:event_expandCBMousePressed

    private void cleanAllPMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanAllPMActionPerformed
        launchMaven("clean", "distclean", new MavenExecInfo("Clean project and build files", "Clean project", null),
                onSuccess(this::saveProjectWithErrorMessage));
    }//GEN-LAST:event_cleanAllPMActionPerformed

    private void logMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logMActionPerformed
        java.util.List<String> cmds = new ArrayList<>();
        cmds.add(Prefs.getAndroidSDKLocation() + File.separator + "platform-tools" + File.separator + ADB.filename());
        cmds.add("logcat");
        cmds.add("-s");
        cmds.add("CrossMob:*");
        switch (proj.getDebugProfile()) {
            case "full":
                cmds.add("AndroidRuntime:E");
            case "outerr":
                cmds.add("System.out:I");
            case "err":
                cmds.add("System.err:W");
        }
        initLaunchVisualsOut(new MavenExecInfo("Display Android Logs", "Android logs", "android"));
        launch = ProjectLauncher.launch(null, getTextPane(), this::mavenFeedback, null,
                StreamListener.NONE, cmds.toArray(new String[0]));
    }//GEN-LAST:event_logMActionPerformed

    private void desktopPackage(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desktopPackage
        createDesktopPackage(evt.getActionCommand(), false);
    }//GEN-LAST:event_desktopPackage

    private void androidPackage(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_androidPackage
        boolean release = "release".equals(evt.getActionCommand());
        buildAndRun("android", true, release, false, res -> Opt.of(getApkPath(true))
                .onError(Log::error)
                .ifExists(f -> Desktop.getDesktop().open(f.getParentFile())).always(i -> mavenFeedback(res)));
    }//GEN-LAST:event_androidPackage

    private void packBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_packBMousePressed
        if (packB.isEnabled())
            if (proj.isPlugin())
                packPl.show(packB, 0, packB.getHeight());
            else
                switch (getCurrentTarget()) {
                    case "android":
                        packAM.show(packB, 0, packB.getHeight());
                        break;
                    case "desktop":
                        if (Paths.getMakeAppExec().isEmpty())
                            packDEM.show(packB, 0, packB.getHeight());
                        else
                            packDMM.show(packB, 0, packB.getHeight());
                        break;
                    case "ios":
                        packIM.show(packB, 0, packB.getHeight());
                        break;
                    case "uwp":
                        packWM.show(packB, 0, packB.getHeight());
                        break;
                }
    }//GEN-LAST:event_packBMousePressed

    private void desktopInstaller(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desktopInstaller
        createDesktopPackage(evt.getActionCommand(), true);
    }//GEN-LAST:event_desktopInstaller

    private void distribPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distribPPActionPerformed
        System.out.println("Create distribution package not supported yet");
    }//GEN-LAST:event_distribPPActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Background;
    private javax.swing.JButton actionB;
    private javax.swing.JPopupMenu actionsAndroidM;
    private javax.swing.JPopupMenu actionsM;
    private javax.swing.JToggleButton androidT;
    private javax.swing.JMenuItem buildAM;
    private javax.swing.JMenuItem buildM;
    private javax.swing.JMenuItem cleanAllPM;
    private javax.swing.JButton cleanB;
    private javax.swing.JPopupMenu cleanM;
    private javax.swing.JMenuItem cleanOM;
    private javax.swing.JPanel cleanP;
    private javax.swing.JPanel commandP;
    private javax.swing.JPanel contentP;
    private javax.swing.JPanel controlP;
    private javax.swing.JPanel controlP_L;
    private javax.swing.JPanel controlP_R;
    private javax.swing.JMenuItem debugP;
    private javax.swing.JMenuItem desktopM;
    private javax.swing.JToggleButton desktopT;
    private javax.swing.JMenuItem distribPP;
    private javax.swing.JButton expandCB;
    private javax.swing.JButton expandOB;
    private javax.swing.JButton expandPB;
    private javax.swing.JButton expandRB;
    private javax.swing.JMenu filesInstallerM;
    private javax.swing.JMenu filesOnlyM;
    private javax.swing.JMenuItem genericEP;
    private javax.swing.JMenuItem genericI;
    private javax.swing.JMenuItem genericP;
    private javax.swing.JPanel idInfoP;
    private javax.swing.JPanel infoP;
    private javax.swing.JMenuItem intellijM;
    private javax.swing.JToggleButton iosT;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPanel leftButtonPanel;
    private javax.swing.JMenuItem linuxA32I;
    private javax.swing.JMenuItem linuxA32P;
    private javax.swing.JMenuItem linuxA64I;
    private javax.swing.JMenuItem linuxA64P;
    private javax.swing.JMenuItem linuxI;
    private javax.swing.JMenuItem linuxP;
    private javax.swing.JMenuItem logAM;
    private javax.swing.JMenuItem macosI;
    private javax.swing.JMenuItem macosP;
    private javax.swing.JMenuItem netbeansM;
    private javax.swing.JMenuItem nosupportedIP;
    private javax.swing.JMenuItem nosupportedWP;
    private javax.swing.JButton openB;
    private javax.swing.JPopupMenu openM;
    private javax.swing.JMenu otherIDEs;
    private javax.swing.JPopupMenu.Separator otherS;
    private javax.swing.JLabel outResult;
    private javax.swing.JPanel outerrorP;
    private javax.swing.JToggleButton outputB;
    private javax.swing.JPopupMenu outputM;
    private javax.swing.JTextPane outputTxt;
    private javax.swing.JPopupMenu packAM;
    private javax.swing.JButton packB;
    private javax.swing.JPopupMenu packDEM;
    private javax.swing.JPopupMenu packDMM;
    private javax.swing.JPopupMenu packIM;
    private javax.swing.JPopupMenu packPl;
    private javax.swing.JPopupMenu packWM;
    private javax.swing.JPanel parameters;
    private javax.swing.JLabel pidL;
    private javax.swing.JToggleButton projectB;
    private javax.swing.ButtonGroup projectG;
    private javax.swing.JPopupMenu projectM;
    private javax.swing.JPanel projectP;
    private javax.swing.JMenuItem releaseP;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JMenuItem runAM;
    private javax.swing.JMenuItem runM;
    private javax.swing.JMenuItem saveOM;
    private javax.swing.JMenuItem savePM;
    private javax.swing.JScrollPane scrollOutP;
    private javax.swing.JMenuItem studioM;
    private javax.swing.ButtonGroup targetG;
    private javax.swing.JPanel targetP;
    private javax.swing.JToggleButton uwpT;
    private javax.swing.JMenuItem vstudioM;
    private javax.swing.JMenuItem win32I;
    private javax.swing.JMenuItem win32P;
    private javax.swing.JMenuItem win64I;
    private javax.swing.JMenuItem win64P;
    private javax.swing.JMenuItem xcodeM;
    // End of variables declaration//GEN-END:variables

}
