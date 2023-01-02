/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import com.panayotis.appenh.EnhancerManager;
import org.crossmobile.gui.parameters.SelectionListParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_SCALE_FACTOR;

public class ScaleFactorParameter extends SelectionListParameter {
    private static final int deflt = getDefault();

    public ScaleFactorParameter(ParamList list) {
        super(list, CM_SCALE_FACTOR.tag(), new String[]{"1", "1.25", "1.5", "1.75", "2", "2.25", "2.5", "3", "3.5", "4", "5"},
                new String[]{"x1", "x1.25", "x1.5", "x1.75", "x2", "x2.25", "x2.5", "x3", "x3.5", "x4", "x5"},
                new String[]{"Scale by 1", "Scale by 1.25", "Scale by 1.5", "Scale by 1.75", "Scale by 2", "Scale by 2.25", "Scale by 2.5", "Scale by 3", "Scale by 3.5", "Scale by 4", "Scale by 5"}, deflt);
    }

    @Override
    public String getVisualTag() {
        return "Screen scaling factor (for HIDPI)";
    }

    private static int getDefault() {
        double scale = EnhancerManager.getDefault().getDPI() / 96d;
        if (scale < 1.10) return 0;
        else if (scale < 1.35) return 1;
        else if (scale < 1.6) return 2;
        else if (scale < 1.8) return 3;
        else if (scale < 2.1) return 4;
        else if (scale < 2.3) return 5;
        else if (scale < 2.6) return 6;
        else if (scale < 3.1) return 7;
        else if (scale < 3.6) return 8;
        else if (scale < 4.2) return 9;
        else return 10;
    }
}
