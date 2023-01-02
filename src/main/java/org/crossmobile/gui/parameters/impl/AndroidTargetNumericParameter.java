/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.HiddenParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.TARGET_NUMERIC;

public class AndroidTargetNumericParameter extends HiddenParameter {

    public static final String DEFAULT = "21";
    public static final String VERSION = "5.0.1";

    public AndroidTargetNumericParameter(ParamList list) {
        super(list, TARGET_NUMERIC.tag());
    }

    @Override
    public String getValue() {
        return DEFAULT;
    }
}
