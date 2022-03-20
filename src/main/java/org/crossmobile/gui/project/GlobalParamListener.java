/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.project;

import org.crossmobile.gui.parameters.ParameterListener;
import org.crossmobile.gui.parameters.ProjectParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GlobalParamListener {

    private BiConsumer<Boolean, String> titleListener;
    private String ctitle = "";
    private String lastTitle = "";
    private final Collection<PListener> listeners = new ArrayList<>();

    public void updateTitle(String newTitle) {
        ctitle = newTitle == null ? "" : newTitle;
        updateTitle();
    }

    private void updateTitle() {
        if (titleListener != null) {
            String newTitle = ctitle + (isDirty() ? " *" : "");
            if (!newTitle.equals(lastTitle))
                titleListener.accept(isDirty(), lastTitle = newTitle);
        }
    }

    public void setApplicationNameListener(BiConsumer<Boolean, String> listener) {
        this.titleListener = listener;
    }

    public void addParameter(ProjectParameter pp) {
        PListener plistener = new PListener(pp);
        listeners.add(plistener);
        pp.addParameterListener(plistener);
    }

    public boolean isDirty() {
        for (PListener listener : listeners)
            if (listener.dirty)
                return true;
        return false;
    }

    public void updateDefaults() {
        for (PListener listener : listeners)
            listener.updateDefaults();
        updateTitle();
    }

    private class PListener implements ParameterListener {

        private String initialValue;
        private final ProjectParameter parameter;
        private boolean dirty;

        public PListener(ProjectParameter parameter) {
            this.parameter = parameter;
            initialValue = parameter.getValue();
        }

        @Override
        public void updateParameter(ProjectParameter parameter) {
            dirty = parameter.shouldTrackChanges() && !Objects.equals(initialValue, parameter.getValue());
            updateTitle();
        }

        private void updateDefaults() {
            initialValue = parameter.getValue();
            dirty = false;
        }
    }
}
