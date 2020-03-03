// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.MultiCheckboxParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_KEYBOARD_SUPPORT;

public class KeyboardSupportParameter extends MultiCheckboxParameter {

    private static final char SEPERATOR = ':';

    public KeyboardSupportParameter(ParamList list) {
        super(list, CM_KEYBOARD_SUPPORT.tag(),
                new String[]{"quit", "pause", "back", "menu", "home", "rotate", "multitouch"},
                new String[]{"Quit emulator", "Pause emulator", "Back button", "Menu button", "Home button", "Rotate", "Multitouch"},
                getValue(list.get(CM_KEYBOARD_SUPPORT.tag())), 4, SEPERATOR);
    }

    @Override
    public String getVisualTag() {
        return "Keyboard support:";
    }

    private static String getValue(String value) {
        if (value == null)
            return "";
        else if (value.equals("all"))
            return "quit:pause:back:menu:home:rotate:multitouch";
        return value;
    }
}
