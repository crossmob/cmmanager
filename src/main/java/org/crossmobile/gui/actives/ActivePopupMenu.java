/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPopupMenuUI;
import java.awt.*;

public class ActivePopupMenu extends JPopupMenu {
    {
        setUI(createActivePopupMenuUI());
    }

    static PopupMenuUI createActivePopupMenuUI() {
        return new SynthPopupMenuUI() {
            @Override
            protected void paint(SynthContext context, Graphics g) {
                g.setColor(Theme.current().areaPrimary);
                g.fillRect(0, 0, context.getComponent().getWidth() - 1, context.getComponent().getHeight() - 1);
            }
        };
    }
}
