// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_SPLASH_DELAY;

public class SplashDelayParameter extends FreeTextParameter {

    public SplashDelayParameter(ParamList list) {
        super(list, CM_SPLASH_DELAY.tag());
    }

    @Override
    public String getVisualTag() {
        return "Splash delay";
    }
}
