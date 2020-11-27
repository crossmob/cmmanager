/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResMenu;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.lang.reflect.Field;

public class ActiveMenu extends HiResMenu {

    static final int DELTA = System.getProperty("os.name").toLowerCase().contains("mac") ? 0 : 10;

    {
        setBorder(new CompoundBorder(getBorder(), BorderFactory.createEmptyBorder(6, DELTA, 6, DELTA)));

        JPopupMenu popupMenu = new ActivePopupMenu();
        popupMenu.setInvoker(this);
        popupListener = createWinListener(popupMenu);
        try {
            Field popupMenuF = JMenu.class.getDeclaredField("popupMenu");
            if (popupMenuF != null) {
                popupMenuF.setAccessible(true);
                popupMenuF.set(this, popupMenu);
            }
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            System.out.println(exception);
        }
    }

    @Override
    public Color getForeground() {
        return isEnabled() ? Theme.current().text : Theme.current().disabled;
    }

    @Override
    public Color getBackground() {
        return Theme.current().areaPrimary;
    }
}
