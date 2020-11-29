/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResComboBox;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class ActiveComboBox<T> extends HiResComboBox<T> {

    private final TooltipManager ttm = new TooltipManager(this);

    public ActiveComboBox() {
        this(new DefaultComboBoxModel<>());
    }

    public ActiveComboBox(T[] items) {
        this(new DefaultComboBoxModel<>(items));
    }

    public ActiveComboBox(Vector<T> items) {
        this(new DefaultComboBoxModel<>(items));
    }

    public ActiveComboBox(ComboBoxModel<T> aModel) {
        super(aModel);
        setFont(getFont());
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
