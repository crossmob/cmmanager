/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import org.crossmobile.Version;
import org.crossmobile.gui.actives.ActiveTextPane;
import org.crossmobile.gui.android.InstallerFrame;
import org.crossmobile.gui.project.ProjectLauncher;
import org.crossmobile.prefs.Config;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.Commander;
import org.crossmobile.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.crossmobile.utils.NumberUtils.safeInt;
import static org.crossmobile.utils.NumberUtils.safeLong;

public class CMMvnActions {
    public interface MavenExecutor {
        void launchMaven(String goal, String profiles, MavenExecInfo info, Consumer<Integer> launchCallback, String... params);

        void mavenFeedback(int result);

        default Consumer<Integer> onSuccess(Runnable success) {
            return result -> {
                if (result == 0)
                    success.run();
                mavenFeedback(result);
            };
        }
    }

    public static final class MavenExecInfo {
        public final String consoleText;
        public final String infoText;
        public final String target;

        public MavenExecInfo(String consoleText, String infoText, String target) {
            this.consoleText = consoleText == null ? "" : consoleText;
            this.infoText = infoText == null ? "" : infoText;
            this.target = target == null ? "" : target;
        }
    }

    private static final Pattern PID_PATTERN = Pattern.compile("PID ([0-9]*) uses (.*) port.*");

    public static Commander callMaven(String goal, String profiles, File projPath, ActiveTextPane outP,
                                      Consumer<Long> pidConsumer, Consumer<Integer> debugPort, Consumer<Integer> launchCallback,
                                      AtomicReference<Runnable> solutionCallbackRef,
                                      Profile profile, String... params) {
        List<String> cmd = new ArrayList<>();
        cmd.add(Paths.getMavenExec());
        if (profiles != null) {
            cmd.add("-P");
            cmd.add(profiles);
        }
        cmd.add(goal);
        cmd.add("-B");
        Map<String, String> env = ProjectLauncher.getJavaEnv();
        if (profile.isDebug()) {
            cmd.add("-e");
            String agent = profile == Profile.XRAY ? Paths.getXRayPath() : null;
            agent = agent == null ? "" : "-javaagent:" + agent + " ";
            if (profiles != null && profiles.contains("desktop"))
                env.put("MAVEN_OPTS", agent + "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0");
        }
        if (params != null)
            for (String param : params)
                if (param != null)
                    cmd.add(param);

        AtomicBoolean foundOldVersion = new AtomicBoolean(false);
        return ProjectLauncher.launch(projPath, outP, launchCallback, env, (seqLine, quality) -> {
            String line = seqLine.toString();
            if (line.contains("platforms;android-"))
                foundOldVersion.set(true);
            if (line.contains("sun.security.provider.certpath.SunCertPathBuilderException"))
                solutionCallbackRef.set(() -> JOptionPane.showMessageDialog(null, "A Certification exception was found\n\n"
                                + "You might need to upgrade your JDK 8 version beyond " + Config.MIN_JAVA_VERSION_FULL + ",\n"
                                + "or else Maven resolving issues will occur.",
                        "Error while executing Java target", JOptionPane.ERROR_MESSAGE));
            else if (line.contains("accept the SDK license agreements"))
                solutionCallbackRef.set(() -> {
                    if (foundOldVersion.get() && Prefs.isAndroidLicenseLocationValid()) {
                        JOptionPane.showMessageDialog(null, "The Android License seems to be accepted,\nbut build tools refer to older versions.\n" +
                                "\nIt might help to \"Clean Project files\" and try again.");
                    } else {
                        String BaseText = "Android SDK license not accepted yet\n\n"
                                + "In order to build Android projects, the Android SDK license\n"
                                + "should be accepted.\n";
                        String Title = "Error while building Android project";
                        if (!FileUtils.isWritable(new File(Prefs.getAndroidSDKLocation()))) {
                            JOptionPane.showMessageDialog(null, BaseText + "\nThe provided SDK location at:\n"
                                            + Prefs.getAndroidSDKLocation() + "\nis not writable.\n\nPlease accept the license agreement and relaunch "
                                            + "the build procedure.",
                                    Title, JOptionPane.ERROR_MESSAGE);
                        } else if (Prefs.getAndroidSDKManagerLocation().isEmpty()) {
                            JOptionPane.showMessageDialog(null, BaseText + "\nUnable to locate the sdkmanager tool.\n\n" +
                                            "Please use the Android Studio to accept the license agreement\nand then relauch the build procedure.",
                                    Title, JOptionPane.ERROR_MESSAGE);
                        } else if (JOptionPane.showConfirmDialog(null, BaseText
                                        + "Do you want to accept the Android license now?\n\n"
                                        + "Note that after accepting, you will need to relaunch\n"
                                        + "the build procedure.",
                                Title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION)
                            new InstallerFrame().launch();
                    }
                });
            else if (line.contains("SDK location not found"))
                solutionCallbackRef.set(() -> JOptionPane.showMessageDialog(null, "Android SDK location is required\n\n"
                                + "Please rerun the initialization wizard first\nand define the Android SDK location.",
                        "Error while locating Android SDK", JOptionPane.ERROR_MESSAGE));
            else if (line.contains("No value has been specified for property 'signingConfig"))
                solutionCallbackRef.set(() -> JOptionPane.showMessageDialog(null, "No keystore passwords found\n\n"
                                + "Please provide the Keystore/Alias password to sign the APK\nunder the Android preferences",
                        "Error signing Android APK", JOptionPane.ERROR_MESSAGE));
            else if (line.contains("[INSTALL_FAILED"))
                solutionCallbackRef.set(() -> JOptionPane.showMessageDialog(null,
                        line.substring(line.indexOf("[INSTALL_FAILED") + 1, line.length() - 1).replace(":", "\n"),
                        "Error installing Android APK", JOptionPane.ERROR_MESSAGE));
            else if (line.contains("xcode-select: error: tool 'xcodebuild' requires Xcode"))
                solutionCallbackRef.set(() -> JOptionPane.showMessageDialog(null,
                        "XCode tools not properly installed\n\nYou probably need to install and\n" +
                                "activate Command Line Tools,\nor use xcode-select with a command similar to:\nsudo xcode-select -s /Applications/Xcode.app/Contents/Developer",
                        "Command Line Tools problem", JOptionPane.ERROR_MESSAGE));

            if (debugPort != null) {
                if (line.startsWith("Listening for transport dt_socket at address: "))
                    safeInt(line.substring(line.indexOf(':') + 1).trim(), debugPort);
                else {
                    Matcher pidMatcher = PID_PATTERN.matcher(line);
                    if (pidMatcher.matches()) {
                        safeInt(pidMatcher.group(2), debugPort);
                        safeLong(pidMatcher.group(1), pidConsumer);
                    }
                }
            }
        }, cmd.toArray(new String[0]));
    }

    /**
     * @param archetype  archetype name (i.e. base or empty)
     * @param artifactId artifactId of the project to be created
     * @param groupId    groupId of the project to be created
     * @param version    version of the project to be
     * @param projectDir root directory o the project to be
     * @param name       Display name
     * @return
     */
    public static Commander createProject(String archetype, String name, String artifactId, String groupId, String version, File projectDir) {
        Commander cprojCmd = new Commander(Paths.getMavenExec(),
                "org.apache.maven.plugins:maven-archetype-plugin:2.4:generate",
                "-B",
                "-DarchetypeGroupId=org.crossmobile",
                "-DarchetypeArtifactId=cmarchetype-" + archetype,
                "-DarchetypeVersion=" + Version.VERSION,
                "-DgroupId=" + groupId,
                "-DartifactId=" + artifactId,
                "-Dversion=" + version,
                "-Dname=" + name,
                "-DarchetypeRepository=https://mvn.crossmobile.org/content/repositories/crossmobile/");
        cprojCmd.setCurrentDir(projectDir.getParentFile());
        cprojCmd.appendEnvironmentalParameter("JAVA_HOME", Prefs.getJDKLocation());
        return cprojCmd;
    }

}
