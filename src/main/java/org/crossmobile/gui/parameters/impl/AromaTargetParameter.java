package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.RadioParameter;
import org.crossmobile.utils.ParamList;

public class AromaTargetParameter extends RadioParameter {
    public AromaTargetParameter(ParamList list) {
        super(list, null
                , new String[]{"images/2of4", "images/2of4", "images/2of4"}
                , new String[]{"Intel 64 bit", "Arm 32 bit", "Arm 64 bit"}
                , new String[]{"x86_64", "arm32", "arm64"}, "x86_64", false);
    }

    @Override
    protected String getVisualTag() {
        return "Target platform";
    }
}
