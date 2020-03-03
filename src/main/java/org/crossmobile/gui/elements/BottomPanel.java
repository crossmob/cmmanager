// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.elements;

import org.crossmobile.gui.ProjectFrame;
import org.crossmobile.gui.actives.ActiveButton;
import org.crossmobile.gui.actives.ActivePanel;

import java.awt.*;
import java.util.function.Consumer;

public class BottomPanel extends ActivePanel {

    private ProjectFrame projectFrame;
    private final ActiveButton button;

    public BottomPanel(String label, String icon, Consumer<ProjectFrame> callback) {
        super(new FlowLayout(FlowLayout.LEFT));
         button = new ActiveButton();
        if (label != null)
            button.setText(label);
        if (icon != null)
            button.setIcon(icon);
        if (callback != null)
            button.addActionListener(e -> callback.accept(this.projectFrame));
        add(button);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    public void setProjectFrame(ProjectFrame projectFrame) {
        this.projectFrame = projectFrame;
    }
}
