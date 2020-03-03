// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResIcon;
import com.panayotis.hrgui.HiResLabel;
import org.crossmobile.gui.elements.Theme;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

public class ActiveLink extends ActiveLabel implements ThemeChanged {

    private String link;

    {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ThemeNotifier.register(this);
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

    @Override
    public void setText(String text) {
        link = text == null || text.isEmpty() ? "" : text;
        super.setText("<HTML>&nbsp;<U>" + link + "</U></HTML>");
    }

    @Override
    public Color getForeground() {
        return Theme.current().text;
    }

    @Override
    public void themeChanged(boolean dark) {
        String old = link;
        setText("");
        setText(old);
    }
}
