/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import com.panayotis.hrgui.HiResOptions;
import org.crossmobile.Version;
import org.crossmobile.gui.actives.ActiveTextPane;
import org.crossmobile.gui.android.InstallerFrame;
import org.crossmobile.gui.project.ProjectLauncher;
import org.crossmobile.prefs.Config;
import org.crossmobile.prefs.LaunchTarget;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.Commander;
import org.crossmobile.utils.FileUtils;

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
        public final LaunchTarget target;

        public MavenExecInfo(String consoleText, String infoText, LaunchTarget target) {
            this.consoleText = consoleText == null ? "" : consoleText;
            this.infoText = infoText == null ? "" : infoText;
            this.target = target;
        }
    }

    public enum Clean {
        NOCLEAN(false, null), CLEAN(true, null), DISTCLEAN(true, "-Pdistclean");
        public final boolean shouldClean;
        public final String cleanTarget;

        Clean(boolean shouldClean, String cleanTarget) {
            this.shouldClean = shouldClean;
            this.cleanTarget = cleanTarget;
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
            if (LaunchTarget.profilesSupportDebugging(profiles))
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
                solutionCallbackRef.set(() -> new HiResOptions().message("A Certification exception was found\n\n"
                        + "You might need to upgrade your JDK 8 version beyond " + Config.MIN_JAVA_VERSION_FULL + ",\n"
                        + "or else Maven resolving issues will occur.").
                        title("Error while executing Java target").error().show());
            else if (line.contains("accept the SDK license agreements"))
                solutionCallbackRef.set(() -> {
                    if (foundOldVersion.get() && Prefs.isAndroidLicenseLocationValid()) {
                        new HiResOptions().message("The Android License seems to be accepted,\nbut build tools refer to older versions.\n" +
                                "\nIt might help to \"Clean Project files\" and try again.").show();
                    } else {
                        String BaseText = "Android SDK license not accepted yet\n\n"
                                + "In order to build Android projects, the Android SDK license\n"
                                + "should be accepted.\n";
                        String Title = "Error while building Android project";
                        if (!FileUtils.isWritable(new File(Prefs.getAndroidSDKLocation()))) {
                            new HiResOptions().message(BaseText + "\nThe provided SDK location at:\n"
                                    + Prefs.getAndroidSDKLocation() + "\nis not writable.\n\nPlease accept the license agreement and relaunch "
                                    + "the build procedure.").
                                    title(Title).error().show();
                        } else if (Prefs.getAndroidSDKManagerLocation().isEmpty()) {
                            new HiResOptions().message(BaseText + "\nUnable to locate the sdkmanager tool.\n\n" +
                                    "Please use the Android Studio to accept the license agreement\nand then relauch the build procedure.").
                                    title(Title).error().show();
                        } else if (new HiResOptions().message(BaseText
                                + "Do you want to accept the Android license now?\n\n"
                                + "Note that after accepting, you will need to relaunch\n"
                                + "the build procedure.").
                                title(Title).buttons("Yes", "No").warning().show() == 0)
                            new InstallerFrame().launch();
                    }
                });
            else if (line.contains("SDK location not found"))
                solutionCallbackRef.set(() -> new HiResOptions().message("Android SDK location is required\n\n"
                        + "Please rerun the initialization wizard first\nand define the Android SDK location.").
                        title("Error while locating Android SDK").error().show());
            else if (line.contains("No value has been specified for property 'signingConfig"))
                solutionCallbackRef.set(() -> new HiResOptions().message("No keystore passwords found\n\n"
                        + "Please provide the Keystore/Alias password to sign the APK\nunder the Android preferences").
                        title("Error signing Android APK").error().show());
            else if (line.contains("[INSTALL_FAILED"))
                solutionCallbackRef.set(() -> new HiResOptions().message(line.substring(line.indexOf("[INSTALL_FAILED") + 1, line.length() - 1).replace(":", "\n")).
                        title("Error installing Android APK").error().show());
            else if (line.contains("xcode-select: error: tool 'xcodebuild' requires Xcode"))
                solutionCallbackRef.set(() -> new HiResOptions().message("XCode tools not properly installed\n\n" +
                        "You probably need to install and activate Command Line Tools,\n" +
                        "or use xcode-select with a command similar to:\n" +
                        "sudo xcode-select -s /Applications/Xcode.app/Contents/Developer").
                        title("Command Line Tools problem").error().show());

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
     * @return The produced Commander
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

    /**
     * @param jarFile the artifact file to be installed
     * @param pomFile the pom file of this artifact
     * @return The produced Commander
     */
    public static Commander installArtifact(File jarFile, File pomFile, File sourcesFile) {
        Commander cprojCmd = new Commander(Paths.getMavenExec(),
                "org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file",
                "-B",
                "-Dfile=" + jarFile.getAbsolutePath(),
                "-DpomFile=" + pomFile.getAbsolutePath(),
                sourcesFile == null ? null : "-Dsources=" + sourcesFile.getAbsolutePath());
        cprojCmd.setCurrentDir(jarFile.getParentFile());
        cprojCmd.appendEnvironmentalParameter("JAVA_HOME", Prefs.getJDKLocation());
        return cprojCmd;
    }

}
