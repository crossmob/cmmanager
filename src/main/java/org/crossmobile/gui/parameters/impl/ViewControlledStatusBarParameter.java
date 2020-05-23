/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.BooleanParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.VIEWCONTROLLED_STATUSBAR;

public class ViewControlledStatusBarParameter extends BooleanParameter {

    public ViewControlledStatusBarParameter(ParamList list) {
        super(list, VIEWCONTROLLED_STATUSBAR.tag(), false);
    }

    @Override
    public String getVisualBooleanTag() {
        return "Status bar visibility is controlled by the View Controller";
    }
}
