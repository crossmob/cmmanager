/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.init;

import org.crossmobile.utils.SystemDependent;

import javax.swing.*;
import java.awt.*;

import static org.crossmobile.prefs.Config.JAVA_RANGE;
import static org.crossmobile.prefs.Config.MIN_JAVA_VERSION_FULL;
import static org.crossmobile.prefs.Prefs.getAndroidSDKLocation;
import static org.crossmobile.prefs.Prefs.getJDKLocation;

public class ApplicationRequirements {

    public static boolean isAndroidConfigured() {
        return !getAndroidSDKLocation().isEmpty();
    }

    public static boolean isJDKconfigured() {
        return !getJDKLocation().isEmpty();
    }

    public static void checkJavaVersion(Component relative, Runnable andAfter) {
        new Thread(() -> {
            try {
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().wait(1000);
                }
            } catch (InterruptedException ignored) {
            }
            if (!SystemDependent.isJavaValid(System.getProperty("java.version")))
                JOptionPane.showOptionDialog(relative, "<html><b>Incorrect java version, required: " + JAVA_RANGE + "</b>.\n\nIt is strongly recommended to update at least\nto JDK version " + MIN_JAVA_VERSION_FULL + " for security reasons.\n\nPlease update your system to a newer Java version,\nor you might experience connectivity issues.\n\n"
                        + "Current Java version: " + System.getProperty("java.version"), "CrossMobile", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"I will"}, 0);
            if (andAfter != null)
                andAfter.run();
        }).start();
    }

}
