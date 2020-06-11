/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.BooleanParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_FULLSCREEN_DESKTOP;
import static org.crossmobile.utils.ParamsCommon.STATUSBARHIDDEN;

public class FullScreenDesktopParameter extends BooleanParameter {

    public FullScreenDesktopParameter(ParamList list) {
        super(list, CM_FULLSCREEN_DESKTOP.tag(), false);
    }

    @Override
    public String getVisualBooleanTag() {
        return "Launch in full-screen mode";
    }
}
