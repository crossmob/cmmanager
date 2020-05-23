/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.HiResComponent;
import org.crossmobile.utils.Param;
import org.crossmobile.utils.ParamList;

public abstract class HiddenParameter extends ProjectParameter {

    public HiddenParameter(ParamList prop, Param key) {
        super(prop, key);
    }

    @Override
    protected HiResComponent initVisuals() {
        return null;
    }

    @Override
    public String getVisualTag() {
        return "";
    }
}
