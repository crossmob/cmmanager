/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_VENDOR;

public class VendorParameter extends FreeTextParameter {

    public VendorParameter(ParamList list) {
        super(list, CM_VENDOR.tag());
    }

    @Override
    public String getVisualTag() {
        return "Vendor";
    }
}
