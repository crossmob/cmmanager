/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import org.crossmobile.gui.actives.ActiveTextPane;
import org.crossmobile.gui.project.Project;
import org.crossmobile.gui.project.ProjectLauncher;
import org.crossmobile.prefs.LaunchTarget;
import org.crossmobile.prefs.Prefs;

import java.io.File;
import java.util.function.Consumer;

import static org.crossmobile.utils.ParamsCommon.ARTIFACT_ID;

public class ExternalCommands {

    public static void openCode(LaunchTarget ide, Project proj, ActiveTextPane txtPane, Consumer<Integer> launchCallback) {
        String[] args;
        switch (ide) {
            case Netbeans:
                args = new String[]{Prefs.getNetbeansLocation(),
                        "--open",
                        proj.getPath().getAbsolutePath()};
                break;
            case IntelliJ_IDEA:
                args = new String[]{Prefs.getIntelliJLocation(),
                        proj.getPom().getAbsolutePath()};
                break;
            case VS_Code:
                args = new String[]{Prefs.getVSCodeLocation(),
                        proj.getPath().getAbsolutePath()};
                break;
            case Android_Studio:
                args = new String[]{Prefs.getAndroidStudioLocation(),
                        proj.getPath().getAbsolutePath()};
                break;
            case Xcode:
                args = new String[]{"/usr/bin/open",
                        proj.getPath().getAbsolutePath() + File.separator + proj.getProperty(ARTIFACT_ID) + ".xcodeproj"};
                break;
            case VStudio:
                args = new String[]{"cmd.exe", "/C", "start",
                        proj.getPath().getAbsolutePath() + File.separator + proj.getProperty(ARTIFACT_ID) + "-WinStore10.sln"};
                break;
            default:
                txtPane.appendText("\nNo IDE is selected.\n", null);
                launchCallback.accept(-1);
                return;
        }
        if (!new File(args[0]).isFile()) {
            txtPane.appendText("\nUnable to locate '" + args[0] + "' when invoking " + ide + ", probably a misconfiguration error.\n", null);
            launchCallback.accept(-1);
            return;
        }
        ProjectLauncher.launch(proj, args);
        launchCallback.accept(0);
    }

    public static void convertAARtoJAR(String signature) {
        System.out.println("convert to AAR");
    }
}
