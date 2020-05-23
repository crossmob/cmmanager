/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import javax.swing.*;
import java.util.List;

public class ProjectListModel extends AbstractListModel {

    private List<ProjectInfo> list;

    public ProjectListModel() {
        reload();
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public ProjectInfo getElementAt(int i) {
        return list.get(i);
    }

    public final void reload() {
        list = RecentsProjectManager.getProjects();
        fireContentsChanged(this, 0, list.size() - 1);
    }

    public int getIndex(ProjectInfo item) {
        return list.indexOf(item);
    }
}
