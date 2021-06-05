/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import com.panayotis.hrgui.HiResLabel;

public class DisplayInfoParameter extends ProjectParameter {

    private final String header;
    private final String text;

    public DisplayInfoParameter(String text) {
        this(null, text);
    }

    public DisplayInfoParameter(String header, String text) {
        super(null, null);
        this.header = header;
        this.text = text;
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    protected String getVisualTag() {
        return "";
    }

    @Override
    protected HiResComponent initVisuals() {
        return new HiResLabel(text);
    }
}
