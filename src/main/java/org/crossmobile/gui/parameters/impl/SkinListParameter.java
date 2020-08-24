/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.SelectionListParameter;
import org.crossmobile.utils.PluginRegistry;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_DESKTOP_SKIN;

public class SkinListParameter extends SelectionListParameter {

    public SkinListParameter(ParamList plist) {
        super(plist, CM_DESKTOP_SKIN.tag(), PluginRegistry.getSkins());
    }

    @Override
    public String getVisualTag() {
        return "Window display and behavior";
    }

}
