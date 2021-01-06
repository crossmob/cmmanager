/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResTooltip;

import javax.swing.*;

public class ActiveTooltip extends HiResTooltip {
    private final TooltipManager ttm = new TooltipManager(this);

    public ActiveTooltip(JComponent parent) {
        super(parent);
    }

    @Override
    public void updateUI() {
        setUI(ActiveTooltipUI.createUI(this));
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }
}
