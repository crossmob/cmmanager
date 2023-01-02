/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResIcon;
import com.panayotis.hrgui.HiResMenuItem;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class ActiveMenuItem extends HiResMenuItem {
    private final TooltipManager ttm = new TooltipManager(this);

    static final int DELTA = System.getProperty("os.name").toLowerCase().contains("mac") ? 0 : 10;

    {
        setBorder(new CompoundBorder(getBorder(), BorderFactory.createEmptyBorder(6, DELTA, 6, DELTA)));
    }

    public ActiveMenuItem() {
    }

    public ActiveMenuItem(HiResIcon icon) {
        super(icon);
    }

    public ActiveMenuItem(String text) {
        super(text);
    }

    public ActiveMenuItem(Action a) {
        super(a);
    }

    public ActiveMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
    }

    public ActiveMenuItem(String text, HiResIcon icon) {
        super(text, icon);
    }

    @Override
    public Color getForeground() {
        return isEnabled() ? Theme.current().text : Theme.current().disabled;
    }

    @Override
    public Color getBackground() {
        return Theme.current().areaPrimary;
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }
}
