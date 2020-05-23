/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.HiddenParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.JAVAC_TARGET;

public class JavacTargetParameter extends HiddenParameter {

    public JavacTargetParameter(ParamList prop) {
        super(prop, JAVAC_TARGET.tag());
    }

    @Override
    public String getValue() {
        return properties.get(JAVAC_TARGET.tag());
    }
}
