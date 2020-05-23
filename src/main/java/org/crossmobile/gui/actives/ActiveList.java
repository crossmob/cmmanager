/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import java.util.Vector;

public class ActiveList extends JList implements ThemeChanged {
    {
        ThemeNotifier.register(this);
        themeChanged(Theme.current() == Theme.dark());
    }

    public ActiveList(ListModel dataModel) {
        super(dataModel);
    }

    public ActiveList(Object[] listData) {
        super(listData);
    }

    public ActiveList(Vector listData) {
        super(listData);
    }

    public ActiveList() {
    }

    @Override
    public void setEnabled(boolean enabled) {
        themeChanged(Theme.current() == Theme.dark());
    }

    @Override
    public void themeChanged(boolean dark) {
        setBackground(isEnabled() ? Theme.current().backCell : Theme.current().disabled);
    }
}
