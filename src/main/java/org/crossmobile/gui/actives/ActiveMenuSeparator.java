/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import java.awt.*;

public class ActiveMenuSeparator extends JPopupMenu.Separator {

    private static final boolean SHOULD_DRAW = System.getProperty("os.name").toLowerCase().contains("mac");

    {
        setPreferredSize(new Dimension(10, 2));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (SHOULD_DRAW) {
            g.setColor(Theme.current().iconbottom);
            g.drawLine(0, 1, getWidth(), 1);
        }
    }
}
