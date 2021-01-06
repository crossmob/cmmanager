/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.android;

import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.Commander;

import java.util.Collection;
import java.util.function.Consumer;

public class InstallerThread extends Thread {

    private static final String[] yesSignatures = {"N/y", "y/N"};

    private final int[] yesIndices = new int[yesSignatures.length];
    private final InstallerFrame installer;
    private Commander cmd;

    public InstallerThread(InstallerFrame installer) {
        super("Android SDK License Agreement thread");
        this.installer = installer;
    }

    @Override
    public void run() {
        Commander cmd = this.cmd = new Commander(Prefs.getAndroidSDKManagerLocation(), "--sdk_root=" + Prefs.getAndroidSDKLocation(), "--licenses");
        cmd.appendEnvironmentalParameter("JAVA_HOME", Prefs.getJDKLocation());
        cmd.setCharOutListener(this::incomingOutChar);
        cmd.setCharErrListener(this::incomingErrChar);
        cmd.exec();
        cmd.waitFor();
        if (!installer.isCancelled())
            installer.finish();
    }

    public void sendYes() {
        Commander cmd = this.cmd;
        if (cmd != null)
            cmd.sendLine("y");
    }

    public void sendCancel() {
        Commander cmd = this.cmd;
        if (cmd != null)
            cmd.kill();
        this.cmd = null;
    }

    private void incomingOutChar(Character c) {
        installer.addChar(c);
        for (int i = 0; i < yesSignatures.length; i++) {
            if (yesSignatures[i].charAt(yesIndices[i]) == c)
                yesIndices[i]++;
            else
                yesIndices[i] = 0;
            if (yesIndices[i] >= yesSignatures[i].length()) {
                for (int ci = 0; ci < yesSignatures.length; ci++)
                    yesIndices[ci] = 0;
                installer.enableYes();
                break;
            }
        }
    }

    private void incomingErrChar(char c) {
        installer.addChar(c);
    }
}
