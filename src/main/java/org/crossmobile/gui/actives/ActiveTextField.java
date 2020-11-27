/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResTextComponent;
import com.panayotis.hrgui.HiResTextField;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class ActiveTextField extends HiResTextField implements ThemeChanged {

    private final TooltipManager ttm = new TooltipManager(this);

    public ActiveTextField() {
        this("");
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ActiveTextField(String text) {
        super(text);
        setOpaque(true);
        ThemeManager.register(this);
        themeChanged(Theme.current() == Theme.dark());
    }

    @Override
    public void setToolTipText(String text) {
        ttm.setToolTipText(text);
    }

    public void setToolTip(JToolTip toolTip) {
        ttm.setToolTip(toolTip);
    }

    @Override
    public void themeChanged(boolean dark) {
        updateTheme(this, dark);
    }

    static void updateTheme(HiResTextComponent comp, boolean dark) {
        JTextComponent jComp = comp.comp();
        Border border = jComp.getBorder();
        if (border != null)
            jComp.setBorder(new MatteBorder(1, 1, 1, 1, Theme.current().textBorder));
        Color background = jComp.getBackground();
        if (background != null && background.getAlpha() > 1)
            jComp.setBackground(Theme.current().textBack);

        jComp.setForeground(dark ? Color.white : Color.black);
        jComp.setCaretColor(dark ? Color.white : Color.black);
    }
}
