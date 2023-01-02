/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.DISPLAY_NAME;
import static org.crossmobile.utils.ParamsCommon.IPHONEOS_DEPLOYMENT_TARGET;

public class DeploymentTargetParameter extends FreeTextParameter {

    public DeploymentTargetParameter(ParamList list) {
        super(list, IPHONEOS_DEPLOYMENT_TARGET.tag());
    }

    @Override
    public String getVisualTag() {
        return "Deployment target";
    }
}
