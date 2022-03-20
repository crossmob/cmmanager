/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

public class LocationHit {

    private final String line;
    private final int linenumber;
    private final int column;

    LocationHit(String line, int lineID, int column) {
        this.line = lineID < 0 ? "" : line;
        this.linenumber = lineID;
        this.column = column;
    }

    public void dump() {
        if (linenumber >= 0)
            System.out.println("    [" + linenumber + "," + column + "] " + line.trim());
    }

    public int getColumn() {
        return column;
    }

    public String getLine() {
        return line;
    }

    public int getLineNumber() {
        return linenumber;
    }
}
