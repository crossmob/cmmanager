/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import com.panayotis.hrgui.HiResButton;
import com.panayotis.hrgui.HiResPanel;
import org.crossmobile.gui.elements.KeystoreManager;
import org.crossmobile.gui.parameters.FreeFileParameter;
import org.crossmobile.gui.utils.Paths;
import org.crossmobile.gui.utils.Paths.HomeReference;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.ParamList;

import java.io.File;

import static org.crossmobile.utils.ParamsCommon.KEY_STORE;

public class AndroidKeyStoreParameter extends FreeFileParameter {

    public AndroidKeyStoreParameter(ParamList list) {
        super(list, KEY_STORE.tag(), Prefs.getAndroidKeyLocation(), true);
        setButtonPanelCallback((HiResPanel buttons) -> {
            HiResButton clear = new HiResButton("Default");
            clear.setToolTipText("Use Settings to set the default keystore location");
            clear.setOpaque(false);
            clear.addActionListener(ae -> setFile(new File(Prefs.getAndroidKeyLocation())));
            buttons.add(clear);
        });
    }

    @Override
    public String getVisualTag() {
        return "Key store location";
    }

    @Override
    public String getValue() {
        String sval = super.getValue();
        if (sval.isEmpty())
            return sval;
        return Paths.getPath(new File(properties.dereferenceValue(sval, true)), HomeReference.PROP_TO_ABS);
    }

    @Override
    protected boolean isFileAccepted(File givenFile) {
        return KeystoreManager.isKeystoreFile(givenFile);
    }
}
