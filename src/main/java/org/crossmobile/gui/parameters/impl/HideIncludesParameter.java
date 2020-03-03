// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.OBJC_IGNORE_INCLUDES;

public class HideIncludesParameter extends FreeTextParameter {
    public HideIncludesParameter(ParamList list) {
        super(list, OBJC_IGNORE_INCLUDES.tag());
    }

    @Override
    protected String getVisualTag() {
        return "Ignore include files that are not implemented yet, as a semicolon list";
    }

    @Override
    protected boolean isSingleLineVisual() {
        return false;
    }
}
