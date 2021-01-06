/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import org.crossmobile.gui.parameters.RadioParameter;
import org.crossmobile.gui.utils.Profile;
import org.crossmobile.utils.CollectionUtils;
import org.crossmobile.utils.ParamList;
import org.crossmobile.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.crossmobile.utils.CollectionUtils.asList;

public class ProfileParameter extends RadioParameter {
    private static final List<String> PROFILES;

    static {
        PROFILES = new ArrayList<>();
        for (Profile p : Profile.values())
            if (p.isValid())
                PROFILES.add(p.name().toLowerCase());
    }

    public ProfileParameter(ParamList list, Profile defaultType) {
        super(list, null, asList(PROFILES, a -> "images/" + a),
                asList(PROFILES, TextUtils::capitalize),
                PROFILES, defaultType.name().toLowerCase(), false);
    }

    @Override
    protected String getVisualTag() {
        return "Profile selection";
    }

    @Override
    public boolean shouldTrackChanges() {
        return false;
    }
}
