/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import org.crossmobile.utils.ParamList;

public abstract class DisplayInfoParameter extends ProjectParameter {

    public DisplayInfoParameter(ParamList list) {
        super(list, null);
    }

    @Override
    public String getValue() {
        return "";
    }
}
