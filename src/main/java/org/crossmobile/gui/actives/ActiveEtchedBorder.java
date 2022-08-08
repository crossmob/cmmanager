/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import javax.swing.border.EtchedBorder;
import java.awt.*;

public class ActiveEtchedBorder extends EtchedBorder implements ThemeChanged {

    {
        ThemeManager.register(this);
        themeChanged(Theme.current() == Theme.dark());
    }

    public ActiveEtchedBorder() {
    }

    public ActiveEtchedBorder(int etchType) {
        super(etchType);
    }

    public ActiveEtchedBorder(Color highlight, Color shadow) {
        super(highlight, shadow);
    }

    public ActiveEtchedBorder(int etchType, Color highlight, Color shadow) {
        super(etchType, highlight, shadow);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return new Insets(3, 3, 3, 3);
    }

    @Override
    public void themeChanged(boolean dark) {
        highlight = Theme.current().iconbottom;
        shadow = dark ? Color.black : Color.lightGray;
    }
}
