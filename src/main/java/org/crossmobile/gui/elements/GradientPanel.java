/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import com.panayotis.hrgui.HiResPanel;

import java.awt.*;

public class GradientPanel extends HiResPanel {

    public GradientPanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        g2d.setPaint(new GradientPaint(0, 0, Theme.current().areaPrimary, 0, h, Theme.current().areaSecondary));
        g2d.fillRect(0, 0, w, h);
    }
}
