/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.elements.VersionDocumentFilter;
import org.crossmobile.gui.parameters.FreeTextParameter;
import org.crossmobile.utils.ParamList;

import static org.crossmobile.utils.ParamsCommon.BUNDLE_VERSION;

public class VersionParameter extends FreeTextParameter {

    private final boolean isPlugin;

    public VersionParameter(ParamList list, boolean isPlugin) {
        super(list, BUNDLE_VERSION.tag());
        this.isPlugin = isPlugin;
        setValue(getValue());
        setFilter(new VersionDocumentFilter());
        setTooltip(VersionDocumentFilter.TOOLTIP);
    }

    @Override
    public String getVisualTag() {
        return (isPlugin ? "Plugin" : "Application") + " version";
    }

    @Override
    public String getValue() {
        String value = super.getValue();
        String[] parts = value.split("\\.");
        StringBuilder out = new StringBuilder();
        for (int index = 0; index < 4 && index < parts.length; index++) {
            String part = parts[index];
            if (part != null)
                try {
                    part = part.trim();
                    int v = Integer.parseInt(part);
                    if (v < 0)
                        v = -v;
                    if (v > 255)
                        v = 255;
                    if (index == 0)
                        if (v > 127)
                            v = 127;
                    out.append(".").append(v);
                } catch (NumberFormatException ignored) {
                }
        }
        return out.length() < 1 ? "1" : out.substring(1);
    }

}
