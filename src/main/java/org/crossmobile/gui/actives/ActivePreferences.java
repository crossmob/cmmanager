package org.crossmobile.gui.actives;

import com.panayotis.appenh.EnhancerManager;
import com.panayotis.hrgui.HiResPreferences;

import java.util.prefs.Preferences;

public class ActivePreferences implements HiResPreferences {
    private static final String SCALE_TAG = "ui_scale";

    @Override
    public float scaleFactor() {
        return getPreferences().getFloat(SCALE_TAG, EnhancerManager.getDefault().getDPI() / 96f);
    }

    @Override
    public boolean storePrefs(float scaleFactor) {
        getPreferences().putFloat(SCALE_TAG, scaleFactor);
        return true;
    }

    private Preferences getPreferences() {
        return Preferences.systemNodeForPackage(ActivePreferences.class);
    }
}
