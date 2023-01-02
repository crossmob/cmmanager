/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.HiddenParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.TARGET;

public class AndroidTargetParameter extends HiddenParameter {

    public static final String DEFAULT = "android-" + AndroidTargetNumericParameter.DEFAULT;

    public AndroidTargetParameter(ParamList list) {
        super(list, TARGET.tag());
    }

    @Override
    public String getValue() {
        return DEFAULT;
    }
}
