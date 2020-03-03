// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.KEY_STORE_PASSWORD;

public class AndroidKeystorePasswordParameter extends FreeTextParameter {

    public AndroidKeystorePasswordParameter(ParamList list) {
        super(list, KEY_STORE_PASSWORD.tag(), Type.PASSWORDFIELD);
    }

    @Override
    public String getVisualTag() {
        return "Keystore password";
    }
}
