/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.HiddenParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.JAVAC_SOURCE;

public class JavacSourceParameter extends HiddenParameter {

    public JavacSourceParameter(ParamList prop) {
        super(prop, JAVAC_SOURCE.tag());
    }

    @Override
    public String getValue() {
        return properties.get(JAVAC_SOURCE.tag());
    }
}
