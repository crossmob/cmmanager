/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.codehound.source;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SourcePatternFactory {

    private final static SourcePattern MAIN_PATTERN = new SourcePattern("Main", false, "publicstaticvoidmain", "staticpublicvoidmain");

    public static Set<SourcePattern> getMainClassPattern() {
        return new HashSet<>(Arrays.asList(MAIN_PATTERN.duplicate()));
    }

}
