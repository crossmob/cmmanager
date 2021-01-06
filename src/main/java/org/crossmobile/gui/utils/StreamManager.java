/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import java.util.ArrayList;
import java.util.Collection;

public class StreamManager {

    private final StreamParser outP;
    private final StreamParser errP;
    private final Collection<StreamListener> listeners = new ArrayList<>();

    private boolean lastIsOut = true;

    public StreamManager() {
        outP = new StreamParser(this::addOutLine, this::rollbackOutLine);
        errP = new StreamParser(this::addErrLine, this::rollbackErrLine);
    }

    public void addListener(StreamListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    private synchronized void rollbackOutLine(int length) {
        if (lastIsOut)
            for (StreamListener listener : listeners)
                listener.removeChars(length);
        lastIsOut = true;
    }

    private synchronized void rollbackErrLine(int length) {
        if (!lastIsOut)
            for (StreamListener listener : listeners)
                listener.removeChars(length);
        lastIsOut = false;
    }

    private synchronized void addOutLine(CharSequence out, StreamQuality quality) {
        for (StreamListener listener : listeners)
            listener.addLine(out, quality);
        lastIsOut = true;
    }

    private synchronized void addErrLine(CharSequence out, StreamQuality quality) {
        for (StreamListener listener : listeners)
            listener.addLine(out, quality == StreamQuality.BASIC ? StreamQuality.ERROR : quality);
        lastIsOut = false;
    }

    public void incomingOutChar(char c) {
        outP.acceptChar(c);
    }

    public void incomingErrChar(char c) {
        errP.acceptChar(c);
    }

    public void clearListeners() {
        listeners.clear();
    }

}
