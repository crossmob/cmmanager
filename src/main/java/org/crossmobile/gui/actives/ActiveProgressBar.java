// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResProgressBar;

import javax.swing.*;

public class ActiveProgressBar extends HiResProgressBar {
    public ActiveProgressBar() {
    }

    public ActiveProgressBar(int orient) {
        super(orient);
    }

    public ActiveProgressBar(int min, int max) {
        super(min, max);
    }

    public ActiveProgressBar(int orient, int min, int max) {
        super(orient, min, max);
    }

    public ActiveProgressBar(BoundedRangeModel newModel) {
        super(newModel);
    }
}
