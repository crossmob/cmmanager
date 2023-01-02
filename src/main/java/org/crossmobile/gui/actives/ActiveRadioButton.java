/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResIcon;
import com.panayotis.hrgui.HiResRadioButton;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import java.awt.*;

public class ActiveRadioButton extends HiResRadioButton {
    private final TooltipManager ttm = new TooltipManager(this);

    {
        setOpaque(false);
    }

    public ActiveRadioButton() {
    }

    public ActiveRadioButton(HiResIcon icon) {
        super(icon);
    }

    public ActiveRadioButton(Action a) {
        super(a);
    }

    public ActiveRadioButton(HiResIcon icon, boolean selected) {
        super(icon, selected);
    }

    public ActiveRadioButton(String text) {
        super(text);
    }

    public ActiveRadioButton(String text, boolean selected) {
        super(text, selected);
    }

    public ActiveRadioButton(String text, HiResIcon icon) {
        super(text, icon);
    }

    public ActiveRadioButton(String text, HiResIcon icon, boolean selected) {
        super(text, icon, selected);
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }

    @Override
    public Color getForeground() {
        return Theme.current().text;
    }
}
