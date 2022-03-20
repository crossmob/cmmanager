/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import java.awt.*;

public class ActivePopupMenu extends JPopupMenu {

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        g2d.setColor(Theme.current().areaPrimary);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
