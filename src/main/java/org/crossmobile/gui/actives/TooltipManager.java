/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class TooltipManager {

    private static final PopupFactory popupFactory = PopupFactory.getSharedInstance();
    private Popup popup;
    private ActiveTooltip toolTip;
    private final JComponent parent;

    TooltipManager(JComponent parent) {
        this.parent = parent;
        parent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (toolTip != null) {
                    try {
                        Point p = parent.getLocationOnScreen();
                        p.y += parent.getHeight() + 1;
                        popup = popupFactory.getPopup(parent, toolTip, p.x, p.y);
                        popup.show();
                    } catch (Exception ignore) {
                        hidePopup();
                    }
                }
            }

            private void hidePopup() {
                if (popup != null) {
                    popup.hide();
                    popup = null;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hidePopup();
            }
        });
    }

    void setToolTipText(String text) {
        if (text == null || text.trim().isEmpty())
            toolTip = null;
        else {
            if (toolTip == null)
                toolTip = new ActiveTooltip(parent);
            toolTip.setTipText(text);
        }
    }

    void setToolTip(ActiveTooltip toolTip) {
        this.toolTip = toolTip;
    }
}
