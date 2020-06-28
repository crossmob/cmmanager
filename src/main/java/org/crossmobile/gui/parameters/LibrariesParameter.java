/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import org.crossmobile.gui.elements.LibraryEditor;
import org.crossmobile.utils.ParamList;
import org.crossmobile.utils.Pom;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.crossmobile.utils.ParamsCommon.CM_PLUGINS;

public class LibrariesParameter extends ProjectParameter {

    private final LibraryEditor editor;

    public LibrariesParameter(ParamList list, File projectRoot) {
        super(list, CM_PLUGINS.tag());
        List<String> libraries = Pom.unpackDependencies(list.get(CM_PLUGINS.tag())).stream()
                .map(d -> d.groupId + ":" + d.artifactId + ":" + d.version)
                .collect(Collectors.toList());
        editor = new LibraryEditor(projectRoot, libraries);
    }

    @Override
    public String getVisualTag() {
        return "";
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    protected HiResComponent initVisuals() {
        return editor;
    }
}
