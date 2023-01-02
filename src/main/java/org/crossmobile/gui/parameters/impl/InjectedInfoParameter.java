/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import com.panayotis.hrgui.HiResFont;
import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import java.awt.*;

import static org.crossmobile.utils.ParamsCommon.INJECTED_INFOPLIST;

public class InjectedInfoParameter extends FreeTextParameter {

    public InjectedInfoParameter(ParamList list) {
        super(list, INJECTED_INFOPLIST.tag(), Type.TEXTAREA);
    }

    @Override
    protected HiResFont getTextFont(Font font) {
        return new HiResFont("monospaced", Font.PLAIN, font.getSize());
    }

    @Override
    public String getVisualTag() {
        return "Injected Info.plist";
    }
}
