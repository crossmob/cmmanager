/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import org.crossmobile.gui.elements.Theme;

import java.awt.*;

public class ActiveContextPanel extends ActivePanel implements ThemeChanged {

    public enum Context {
        RUNNING {
            @Override
            Color getColor() {
                return Theme.current().backgroundRunning;
            }
        }, SUCCESS {
            @Override
            Color getColor() {
                return Theme.current().backgroundSuccess;
            }
        }, WARNING {
            @Override
            Color getColor() {
                return Theme.current().backgroundWarning;
            }
        }, ERROR {
            @Override
            Color getColor() {
                return Theme.current().backgroundError;
            }
        };

        abstract Color getColor();
    }

    private Context ctx = Context.RUNNING;

    {
        ThemeManager.register(this);
        themeChanged(Theme.current() == Theme.dark());
        setOpaque(true);
    }

    public ActiveContextPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public ActiveContextPanel(LayoutManager layout) {
        super(layout);
    }

    public ActiveContextPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public ActiveContextPanel() {
    }

    public void setContext(Context context) {
        if (context == null)
            return;
        this.ctx = context;
        themeChanged(Theme.current() == Theme.dark());
    }

    @Override
    public void themeChanged(boolean dark) {
        setBackground(ctx.getColor());
    }
}
