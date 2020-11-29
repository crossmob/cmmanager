/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Vector;

public class ActiveList<D> extends JList<D> implements ThemeChanged {
    private final TooltipManager ttm = new TooltipManager(this);

    {
        setCellRenderer(new ActiveListCellRenderer());
        ThemeManager.register(this);
        themeChanged(Theme.current() == Theme.dark());
    }

    public ActiveList(ListModel<D> dataModel) {
        super(dataModel);
    }

    public ActiveList(D[] listData) {
        super(listData);
    }

    public ActiveList(Vector<D> listData) {
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

    private static final class ActiveListCellRenderer implements ListCellRenderer<Object> {

        @Override
        public Component getListCellRendererComponent(JList list, Object item, int index, boolean isSelected, boolean cellHasFocus) {
            ActiveLabel renderer = new ActiveLabel();
            renderer.setBorder(new EmptyBorder(2, 2, 4, 2));
            renderer.setText(item == null ? "" : item.toString());
            renderer.setEnabled(list.isEnabled());
            renderer.setForeground(isSelected ? Theme.current().textSelCell : Theme.current().text);
            renderer.setBackground(isSelected ? Theme.current().backCellSelected : Theme.current().backCell);
            renderer.setFont(list.getFont());
            renderer.setOpaque(true);
            return renderer;
        }
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }
}
