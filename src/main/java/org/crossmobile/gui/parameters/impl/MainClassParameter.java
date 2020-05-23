/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.MAIN_CLASS;

public class MainClassParameter extends FreeTextParameter {

    public MainClassParameter(ParamList list) {
        super(list, MAIN_CLASS.tag(), Type.TEXTFIELD, false);
    }

    @Override
    public String getVisualTag() {
        return "Main Class";
    }

}
