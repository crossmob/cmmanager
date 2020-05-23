/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import static net.gcardone.junidecode.Junidecode.unidecode;

public class NameConverter {

    public static String unicodeToAsciiID(String input) {
        StringBuilder out = new StringBuilder();
        for (char c : unidecode(input.toLowerCase()).toCharArray())
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '_')
                out.append(c);
        return out.toString();
    }
}
