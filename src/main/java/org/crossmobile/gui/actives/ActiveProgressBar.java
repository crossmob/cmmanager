/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResProgressBar;

import javax.swing.*;

public class ActiveProgressBar extends HiResProgressBar {
    private final TooltipManager ttm = new TooltipManager(this);

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

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }
}
