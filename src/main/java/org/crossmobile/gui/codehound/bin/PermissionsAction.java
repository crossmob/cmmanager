/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.bin;

import org.crossmobile.gui.LongProcFrame;
import org.crossmobile.gui.project.Project;
import org.crossmobile.gui.utils.Paths;
import org.crossmobile.utils.Commander;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import static org.crossmobile.utils.ClasspathUtils.CLASS_USAGE_SIGNATURE;

public class PermissionsAction {

    public static Collection<Permissions> findPermissions(final Project proj) {
        final LongProcFrame frame = new LongProcFrame("Recalculating dependencies",
                "This procedure will need to clean the current project and rebuild it using a java-only target. If you want to go on press the Continue button.",
                "Calculate dependencies for plugins also", "Please wait while compiling project...");
        Collection<Permissions> permissions = new TreeSet<>();

        Commander launcher = new Commander(Paths.getMavenExec(), "clean", "compile", "-Pdesktop,findclasses");
        launcher.setCurrentDir(proj.getPath());
        launcher.setOutListener(line -> {
            int idx = line.indexOf(CLASS_USAGE_SIGNATURE);
            if (idx >= 0)
                permissions.addAll(convertImportToPermissions(line.substring(idx + CLASS_USAGE_SIGNATURE.length()).trim()));
            else
                System.out.println(line);
        });
        launcher.setErrListener(System.err::println);
        launcher.setEndListener(result -> frame.invoke(result == 0
                ? "Process terminated successfully"
                : "Unable to clean up project"));

        frame.setExecuteCallback((withOptions) -> launcher.exec());
        frame.setCancelCallback(launcher::kill);

        frame.setVisible(true);
        // wait for the window to finish
        return permissions;
    }

    private static Collection<Permissions> convertImportToPermissions(String className) {
        EnumSet<Permissions> perms = EnumSet.noneOf(Permissions.class);
        for (Permissions perm : Permissions.values())
            if (perm.requires(className))
                perms.add(perm);
        return perms;
    }

}
