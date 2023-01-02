/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.RadioParameter;
import org.crossmobile.utils.ParamList;

public class AndroidLogParameter extends RadioParameter {
    public AndroidLogParameter(ParamList list) {
        super(list, null
                , new String[]{"images/4of4", "images/3of4", "images/2of4", "images/1of4"}
                , new String[]{"Full debug", "Out & Error", "Error", "NSLog only"}
                , new String[]{"full", "outerr", "err", "nslog"}, "outerr", false);
    }

    @Override
    protected String getVisualTag() {
        return "Debug output";
    }

    @Override
    public boolean shouldTrackChanges() {
        return false;
    }
}
