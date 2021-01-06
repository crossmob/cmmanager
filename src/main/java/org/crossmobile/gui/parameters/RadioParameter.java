/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import com.panayotis.hrgui.HiResPanel;
import org.crossmobile.gui.actives.ActiveIcon;
import org.crossmobile.gui.actives.ActiveToggleButton;
import org.crossmobile.utils.Param;
import org.crossmobile.utils.ParamList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class RadioParameter extends ProjectParameter {

    private String[] values;
    private final String[] icons;
    private final String[] texts;
    private String value;

    public RadioParameter(ParamList list, Param key, List<String> icons, List<String> texts, List<String> values, String deflt, boolean strict) {
        this(list, key, icons.toArray(new String[0]), texts.toArray(new String[0]), values.toArray(new String[0]), deflt, strict);
    }

    public RadioParameter(ParamList list, Param key, String[] icons, String[] texts, String[] values, String deflt, boolean strict) {
        super(list, key);
        value = deflt;
        this.icons = icons;
        this.texts = texts;
        this.values = values;
        Objects.requireNonNull(icons);
        Objects.requireNonNull(texts);
        Objects.requireNonNull(this.values);
        if (values.length != icons.length)
            throw new IllegalArgumentException("Possible values are " + values.length + " but " + icons.length + " icons found");
        if (values.length != texts.length)
            throw new IllegalArgumentException("Possible values are " + values.length + " but " + texts.length + " texts found");

        boolean found = false;
        for (String current : values) {
            if (Objects.equals(current, deflt)) {
                found = true;
                break;
            }
        }
        if (!found && strict)
            throw new IllegalArgumentException("Default value " + deflt + " is not part of the expected values");
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    protected HiResComponent initVisuals() {
        HiResPanel comp = new HiResPanel();
        comp.setLayout(new BoxLayout(comp, BoxLayout.X_AXIS));
        comp.setBorder(new EmptyBorder(4, 0, 0, 0));
        comp.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        Collection<ActiveToggleButton> buttons = new ArrayList<>();
        Dimension max = new Dimension(0, 0);
        for (int i = 0; i < values.length; i++) {
            if (i > 0)  // not the first item
                comp.add(Box.createHorizontalStrut(4));
            ActiveToggleButton button = new ActiveToggleButton(texts[i], new ActiveIcon(icons[i]));
            buttons.add(button);
            button.setActionCommand(values[i]);
            bg.add(button);
            comp.add(button);
            if (Objects.equals(value, values[i]))
                button.setSelected(true);
            button.addActionListener(e -> {
                if (!Objects.equals(value, button.getActionCommand())) {
                    value = button.getActionCommand();
                    fireValueUpdated();
                }
            });
            Dimension size = button.getPreferredSize();
            max.width = Math.max(max.width, size.width);
            max.height = Math.max(max.height, size.height);
        }
        for (ActiveToggleButton button : buttons) {
            button.setPreferredSize(max);
            button.setMinimumSize(max);
            button.setMaximumSize(max);
        }
        return comp;
    }
}
