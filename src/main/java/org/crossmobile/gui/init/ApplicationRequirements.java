/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.init;

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
