/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class JWait extends JPanel {

    private final Timer timer;
    private final Icon icon;
    private float angle = 0;
    private int border = 2;

    public JWait(Icon icon) {
        timer = new Timer(40, e -> {
            angle += 0.05f;
            repaint();
        });
        timer.setRepeats(true);
        this.icon = icon;
        setOpaque(false);
    }

    public void setRunning(boolean running) {
        if (isRunning() == running)
            return;
        if (running)
            timer.start();
        else
            timer.stop();
        angle = 0;
        repaint();
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(icon.getIconWidth() + border * 2, icon.getIconHeight() + border * 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isRunning())
            return;

        timer.stop();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int cx = icon.getIconWidth() / 2 + border;
        int cy = icon.getIconHeight() / 2 + border;
        Rectangle r = new Rectangle(0, 0, icon.getIconWidth() + border * 2, icon.getIconHeight() + border * 2);
        g2.setClip(r);
        AffineTransform original = g2.getTransform();
        AffineTransform at = new AffineTransform();
        at.concatenate(original);
        at.rotate(angle, cx, cy);
        g2.setTransform(at);
        icon.paintIcon(this, g2, border, border);
        g2.setTransform(original);
        timer.start();
    }
}
