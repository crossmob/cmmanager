/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

import org.crossmobile.utils.ProjectException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class SourceDocument {

    private final static String[] BLACKLIST = {"import", "package", "//"};
    //
    private final List<String> lines = new ArrayList<>();
    private final String filename;
    private final String classname;
    private final String fulltext;

    SourceDocument(File file, String classname) throws ProjectException {
        this.filename = file.getPath();
        this.classname = classname;

        StringBuilder full = new StringBuilder();
        BufferedReader in = null;
        String line;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            next_line:
            while ((line = in.readLine()) != null) {
                line = line.trim();
                for (String current : BLACKLIST)
                    if (line.startsWith(current))
                        continue next_line;
                lines.add(line);
                full.append(line);
            }
            fulltext = full.toString().replaceAll("[\\t\\n\\r\\f ]", "");
        } catch (IOException ex) {
            throw new ProjectException("Error while parsing source file " + file.getPath(), ex);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException ex) {
                }
        }
    }

    void parse(Set<SourcePattern> list) {
        for (SourcePattern p : list)
            if (p.shouldMatchPerLine()) {
                for (int i = 0; i < lines.size(); i++)
                    if (p.match(filename, classname, lines.get(i), i + 1))
                        return;
            } else
                p.match(filename, classname, fulltext, -1);
    }
}
