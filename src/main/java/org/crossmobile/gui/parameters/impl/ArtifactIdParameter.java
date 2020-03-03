// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.elements.IdDocumentFilter;
import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.ARTIFACT_ID;

public class ArtifactIdParameter extends FreeTextParameter {

    public static final String DEFAULT_ARTIFACT_ID = "crossmobile.projects";

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ArtifactIdParameter(ParamList list) {
        super(list, ARTIFACT_ID.tag());
        setValue(getValue());
        setFilter(new IdDocumentFilter(true));
        setTooltip(IdDocumentFilter.TOOLTIP_SIMPLE);
    }

    @Override
    public String getVisualTag() {
        return "Application name";
    }

    @Override
    public String getValue() {
        String val = super.getValue();
        if (val == null)
            return DEFAULT_ARTIFACT_ID;
        return val.toLowerCase();
    }

}
