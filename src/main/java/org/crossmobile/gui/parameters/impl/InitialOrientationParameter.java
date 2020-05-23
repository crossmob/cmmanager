/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.SelectionListParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.ORIENTATIONS_INITIAL;

public class InitialOrientationParameter extends SelectionListParameter {

    public InitialOrientationParameter(ParamList list) {
        super(list, ORIENTATIONS_INITIAL.tag(), new String[]{"UIInterfaceOrientationPortrait", "UIInterfaceOrientationPortraitUpsideDown", "UIInterfaceOrientationLandscapeLeft", "UIInterfaceOrientationLandscapeRight"}, new String[]{"Portrait", "Upside down", "Left", "Right"}, new String[]{"Initial orientation will be portrait", "Initial orientation will be upside down portrait", "Initial orientation will be counter-clockwise landscape", "Initial orientation will be clockwise landscape"}, 0);
    }

    @Override
    public String getVisualTag() {
        return "Initial orientation";
    }

    public void check(String value) {
        String[] split = value.split(":");
        if (split.length < 1)
            return;
        for (String s : split)
            if (s.equals(getValue()))
                return;
        setValue(split[0]);
    }
}
