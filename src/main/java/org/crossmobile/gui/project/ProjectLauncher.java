/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.gui.actives.ActiveTextPane;
import org.crossmobile.gui.utils.StreamListener;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.Commander;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ProjectLauncher {

    public static Map<String, String> getJavaEnv() {
        Map<String, String> javaMap = new HashMap<>();
        javaMap.put("JAVA_HOME", Prefs.getJDKLocation());
        return javaMap;
    }

    public static Commander launch(Project proj, String... command) {
        return launch(proj.getPath(), null, null, getJavaEnv(), null, command);
    }

    public static Commander launch(File currentDir, final ActiveTextPane out, final Consumer<Integer> result,
                                   Map<String, String> env, StreamListener extraListener, String... command) {
        Commander cmd = new Commander(command);
        if (currentDir != null)
            cmd.setCurrentDir(currentDir);
        if (out != null) {
            initializeTextPane(out, extraListener);
            cmd.setCharOutListener(out.getStreamManager()::incomingOutChar);
            cmd.setCharErrListener(out.getStreamManager()::incomingErrChar);
        }
        if (env != null)
            for (String key : env.keySet())
                cmd.appendEnvironmentalParameter(key, env.get(key));
        if (result != null)
            cmd.setEndListener(result);
        cmd.setDebug(true);
        cmd.exec();
        return cmd;
    }

    private static void initializeTextPane(ActiveTextPane atp, StreamListener extraListener) {
        atp.getStreamManager().clearListeners();
        atp.getStreamManager().addListener(atp);
        if (extraListener != null)
            atp.getStreamManager().addListener(extraListener);
    }

}
