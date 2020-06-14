/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.MultiButtonParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.ORIENTATIONS_SUPPORTED;

public class SupportedOrientationsParameter extends MultiButtonParameter {

    private static final char SEPERATOR = ':';

    public SupportedOrientationsParameter(ParamList list) {
        super(list, ORIENTATIONS_SUPPORTED.tag(),
                new String[]{"UIInterfaceOrientationPortrait",
                        "UIInterfaceOrientationLandscapeRight",
                        "UIInterfaceOrientationPortraitUpsideDown",
                        "UIInterfaceOrientationLandscapeLeft"},
                new String[]{"Portrait", "Right", "Upside down", "Left"}, null,
                new String[]{"images/portrait", "images/right", "images/upside", "images/left"},
                null, SEPERATOR);
    }

    @Override
    public String getVisualTag() {
        return "Supported orientations:";
    }

    public void setOrientation(String value) {
        activateValue(value);
    }
}
