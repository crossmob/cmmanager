// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.KEY_ALIAS_PASSWORD;

public class AndroidAliasPasswordParameter extends FreeTextParameter {

    public AndroidAliasPasswordParameter(ParamList list) {
        super(list, KEY_ALIAS_PASSWORD.tag(), Type.PASSWORDFIELD);
    }

    @Override
    public String getVisualTag() {
        return "Alias password";
    }
}
