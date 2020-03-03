// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui;

import com.panayotis.hrgui.HiResFrame;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class RegisteredFrame extends HiResFrame {

    private final static Set<JFrame> FRAMES = new HashSet<>();
    private static Runnable finishCallback;

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        synchronized (FRAMES) {
            if (visible)
                FRAMES.add(this);
            else {
                FRAMES.remove(this);
                if (FRAMES.isEmpty()) {
                    if (finishCallback != null)
                        finishCallback.run();
                    System.exit(0);
                }
            }
        }
    }

    public static int count() {
        return FRAMES.size();
    }

    public static void setFinishCallback(Runnable finishCallback) {
        RegisteredFrame.finishCallback = finishCallback;
    }
}
