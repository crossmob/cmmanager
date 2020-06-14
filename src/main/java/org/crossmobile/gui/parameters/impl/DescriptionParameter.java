/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_DESCRIPTION;

public class DescriptionParameter extends FreeTextParameter {

    public DescriptionParameter(ParamList list) {
        super(list, CM_DESCRIPTION.tag());
    }

    @Override
    public String getVisualTag() {
        return "Description";
    }
}
