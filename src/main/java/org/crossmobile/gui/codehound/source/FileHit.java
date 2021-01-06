/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

import java.util.ArrayList;
import java.util.List;

public class FileHit {

    private final String path;
    private final String classname;
    private final List<LocationHit> locations;

    public FileHit(String path, String classname) {
        this.path = path;
        this.classname = classname;
        locations = new ArrayList<>();
    }

    public void dump() {
        System.out.println("  " + path);
        for (LocationHit hit : locations)
            hit.dump();
    }

    void add(LocationHit locationHit) {
        if (locationHit != null)
            locations.add(locationHit);
    }

    public String getPath() {
        return path;
    }

    public String getClassName() {
        return classname;
    }
}
