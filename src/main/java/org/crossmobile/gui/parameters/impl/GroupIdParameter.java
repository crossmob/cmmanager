/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.elements.IdDocumentFilter;
import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.GROUP_ID;

public class GroupIdParameter extends FreeTextParameter {

    public static final String DEFAULT_GROUP_ID = "my.company";

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GroupIdParameter(ParamList list) {
        super(list, GROUP_ID.tag());
        setValue(getValue());
        setFilter(new IdDocumentFilter());
        setTooltip(IdDocumentFilter.TOOLTIP);
    }

    @Override
    public String getVisualTag() {
        return "Organization ID";
    }

    @Override
    public String getValue() {
        String val = super.getValue();
        if (val == null)
            return DEFAULT_GROUP_ID;
        val = val.replaceAll("\\.\\.\\.*", ".");
        if (val.startsWith("."))
            val = val.substring(1);
        if (val.endsWith("."))
            val = val.substring(0, val.length() - 1);
        return val.toLowerCase();
    }

}
