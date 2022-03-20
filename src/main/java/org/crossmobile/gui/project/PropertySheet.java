/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import com.panayotis.hrgui.HiResComponent;
import org.crossmobile.gui.actives.ActivePanel;
import org.crossmobile.gui.parameters.ProjectParameter;
import org.crossmobile.utils.UIUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PropertySheet {

    private final List<ProjectParameter> properties = new ArrayList<>();
    private final List<SheetItem> visuals = new ArrayList<>();
    private final String name;
    private final GlobalParamListener listener;
    private ActivePanel bottomPanel;

    public PropertySheet(String name, GlobalParamListener listener) {
        this.name = name;
        this.listener = listener;
    }

    public void add(HiResComponent component) {
        visuals.add(new SheetItem(component));
    }

    public void add(ProjectParameter pp) {
        properties.add(pp);
        listener.addParameter(pp);
        visuals.add(new SheetItem(pp));
    }

    public Iterable<ProjectParameter> getProperties() {
        return properties;
    }

    public Iterable<HiResComponent> getVisuals() {
        return () -> new Iterator<HiResComponent>() {

            private final Iterator<SheetItem> it = visuals.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public HiResComponent next() {
                SheetItem item = it.next();
                return item.visual != null ? item.visual : (item.param != null ? item.param.getVisuals() : null);
            }
        };
    }

    public String getName() {
        return name;
    }

    public void alignVisuals() {
        Collection<Component> comps = new ArrayList<>();
        for (ProjectParameter param : properties) {
            HiResComponent cmp = param.getIndentedComponent();
            if (cmp != null)
                comps.add(cmp.comp());
        }
        UIUtils.syncWidth(comps);
    }

    public void setBottomPanel(ActivePanel bottomPanel) {
        this.bottomPanel = bottomPanel;
    }

    public ActivePanel getBottomPanel() {
        return bottomPanel;
    }

    private static final class SheetItem {
        private final ProjectParameter param;
        private final HiResComponent visual;

        private SheetItem(ProjectParameter param) {
            this.param = param;
            this.visual = null;
        }

        private SheetItem(HiResComponent visual) {
            this.param = null;
            this.visual = visual;
        }
    }
}
