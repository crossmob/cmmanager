/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.BooleanParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.FILESHARINGENABLED;

public class FileSharingParameter extends BooleanParameter {

    public FileSharingParameter(ParamList list) {
        super(list, FILESHARINGENABLED.tag(), false);
    }

    @Override
    public String getVisualBooleanTag() {
        return "File sharing enabled";
    }
}
