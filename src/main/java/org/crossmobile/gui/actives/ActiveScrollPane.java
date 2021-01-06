/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResScrollPane;

import java.awt.*;

public class ActiveScrollPane extends HiResScrollPane {
    public ActiveScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

    public ActiveScrollPane(Component view) {
        super(view);
    }

    public ActiveScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    public ActiveScrollPane() {
    }
}
