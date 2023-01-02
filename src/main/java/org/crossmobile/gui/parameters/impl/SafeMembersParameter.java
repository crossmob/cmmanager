/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.BooleanParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_OBJC_SAFEMEMBERS;

public class SafeMembersParameter extends BooleanParameter {

    public SafeMembersParameter(ParamList list) {
        super(list, CM_OBJC_SAFEMEMBERS.tag(), true);
    }

    @Override
    protected String getVisualBooleanTag() {
        return "Use safe member references";
    }

    @Override
    protected String tooltipForStatus(Boolean status) {
        return "When enabled, full java inheritance on member variables is supported.\n" +
                "If safe references are disabled, then the generated iOS code is faster,\n" +
                "but inherited objects could not have members with the same name as\n" +
                "in parent objects. ";
    }
}
