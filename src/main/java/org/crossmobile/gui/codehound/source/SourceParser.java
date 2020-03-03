// (c) 2020 by Panayotis Katsaloulis
// SPDX-License-Identifier: AGPL-3.0-only

package org.crossmobile.gui.codehound.source;

import org.crossmobile.utils.ProjectException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        } else if (file.isDirectory()) {
            File[] other = file.listFiles();
            if (other != null)
                for (File item : other) {
                    String newpack = pack.isEmpty() ? item.getName() : (pack + "." + item.getName());
                    getFilesRecursively(item, newpack);
                }
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
