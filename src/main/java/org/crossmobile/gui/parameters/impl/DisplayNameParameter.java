/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.DISPLAY_NAME;

public class DisplayNameParameter extends FreeTextParameter {

    private final boolean isPlugin;

    public DisplayNameParameter(ParamList list, boolean isPlugin) {
        super(list, DISPLAY_NAME.tag());
        this.isPlugin = isPlugin;
    }

    @Override
    public String getVisualTag() {
        return (isPlugin ? "Descriptive" : "Display") + " name";
    }
}
