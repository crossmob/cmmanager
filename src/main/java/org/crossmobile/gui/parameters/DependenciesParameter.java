// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import org.crossmobile.gui.elements.DependencyEditor;
import org.crossmobile.utils.ParamList;
import org.crossmobile.utils.Pom;

import static org.crossmobile.utils.ParamsCommon.CM_PLUGINS;

public class DependenciesParameter extends ProjectParameter {

    private final DependencyEditor comp;

    private String value;

    public DependenciesParameter(ParamList list) {
        super(list, CM_PLUGINS.tag());
        value = list.get(CM_PLUGINS.tag());
        comp = new DependencyEditor(list, Pom.unpackDependencies(value));
        comp.addDependencyListener(deps -> {
            value = Pom.packDependencies(deps);
            fireValueUpdated();
        });
    }

    @Override
    public void updatePropertyList() {
        super.updatePropertyList();
        comp.updatePropertyList();
    }

    @Override
    public String getVisualTag() {
        return "";
    }

    @Override
    public String getValue() {
        if (value == null)
            return "";
        return value;
    }

    @Override
    protected HiResComponent initVisuals() {
        return comp;
    }
}
