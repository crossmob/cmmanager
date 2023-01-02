/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResMenu;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class ActiveMenu extends HiResMenu {
    private final TooltipManager ttm = new TooltipManager(this);

    static final int DELTA = System.getProperty("os.name").toLowerCase().contains("mac") ? 0 : 10;

    {
        setBorder(new CompoundBorder(getBorder(), BorderFactory.createEmptyBorder(6, DELTA, 6, DELTA)));
        JPopupMenu popupMenu = getPopupMenu();
        popupMenu.setUI(ActivePopupMenu.createActivePopupMenuUI());
        popupMenu.setInvoker(this);
        popupListener = createWinListener(popupMenu);
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
