/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.properties;

public interface PropertyList {

    public String get(String key);

    public void put(String key, String value);

    public void putIfMissing(String APPLICATION_NAME, String get);

    public void remove(String key);
}
