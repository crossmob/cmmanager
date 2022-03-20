/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_URL;

public class URLParameter extends FreeTextParameter {

    public URLParameter(ParamList list) {
        super(list, CM_URL.tag());
    }

    @Override
    public String getVisualTag() {
        return "URL";
    }
}
