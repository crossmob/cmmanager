/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResTooltip;
import com.panayotis.hrgui.ScreenUtils;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;
import java.awt.*;

public class ActiveTooltipUI extends BasicToolTipUI {
    private static ActiveTooltipUI instance;
    private static final ActiveTextArea textArea = new ActiveTextArea();
    private CellRendererPane rendererPane;
    private static final int delta = (int) Math.ceil(ScreenUtils.getGraphicsScale() * 3);

    static {
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
        textArea.setBackground(new Color(0, 0, 0, 0));
    }

    public static ComponentUI createUI(HiResTooltip hiResTooltip) {
        if (instance == null)
            instance = new ActiveTooltipUI();
        return instance;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        rendererPane = new CellRendererPane();
        c.add(rendererPane);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        c.remove(rendererPane);
        rendererPane = null;
    }

    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        g.setColor(Theme.current().areaPrimary);
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Theme.current().iconbottom);
        g.drawRect(0, 0, size.width - 1, size.height - 1);
        rendererPane.paintComponent(g, textArea, c, delta, delta, size.width - 2 * delta, size.height - 2 * delta, true);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        textArea.setText(((JToolTip) c).getTipText());
        Dimension preferredSize = textArea.getPreferredSize();
        preferredSize.width += delta * 2;
        preferredSize.height += delta * 2;
        return preferredSize;
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}