/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResMenuItem;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class ActiveMenuItem extends HiResMenuItem {

    static final int DELTA = System.getProperty("os.name").toLowerCase().contains("mac") ? 0 : 10;

    {
        setBorder(new CompoundBorder(getBorder(), BorderFactory.createEmptyBorder(6, DELTA, 6, DELTA)));
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
