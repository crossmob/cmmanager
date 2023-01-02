/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.gui.actives.ActiveIcon;

import javax.swing.*;

public enum TemplateType {
    BUTTON_PROJECT,
    NAVIGATION_PROJECT,
    TABLE_PROJECT,
    STORYBOARD_PROJECT,
    I18N_PROJECT,
    CAMERA_PROJECT,
    MAP_PROJECT,
    SINGLE_PROJECT,
    PLUGIN_PROJECT,
    EMPTY_PROJECT;

    public final String actual;

    TemplateType() {
        String name = name();
        actual = name.substring(0, name.indexOf('_')).toLowerCase();
    }

    public Icon icon() {
        return new ActiveIcon("images/" + actual + "_project");
    }
}
