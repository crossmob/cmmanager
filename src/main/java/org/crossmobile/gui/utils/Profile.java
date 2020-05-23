/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

public enum Profile {
    DEBUG, XRAY(Paths.getXRayPath() != null), RELEASE, OBFUSCATE;
    private boolean valid;

    Profile() {
        this(true);
    }

    Profile(boolean valid) {
        this.valid = valid;
    }

    public static Profile safeValueOf(String launchType) {
        if (launchType == null)
            return null;
        launchType = launchType.toUpperCase();
        try {
            return Profile.valueOf(launchType);
        } catch (IllegalArgumentException e) {
            return DEBUG;
        }
    }

    public boolean isRelease() {
        return this == RELEASE || this == OBFUSCATE;
    }

    public boolean isDebug() {
        return this == DEBUG || this == XRAY;
    }

    public boolean isValid() {
        return valid;
    }
}
