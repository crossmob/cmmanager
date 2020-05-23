/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.BooleanParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.STATUSBARHIDDEN;

public class StatusBarHiddenParameter extends BooleanParameter {

    public StatusBarHiddenParameter(ParamList list) {
        super(list, STATUSBARHIDDEN.tag(), false);
    }

    @Override
    public String getVisualBooleanTag() {
        return "Status bar is hidden";
    }
}
