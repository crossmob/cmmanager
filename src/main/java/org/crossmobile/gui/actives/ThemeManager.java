/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.actives;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;

public class ThemeManager {

    private static final HashSet<WeakReference<ThemeChanged>> listeners = new HashSet<>();

    static void register(ThemeChanged themeListener) {
        listeners.add(new WeakReference<>(themeListener));
    }

    public static void notifyListeners(boolean dark) {
        Iterator<WeakReference<ThemeChanged>> it = listeners.iterator();
        while (it.hasNext()) {
            WeakReference<ThemeChanged> next = it.next();
            ThemeChanged listener = next.get();
            if (listener == null)
                it.remove();
            else
                listener.themeChanged(dark);
        }
    }
}
