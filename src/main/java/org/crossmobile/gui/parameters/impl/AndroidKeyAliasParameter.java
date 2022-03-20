/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters.impl;

import com.panayotis.hrgui.HiResButton;
import com.panayotis.hrgui.HiResComponent;
import com.panayotis.hrgui.HiResPanel;
import org.crossmobile.gui.elements.KeystoreManager;
import org.crossmobile.gui.parameters.ParameterListener;
import org.crossmobile.gui.parameters.ProjectParameter;
import org.crossmobile.gui.parameters.SelectionListParameter;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.ParamDisplay;
import org.crossmobile.utils.ParamDisplay.GenericParamDisplay;
import org.crossmobile.utils.ParamList;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.crossmobile.utils.ParamsCommon.KEY_ALIAS;
import static org.crossmobile.utils.ParamsCommon.KEY_STORE;

public class AndroidKeyAliasParameter extends SelectionListParameter implements ParameterListener {

    public AndroidKeyAliasParameter(ParamList list) {
        super(list, KEY_ALIAS.tag(), getItems(list));
    }

    @Override
    public String getVisualTag() {
        return "Key alias";
    }

    @Override
    protected HiResComponent initVisuals() {
        HiResComponent coreVisuals = super.initVisuals();
        HiResButton clear = new HiResButton("Update");
        clear.setOpaque(false);
        clear.addActionListener(event -> updateVisuals(getItems(properties), true));
        HiResPanel panel = new HiResPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(coreVisuals.comp(), BorderLayout.CENTER);
        panel.add(clear, BorderLayout.EAST);
        return panel;
    }

    private static Collection<ParamDisplay> getItems(ParamList list) {
        Collection<ParamDisplay> pd = new ArrayList<>();
        String deflt = list.get(KEY_ALIAS.tag());
        String keystore = list.get(KEY_STORE.tag());
        if (keystore == null)
            keystore = Prefs.getAndroidKeyLocation();
        keystore = list.dereferenceValue(keystore, true);
        List<String> aliases = KeystoreManager.getKeystoreAliases(new File(keystore));
        if (!aliases.isEmpty()) {
            int index = 0;
            if (deflt != null)
                index = aliases.indexOf(deflt);
            if (index < 0)
                index = 0;
            for (int i = 0; i < aliases.size(); i++)
                pd.add(new GenericParamDisplay(aliases.get(i), null, null, index == i));
        } else
            pd.add(new GenericParamDisplay("", null, null, false));
        return pd;
    }

    @Override
    public void updateParameter(ProjectParameter parameter) {
        updateVisuals(getItems(properties), true);
    }

}
