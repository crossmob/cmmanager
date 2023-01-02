/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResIcon;
import org.crossmobile.gui.elements.Theme;

import java.awt.*;

public class ActiveIcon extends HiResIcon implements ThemeChanged {

    private final transient Image bright;
    private final transient Image dark;

    {
        ThemeManager.register(this);
    }

    public ActiveIcon(String resourceName) {
        super(resourceName, Theme.bright().icontop, Theme.bright().iconbottom);
        this.bright = getImage();
        this.dark = new HiResIcon(resourceName, Theme.dark().icontop, Theme.dark().iconbottom).getImage();
        themeChanged(Theme.current() == Theme.dark());
    }

    @Override
    public void themeChanged(boolean dark) {
        setImage(dark ? this.dark : this.bright);
    }
}
