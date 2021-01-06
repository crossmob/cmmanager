/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import com.panayotis.hrgui.HiResPanel;
import org.crossmobile.gui.actives.ActiveButton;
import org.crossmobile.gui.actives.ActiveIcon;
import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.utils.*;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

public class DependencyEditor extends HiResPanel {

    private final Collection<DependencyEntry> dependencies = new TreeSet<>();
    private Dependency theme;
    private final Set<Consumer<Collection<Dependency>>> listeners = new HashSet<>();
    private final ParamList list;

    public DependencyEditor(ParamList list, Collection<Dependency> deps) {
        initComponents();
        this.list = list;
        for (Dependency dep : deps)
            appendDependencySilently(dep);
    }

    public void updatePropertyList() {
        for (DependencyEntry entry : dependencies)
            for (DependencyParam param : entry.dependency.getConfigParams())
                list.getProperties().setProperty(param.fullname, entry.getValue(param));
    }

    // Callback when changes are performed
    public void addDependencyListener(Consumer<Collection<Dependency>> listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void fireDependenciesChange() {
        Collection<Dependency> deps = new ArrayList<>();
        if (theme != null)
            deps.add(theme);
        for (DependencyEntry entry : dependencies)
            deps.add(entry.dependency);
        deps = Collections.unmodifiableCollection(deps);
        for (Consumer<Collection<Dependency>> listener : listeners)
            listener.accept(deps);
    }

    private void appendDependencySilently(Dependency dep) {
        if (dep.theme)
            setThemeSilently(dep);
        else {
            DependencyEntry entry = new DependencyEntry(dep);
            if (!dependencies.contains(entry)) {
                entry.init(list);
                dependencies.add(entry);
                entry.setDeleteCallback(() -> removeDependency(entry));
                for (DependencyParam param : dep.getConfigParams())
                    list.getParamset().register(param.param);
                pluginsP.add(entry);
                rearrange();
            }
        }
    }

    private void setThemeSilently(Dependency theme) {
        themeL.setText(theme.getDisplayName());
        themeL.setToolTipText(theme.getInformation());
        this.theme = theme;
    }

    private void appendDependency(Dependency dep) {
        appendDependencySilently(dep);
        fireDependenciesChange();
    }

    private void removeDependency(DependencyEntry entry) {
        dependencies.remove(entry);
        pluginsP.remove(entry);
        for (DependencyParam param : entry.dependency.getConfigParams())
            list.getParamset().unregister(param.param);
        rearrange();
        fireDependenciesChange();
    }

    private void setTheme(Dependency theme) {
        setThemeSilently(theme);
        fireDependenciesChange();
    }

    private void rearrange() {
        Collection<Component> comps = new ArrayList<>();
        for (DependencyEntry entry : dependencies)
            for (Component c : entry.getIndentVisuals())
                comps.add(c);
        UIUtils.syncWidth(comps);
        pluginsP.revalidate();
        pluginsP.repaint();
    }

    private String getAppId() {
        return list.get(ParamsCommon.GROUP_ID.tag()) + "." + list.get(ParamsCommon.ARTIFACT_ID.tag());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        themeP = new javax.swing.JPanel();
        jLabel3 = new ActiveLabel();
        jPanel5 = new javax.swing.JPanel();
        editThemeB = new ActiveButton();
        themeL = new ActiveLabel();
        pluginwrapP = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new ActiveLabel();
        addB = new ActiveButton();
        pluginsP = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        themeP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().line));
        themeP.setOpaque(false);
        themeP.setLayout(new java.awt.BorderLayout());

        jLabel3.setText("Theme");
        themeP.add(jLabel3, java.awt.BorderLayout.NORTH);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 16, 5));

        editThemeB.setIcon(new ActiveIcon("images/edit"));
        editThemeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editThemeBActionPerformed(evt);
            }
        });
        jPanel5.add(editThemeB);
        jPanel5.add(themeL);

        themeP.add(jPanel5, java.awt.BorderLayout.CENTER);

        add(themeP);

        pluginwrapP.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 0, 0));
        pluginwrapP.setOpaque(false);
        pluginwrapP.setLayout(new java.awt.BorderLayout());

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Plugins");
        jPanel3.add(jLabel2, java.awt.BorderLayout.CENTER);

        addB.setIcon(new ActiveIcon("images/add"));
        addB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBActionPerformed(evt);
            }
        });
        jPanel3.add(addB, java.awt.BorderLayout.EAST);

        pluginwrapP.add(jPanel3, java.awt.BorderLayout.NORTH);

        pluginsP.setOpaque(false);
        pluginsP.setLayout(new javax.swing.BoxLayout(pluginsP, javax.swing.BoxLayout.Y_AXIS));
        pluginwrapP.add(pluginsP, java.awt.BorderLayout.CENTER);

        add(pluginwrapP);
    }// </editor-fold>//GEN-END:initComponents

    private void addBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBActionPerformed
        new DependencyCreator(this::appendDependency, false, getAppId()).setVisible(true);
    }//GEN-LAST:event_addBActionPerformed

    private void editThemeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editThemeBActionPerformed
        new DependencyCreator(this::setTheme, true, getAppId()).setVisible(true);
    }//GEN-LAST:event_editThemeBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addB;
    private javax.swing.JButton editThemeB;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel pluginsP;
    private javax.swing.JPanel pluginwrapP;
    private javax.swing.JLabel themeL;
    private javax.swing.JPanel themeP;
    // End of variables declaration//GEN-END:variables

}
