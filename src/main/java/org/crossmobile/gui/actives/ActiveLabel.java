/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResIcon;
import com.panayotis.hrgui.HiResLabel;
import org.crossmobile.gui.elements.Theme;

import java.awt.*;

public class ActiveLabel extends HiResLabel {
    private final TooltipManager ttm = new TooltipManager(this);

    {
        setOpaque(false);
    }

    public ActiveLabel(String text, HiResIcon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public ActiveLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public ActiveLabel(String text) {
        super(text);
    }

    public ActiveLabel(HiResIcon icon, int horizontalAlignment) {
        super(icon, horizontalAlignment);
    }

    public ActiveLabel(HiResIcon icon) {
        super(icon);
    }

    public ActiveLabel() {
    }

    @Override
    public Color getForeground() {
        return Theme.current().text;
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }
}
