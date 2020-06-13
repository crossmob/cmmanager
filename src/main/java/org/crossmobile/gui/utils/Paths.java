/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import org.crossmobile.utils.Log;
import org.crossmobile.utils.Opt;
import org.crossmobile.utils.SystemDependent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.crossmobile.utils.SystemDependent.Execs.*;
import static org.crossmobile.utils.SystemDependent.getHome;

public final class Paths {

    private static final String EXTRA_PATH = "../extra";   // location of libraries when debugging

    private static File APPFILE;
    private static final String MAVEN_PATH;
    private static final String MAKEAPP_PATH;
    private static final String APP_PATH;

    static {
        try {
            APPFILE = new File(Paths.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ignore) {
            APPFILE = new File(Paths.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        }
        String mavenroot, makeapproot, approot;
        if (APPFILE.isDirectory()) {
            File extra = new File(APPFILE.getParentFile().getParentFile(), "extra").getAbsoluteFile();
            mavenroot = extra + File.separator + "common";
            makeapproot = extra + File.separator + SystemDependent.getOSBinName();
            approot = APPFILE.getParentFile().getAbsolutePath();
        } else {
            mavenroot = makeapproot = getPath(APPFILE.getParentFile().getAbsoluteFile(), HomeReference.NO_OVERRIDE);
            approot = APPFILE.getAbsolutePath();
        }
        MAVEN_PATH = mavenroot + File.separator + "apache-maven" + File.separator + "bin" + File.separator + MVN.filename();
        MAKEAPP_PATH = makeapproot + File.separator + "bin" + File.separator + MAKEAPP.filename();

        if (approot.startsWith("/tmp/.mount_")) {
            // as an AppImage
            String appimageroot = System.getenv("APPIMAGE");
            if (appimageroot != null)
                APP_PATH = appimageroot;
            else {
                APP_PATH = approot;
                Log.error("Application started as AppImage but the file location was not found.");
            }
        } else
            APP_PATH = approot;
    }

    public static String getPath(File path, HomeReference overrideHome) {
        try {
            return getPath(path.getCanonicalPath(), overrideHome);
        } catch (IOException ex) {
            return getPath(path.getAbsolutePath(), overrideHome);
        }
    }

    public static String getPath(String path, HomeReference overrideHome) {
        path = path.replace('\\', '/');
        if (overrideHome != null && overrideHome != HomeReference.NO_OVERRIDE)
            if (overrideHome == HomeReference.PROP_TO_ABS) {
                if (path.startsWith("${user.home}"))
                    path = getHome() + path.substring("${user.home}".length());
            } else if (path.startsWith(getHome() + "/"))
                path = (overrideHome == HomeReference.PROPERTY_STYLE ? "${user.home}" : "~")
                        + ((getHome().length() + 1) < path.length() ? ("/" + path.substring(getHome().length() + 1)) : "");
        return path;
    }

    public static File getAbsolutePath(String path, String currentDir) {
        return getAbsolutePath(new File(path), currentDir == null ? null : new File(currentDir));
    }

    public static File getAbsolutePath(File path, File currentDir) {
        if (!path.isAbsolute()) {
            if (currentDir == null)
                currentDir = new File(System.getProperty("user.dir"));
            path = new File(currentDir, path.getPath());
        }
        try {
            return path.getCanonicalFile();
        } catch (IOException ex) {
            return path;
        }
    }

    public static String getPathSimple(File path) {
        if (path == null)
            return null;
        return path.getPath().replace('\\', '/');
    }

    public static String getMavenExec() {
        return MAVEN_PATH;
    }

    public static String getMakeAppExec() {
        return MAKEAPP_PATH;
    }

    public static String getApplicationPath() {
        return APP_PATH;
    }

    public static String getXRayPath() {
        File cmxray = new File(SystemDependent.getPluginsDir(), "cmxray.jar");
        return cmxray.exists() ? cmxray.getAbsolutePath() : null;
    }

    private Paths() {
    }

    public enum HomeReference {

        NO_OVERRIDE,
        PROPERTY_STYLE,
        PATH_STYLE,
        PROP_TO_ABS
    }
}
