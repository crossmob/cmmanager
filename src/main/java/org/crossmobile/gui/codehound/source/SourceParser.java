/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

import org.crossmobile.utils.ProjectException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.crossmobile.bridge.system.BaseUtils.listFiles;

public class SourceParser {

    private static final String[] FILES = {".java"};
    //
    private final List<SourceDocument> sourcefiles = new ArrayList<>();
    private Set<SourcePattern> requirements;

    public SourceParser(String basedir) throws ProjectException {
        getFilesRecursively(new File(basedir), "");
    }

    private void getFilesRecursively(File file, String pack) throws ProjectException {
        if (file.isFile()) {
            String lower = file.getName().toLowerCase();
            for (String ext : FILES)
                if (lower.endsWith(ext)) {
                    sourcefiles.add(new SourceDocument(file, pack.substring(0, pack.length() - 5)));  // remove extension
                    return;
                }
        } else if (file.isDirectory())
            for (File item : listFiles(file)) {
                String newPack = pack.isEmpty() ? item.getName() : (pack + "." + item.getName());
                getFilesRecursively(item, newPack);
            }
    }

    public void setPattern(Set<SourcePattern> permissions) {
        this.requirements = permissions;
    }

    public List<SourcePattern> parse() {
        for (SourceDocument doc : sourcefiles)
            doc.parse(requirements);
        List<SourcePattern> found = new ArrayList<>();
        for (SourcePattern req : requirements)
            if (req.isFound())
                found.add(req);
        return found;
    }
}
