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
}
