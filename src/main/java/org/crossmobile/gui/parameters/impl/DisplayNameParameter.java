// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.DISPLAY_NAME;

public class DisplayNameParameter extends FreeTextParameter {

    public DisplayNameParameter(ParamList list) {
        super(list, DISPLAY_NAME.tag());
    }

    @Override
    public String getVisualTag() {
        return "Display name";
    }
}
