/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

class StreamParser {

    private boolean lastCharWasCR;
    private int wipedChars;
    private final StringBuilder line = new StringBuilder();
    private final BiConsumer<CharSequence, StreamQuality> print;
    private final Consumer<Integer> wipe;
    private StreamQuality quality = StreamQuality.BASIC;

    StreamParser(BiConsumer<CharSequence, StreamQuality> print, Consumer<Integer> wipe) {
        this.print = print;
        this.wipe = wipe;
    }

    void acceptChar(char c) {
        switch (c) {
            case '\r':
                lastCharWasCR = true;
                print(true);
                break;
            case '\n':
                if (lastCharWasCR)
                    lastCharWasCR = false;
                else
                    print(false);
                break;
            default:
                if (lastCharWasCR && wipedChars > 0) {
                    wipe.accept(wipedChars);
                    wipedChars = 0;
                }
                line.append(c);
                lastCharWasCR = false;
                break;
        }
    }

    private void print(boolean canBeRolledBack) {
        if (line.length() > 0) {
            wipedChars = canBeRolledBack ? line.length() + 1 : 0;
            String send = line.toString();
            if (send.startsWith("[INFO] ")) {
                send = send.substring(7);
                wipedChars -= 7;
                quality = StreamQuality.INFO;
            } else if (send.startsWith("[ERROR] ")) {
                send = send.substring(8);
                wipedChars -= 8;
                quality = StreamQuality.ERROR;
            } else if (send.startsWith("[DEBUG] ")) {
                send = send.substring(8);
                wipedChars -= 8;
                quality = StreamQuality.DEBUG;
            } else if (send.startsWith("[WARNING] ")) {
                send = send.substring(10);
                wipedChars -= 10;
                quality = StreamQuality.WARNING;
            } else if (send.isEmpty())
                quality = StreamQuality.BASIC;
            print.accept(send, quality);
            line.delete(0, line.length());
        } else
            wipedChars = 0;
    }

}
