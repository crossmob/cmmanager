/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

public interface StreamListener {

    void addLine(CharSequence line, StreamQuality quality);

    default void removeChars(int size) {
    }

    StreamListener NONE = (line, quality) -> {
    };
}
