/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.lic;

import com.panayotis.hrgui.HiResIcon;
import org.crossmobile.gui.actives.ActiveIcon;
import org.crossmobile.utils.Dependency;
import org.crossmobile.utils.launcher.Flavour;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class LicenseTreeRenderer extends DefaultTreeCellRenderer {

    public static final String EMPTY = "_";
    public static final String ID = "#";

    private static final HiResIcon appIdIcon = new ActiveIcon("images/appid_lic");
    private static final HiResIcon pluginIcon = new ActiveIcon("images/plugins_lic");
    private static final HiResIcon iosIcon = new ActiveIcon("images/ios_lic");
    private static final HiResIcon androidIcon = new ActiveIcon("images/android_lic");
    private static final HiResIcon desktopIcon = new ActiveIcon("images/desktop_lic");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object node, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, node, sel, expanded, leaf, row, hasFocus);
        if (node instanceof DefaultMutableTreeNode) {
            Object value = ((DefaultMutableTreeNode) node).getUserObject();
            if (value instanceof String) {
                String txt = (String) value;
                if (txt.startsWith(EMPTY)) {
                    setIcon(null);
                    setText(txt.substring(1));
                } else if (txt.startsWith(ID)) {
                    txt = txt.substring(1);
                    setIcon(appIdIcon);
                    setText("*".equals(txt) ? "All Applications" : txt);
                } else {
                    setIcon(null);
                    setText(txt);
                }
            } else if (value instanceof Dependency) {
                setIcon(pluginIcon);
                setText(((Dependency) value).name);
            } else if (value instanceof Flavour)
                switch ((Flavour) value) {
                    case IOS:
                        setIcon(iosIcon);
                        setText("iOS");
                        break;
                    case ANDROID:
                        setIcon(androidIcon);
                        setText("Android");
                        break;
                    case DESKTOP:
                        setIcon(desktopIcon);
                        setText("Desktop");
                        break;
                }
        }
        return this;
    }

}
