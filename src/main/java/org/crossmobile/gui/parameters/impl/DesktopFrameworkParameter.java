// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.BooleanParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_DESKTOP_FRAMEWORK;

public class DesktopFrameworkParameter extends BooleanParameter {

    public DesktopFrameworkParameter(ParamList list) {
        super(list, CM_DESKTOP_FRAMEWORK.tag(), false);
    }

    @Override
    public String getVisualBooleanTag() {
        return "Java FX";
    }

}
