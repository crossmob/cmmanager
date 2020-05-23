/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import java.awt.*;

public class EventUtils {

    public static void postAction(Runnable action) {
        new Thread(() -> EventQueue.invokeLater(action)).start();
    }
}
