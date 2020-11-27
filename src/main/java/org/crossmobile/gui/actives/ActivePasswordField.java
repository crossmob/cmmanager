/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResPasswordField;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;

import java.awt.*;

import static org.crossmobile.gui.actives.ActiveTextField.updateTheme;

public class ActivePasswordField extends HiResPasswordField implements ThemeChanged {

    private final TooltipManager ttm = new TooltipManager(this);

    public ActivePasswordField() {
        this("");
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ActivePasswordField(String text) {
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
}
