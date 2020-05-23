/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.HiddenParameter;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.SDK_DIR;

public class AndroidSDKParameter extends HiddenParameter {

    public AndroidSDKParameter(ParamList params) {
        super(params, SDK_DIR.tag());
    }

    @Override
    public String getValue() {
        return Prefs.getAndroidSDKLocation();
    }
}
