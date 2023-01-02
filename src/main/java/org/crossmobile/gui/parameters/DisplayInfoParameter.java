/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.gui.actives.ActiveLink;
import org.crossmobile.gui.actives.ActivePanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class DisplayInfoParameter extends ProjectParameter {

    private final String[] text;

    public DisplayInfoParameter(String... text) {
        super(null, null);
        this.text = text;
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    protected String getVisualTag() {
        return "";
    }

    @Override
    protected HiResComponent initVisuals() {
        ActivePanel panel = new ActivePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (String row : text) {
            if (row.startsWith("http")) {
                ActiveLink link = new ActiveLink(row);
                link.setMultiRun(true);
                link.setAction(() -> {
                    try {
                        Desktop.getDesktop().browse(URI.create(row));
                    } catch (IOException ignored) {
                    }
                });
                panel.add(link);
            } else
                panel.add(new ActiveLabel(row));
        }
        return panel;
    }
}
