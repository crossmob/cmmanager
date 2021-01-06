/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import org.crossmobile.gui.elements.LibraryEditor;
import org.crossmobile.gui.utils.CMMvnActions;
import org.crossmobile.utils.ParamList;
import org.crossmobile.utils.Pom;

import java.io.File;

import static org.crossmobile.utils.ParamsCommon.CM_PLUGINS;

public class LibrariesParameter extends ProjectParameter {

    private final LibraryEditor editor;
    private String value;

    public LibrariesParameter(ParamList list, File projectRoot, CMMvnActions.MavenExecutor fetchEvent) {
        super(list, CM_PLUGINS.tag());
        value = list.get(CM_PLUGINS.tag());
        editor = new LibraryEditor(projectRoot, Pom.unpackDependencies(value), v -> {
            if (!v.equals(value)) {
                value = v;
                fireValueUpdated();
            }
        }, fetchEvent);
    }

    @Override
    public String getVisualTag() {
        return "";
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    protected HiResComponent initVisuals() {
        return editor;
    }
}
