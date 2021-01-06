/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.SelectionListParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.CM_PROJECT;

public class ProjectTypeParameter extends SelectionListParameter {

    public ProjectTypeParameter(ParamList list) {
        super(list, CM_PROJECT.tag(), new String[]{"iphone", "ipad", "ios"}, new String[]{"Phone only", "Pad only", "Hybrid Phone and Pad"}, new String[]{"Project will be phone-only", "Project will be pad-only", "Project will support both phone and pad idioms"}, 0);
    }

    @Override
    public String getVisualTag() {
        return "Project type";
    }
}
