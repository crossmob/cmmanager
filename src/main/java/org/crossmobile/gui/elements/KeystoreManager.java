/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import com.panayotis.appenh.AFileChooser;
import com.panayotis.appenh.Enhancer;
import com.panayotis.hrgui.HiResOptions;
import org.crossmobile.prefs.Prefs;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.panayotis.appenh.AFileChooser.FileSelectionMode.FilesOnly;

public class KeystoreManager {
    private static final AFileChooser afc = new AFileChooser().setMode(FilesOnly).setRememberSelection(true);

    public static String browseKeystore() {
        File keystore = afc.setDirectory(new File(Prefs.getAndroidKeyLocation())).openSingle();
        return keystore != null && keystore.exists() && isKeystoreFile(keystore)
                ? keystore.getAbsolutePath()
                : null;
    }

    public static boolean isKeystoreFile(File file) {
        if (getKeystore(file) == null) {
            new HiResOptions().message("File " + file.getAbsolutePath() + " is not a valid Java Keystore").title( "Error while parsing keystore file").error().show();
            return false;
        } else
            return true;
    }

    public static List<String> getKeystoreAliases(File file) {
        List<String> list = new ArrayList<>();
        KeyStore ks = getKeystore(file);
        if (ks != null) {
            Enumeration<String> aliases;
            try {
                aliases = ks.aliases();
                while (aliases.hasMoreElements())
                    list.add(aliases.nextElement());
            } catch (KeyStoreException ignored) {
            }
        }
        return list;
    }

    @SuppressWarnings("UseSpecificCatch")
    private static KeyStore getKeystore(File file) {
        java.io.FileInputStream fis = null;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new java.io.FileInputStream(file);
            ks.load(fis, null); // no password
            return ks;
        } catch (Exception ignored) {
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
        }
        return null;
    }
}
