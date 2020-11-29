/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import com.panayotis.hrgui.HiResTextArea;
import org.crossmobile.gui.elements.Theme;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static org.crossmobile.gui.actives.ActiveTextField.updateTheme;

public class ActiveTextArea extends HiResTextArea implements ThemeChanged {

    private final TooltipManager ttm = new TooltipManager(this);
    private boolean inactiveColorFollowsActive = false;

    {
        setTabSize(4);
        ThemeManager.register(this);
        themeChanged(Theme.current() == Theme.dark());
    }

    public ActiveTextArea() {
    }

    public ActiveTextArea(String text) {
        super(text);
    }

    public ActiveTextArea(int rows, int columns) {
        super(rows, columns);
    }

    public ActiveTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
    }

    public ActiveTextArea(Document doc) {
        super(doc);
    }

    public ActiveTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
    }

    @Override
    public void setToolTipText(String text) {
        if (ttm != null)
            ttm.setToolTipText(text);
    }

    public void setInactiveColorFollowsActive(boolean inactiveColorFollowsActive) {
        if (this.inactiveColorFollowsActive == inactiveColorFollowsActive)
            return;
        this.inactiveColorFollowsActive = inactiveColorFollowsActive;
        themeChanged(Theme.current() == Theme.dark());
    }

    @Override
    public void themeChanged(boolean dark) {
        updateTheme(this, dark);
        if (inactiveColorFollowsActive)
            setDisabledTextColor(getForeground());
    }

    public void removeMouseListeners() {
        for (MouseListener listener : getListeners(MouseListener.class))
            removeMouseListener(listener);
        for (MouseMotionListener listener : getListeners(MouseMotionListener.class))
            removeMouseMotionListener(listener);
    }
}
