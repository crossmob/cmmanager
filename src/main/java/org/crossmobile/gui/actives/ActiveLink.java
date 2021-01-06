/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResIcon;
import org.crossmobile.gui.elements.Theme;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActiveLink extends ActiveLabel {
    private final TooltipManager ttm = new TooltipManager(this);
    private Runnable action;
    private boolean multiRun = false;

    {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.run();
                    if (!multiRun) {
                        action = null;
                        setText("");
                    }
                }
            }
        });
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setText("");
    }

    public ActiveLink(String text, HiResIcon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public ActiveLink(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public ActiveLink(String text) {
        super(text);
    }

    public ActiveLink(HiResIcon icon, int horizontalAlignment) {
        super(icon, horizontalAlignment);
    }

    public ActiveLink(HiResIcon icon) {
        super(icon);
    }

    public ActiveLink() {
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setMultiRun(boolean multiRun) {
        this.multiRun = multiRun;
    }

    @Override
    public Color getForeground() {
        return Theme.current().text;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics metrics = getFontMetrics(getFont());
        g.drawLine(0, metrics.getAscent() + 1, metrics.stringWidth(getText()), metrics.getAscent() + 1);
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }
}
