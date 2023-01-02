/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.gui.parameters.impl.GroupIdParameter;
import org.crossmobile.utils.*;

import java.io.File;
import java.util.Properties;

import static java.util.Collections.singletonList;
import static org.crossmobile.bridge.system.BaseUtils.listFiles;
import static org.crossmobile.prefs.Config.*;
import static org.crossmobile.utils.ParamsCommon.*;
import static org.crossmobile.utils.TemplateUtils.copyTemplate;

public class ProjectUpdater {

    public static void update(File basedir, ParamList list) throws ProjectException {
        // Create pom
        File pom = new File(basedir, "pom.xml");
        if (!pom.isFile())
            copyTemplate("pom_xml", pom, list, null);

        // Move source files
        File javaDir = new File(basedir, "src" + File.separator + "main" + File.separator + "java");
        javaDir.mkdirs();
        File old;
        if ((old = new File(basedir, "src" + File.separator + "java")).exists()) {
            FileUtils.copy(old, javaDir);
            FileUtils.delete(old);
        }

        // Move old artwork
        if ((old = new File(basedir, "artwork")).exists()) {
            FileUtils.copy(old, new File(basedir, "src/main/artwork"));
            FileUtils.delete(old);
        }
        // Move icons (after artwork relocation)
        File iconDir = new File(basedir, ICON_DIR);
        listFiles(new File(basedir, "src/main/artwork")).stream().filter(f -> f.getName().toLowerCase().startsWith("icon") && f.getName().toLowerCase().endsWith(".png"))
                .forEach(icon -> FileUtils.move(icon, new File(iconDir, icon.getName()), null));

        // Move old src/main/artwork
        if ((old = new File(basedir, "src/main/artwork")).exists()) {
            FileUtils.copy(old, new File(basedir, MATERIALS_PATH));
            FileUtils.delete(old);
        }

        // Update to adaptive icons
        File iconFore = new File(basedir, FORE_ICONS);
        listFiles(iconDir).stream().filter(f -> f.isFile() && f.getName().toLowerCase().endsWith(".png"))
                .forEach(icon -> FileUtils.move(icon, new File(iconFore, icon.getName()), null));

        // Maybe obsolete
        if ((old = new File(basedir, "src/main/cmresources")).exists()) {
            FileUtils.copy(old, new File(basedir, MATERIALS_PATH));
            FileUtils.delete(old);
        }
    }

    public static void updateOldToNew(Properties props) {
        String group = "", artifact = "", displayname;
        String id = props.getProperty("bundle.identifier");
        if (id != null) {
            int dot = id.lastIndexOf(".");
            group = dot < 0 ? GroupIdParameter.DEFAULT_GROUP_ID : id.substring(0, dot);
            artifact = dot < 0 ? id : id.substring(dot + 1);
        }
        displayname = props.getProperty("bundle.displayname");
        if (displayname == null || displayname.isEmpty())
            displayname = props.getProperty("application.name");
        if (displayname == null || displayname.isEmpty())
            displayname = artifact;

        mightUpdateProperty(props, DISPLAY_NAME.tag(), displayname);
        mightUpdateProperty(props, ARTIFACT_ID.tag(), artifact);
        mightUpdateProperty(props, GROUP_ID.tag(), group);
        mightUpdateProperty(props, CM_PLUGINS.tag(), Pom.packDependencies(singletonList(PluginRegistry.getDefaultTheme())));
    }

    private static void mightUpdateProperty(Properties props, Param tag, String newvalue) {
        if (newvalue == null || newvalue.isEmpty())
            return;
        String cvalue = props.getProperty(tag.name);
        if (cvalue != null && !cvalue.isEmpty())
            return;
        props.setProperty(tag.name, newvalue);
    }

}
