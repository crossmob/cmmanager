/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.crossmobile.gui.elements;

import com.panayotis.hrgui.HiResButton;
import com.panayotis.hrgui.HiResComboBox;
import com.panayotis.hrgui.HiResOptions;
import org.crossmobile.gui.actives.ActiveButton;
import org.crossmobile.gui.actives.ActiveIcon;
import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.gui.actives.ActiveRadioButton;
import org.crossmobile.utils.Dependency;
import org.crossmobile.utils.PluginRegistry;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.crossmobile.gui.actives.ActiveComboBox;
import org.crossmobile.gui.actives.ActiveTextField;

public final class DependencyCreator extends javax.swing.JFrame {

    private final ComboBoxModel<Dependency> dependecyModel;
    private final Consumer<Dependency> dependencyCallback;
    private final String typename;
    private final boolean isTheme;
    private final String appId;

    public DependencyCreator(Consumer<Dependency> dependency, boolean theme, String appId) {
        List<Dependency> deplist = new ArrayList<>();
        Map<String, Collection<Dependency>> categories = theme ? PluginRegistry.getCategorizedSystemThemes() : PluginRegistry.getCategorizedSystemPlugins();
        for (String name : categories.keySet()) {
            if (categories.size() != 1) {
                deplist.add(getTitle(name));
            }
            deplist.addAll(categories.get(name));
        }

        dependecyModel = new DefaultComboBoxModel<Dependency>(deplist.toArray(new Dependency[]{})) {
            @Override
            public void setSelectedItem(Object item) {
                if (isTitle(item)) {
                    return;
                }
                super.setSelectedItem(item);
            }
        };
        initComponents();
        cmPlugin.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                boolean isTitle = isTitle(value);
                Component comp = super.getListCellRendererComponent(list, isTitle ? "    " + ((Dependency) value).artifactId : value, index, !isTitle && isSelected, !isTitle && cellHasFocus);
                if (isTitle(value)) {
                    comp.setForeground(Color.gray);
                }
                return comp;
            }
        });
  //      cmPlugin.setSelectedIndex(1);

        this.dependencyCallback = dependency;
        enableGroup(true);
        if (theme) {
            addB.setText("Set");
        }
        typename = theme ? "Theme" : "Plugin";
        crossSelection.setText(crossSelection.getText() + typename);
        customSelection.setText(customSelection.getText() + typename);
        setLocationRelativeTo(null);
        prefixArtifact.setText(theme ? "cmtheme-" : "cmplugin-");
        this.isTheme = theme;
        this.appId = appId;
    }

    private void enableGroup(boolean asCrossMobile) {
        cmPlugin.setEnabled(asCrossMobile);
        info.setEnabled(asCrossMobile);
        group.setEnabled(!asCrossMobile);
        groupL.setEnabled(!asCrossMobile);
        artifact.setEnabled(!asCrossMobile);
        artifactL.setEnabled(!asCrossMobile);
        version.setEnabled(!asCrossMobile);
        versionL.setEnabled(!asCrossMobile);
        prefixArtifact.setEnabled(!asCrossMobile);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pluginSel = new javax.swing.ButtonGroup();
        gradientP = new GradientPanel();
        customP = new javax.swing.JPanel();
        customSelection = new ActiveRadioButton();
        customListP = new javax.swing.JPanel();
        customLeftP = new javax.swing.JPanel();
        groupL = new ActiveLabel();
        artifactL = new ActiveLabel();
        versionL = new ActiveLabel();
        customRightP = new javax.swing.JPanel();
        group = new ActiveTextField();
        jPanel1 = new javax.swing.JPanel();
        prefixArtifact = new ActiveLabel();
        artifact = new ActiveTextField();
        version = new ActiveTextField();
        crossGP = new javax.swing.JPanel();
        crossSelection = new ActiveRadioButton();
        crossP = new javax.swing.JPanel();
        cmPlugin = new ActiveComboBox<>(dependecyModel);
        info = new ActiveButton();
        commandsP = new javax.swing.JPanel();
        addB = new HiResButton();
        cancelB = new HiResButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        gradientP.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        gradientP.setLayout(new java.awt.BorderLayout());

        customP.setOpaque(false);
        customP.setLayout(new java.awt.BorderLayout());

        pluginSel.add(customSelection);
        customSelection.setText("Use a custom Maven Artifact ");
        customSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customSelectionActionPerformed(evt);
            }
        });
        customP.add(customSelection, java.awt.BorderLayout.NORTH);

        customListP.setOpaque(false);
        customListP.setLayout(new java.awt.BorderLayout());

        customLeftP.setOpaque(false);
        customLeftP.setLayout(new java.awt.GridLayout(0, 1));

        groupL.setText("Group ID");
        customLeftP.add(groupL);

        artifactL.setText("Artifact ID");
        customLeftP.add(artifactL);

        versionL.setText("Version");
        customLeftP.add(versionL);

        customListP.add(customLeftP, java.awt.BorderLayout.WEST);

        customRightP.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 0));
        customRightP.setOpaque(false);
        customRightP.setLayout(new java.awt.GridLayout(0, 1));

        group.setColumns(30);
        customRightP.add(group);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(prefixArtifact, java.awt.BorderLayout.WEST);

        artifact.setColumns(30);
        jPanel1.add(artifact, java.awt.BorderLayout.CENTER);

        customRightP.add(jPanel1);

        version.setColumns(30);
        customRightP.add(version);

        customListP.add(customRightP, java.awt.BorderLayout.CENTER);

        customP.add(customListP, java.awt.BorderLayout.CENTER);

        gradientP.add(customP, java.awt.BorderLayout.CENTER);

        crossGP.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 16, 0));
        crossGP.setOpaque(false);
        crossGP.setLayout(new java.awt.BorderLayout());

        pluginSel.add(crossSelection);
        crossSelection.setSelected(true);
        crossSelection.setText("Use a CrossMobile ");
        crossSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crossSelectionActionPerformed(evt);
            }
        });
        crossGP.add(crossSelection, java.awt.BorderLayout.NORTH);

        crossP.setOpaque(false);
        crossP.setLayout(new java.awt.BorderLayout());
        crossP.add(cmPlugin, java.awt.BorderLayout.CENTER);

        info.setIcon(new ActiveIcon("images/info"));
        info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoActionPerformed(evt);
            }
        });
        crossP.add(info, java.awt.BorderLayout.EAST);

        crossGP.add(crossP, java.awt.BorderLayout.CENTER);

        gradientP.add(crossGP, java.awt.BorderLayout.NORTH);

        commandsP.setOpaque(false);
        commandsP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        addB.setText("Add");
        addB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBActionPerformed(evt);
            }
        });
        commandsP.add(addB);

        cancelB.setText("Cancel");
        cancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBActionPerformed(evt);
            }
        });
        commandsP.add(cancelB);

        gradientP.add(commandsP, java.awt.BorderLayout.SOUTH);

        getContentPane().add(gradientP, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBActionPerformed
        Dependency dep = crossSelection.isSelected()
                ? (Dependency) cmPlugin.getSelectedItem()
                : PluginRegistry.find(group.getText(), prefixArtifact.getText() + artifact.getText(), version.getText(), null, null, null);
        if (dep != null) {
            dependencyCallback.accept(dep);
            setVisible(false);
        } else
            new HiResOptions().parent(this).message("The provided plugin is not valid").title("Error while adding plugin").error().show();
    }//GEN-LAST:event_addBActionPerformed

    private void cancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelBActionPerformed

    private void customSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customSelectionActionPerformed
        enableGroup(false);
    }//GEN-LAST:event_customSelectionActionPerformed

    private void crossSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crossSelectionActionPerformed
        enableGroup(true);
    }//GEN-LAST:event_crossSelectionActionPerformed

    private void infoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoActionPerformed
        Dependency dep = (Dependency) cmPlugin.getSelectedItem();
        showInfo(DependencyCreator.this, dep.description, dep.url, typename);
    }//GEN-LAST:event_infoActionPerformed

    private static Dependency getTitle(String title) {
        return PluginRegistry.find("*", title, "", "", "", "");
    }

    private static boolean isTitle(Object o) {
        return o instanceof Dependency && ((Dependency) o).groupId.equals("*");
    }

    public static void showInfo(Component parent, String description, String url, String type) {
        URI uri = url == null ? null : URI.create(url);
        if (uri != null) {
            String msg = description + "\n\nPlease click on `More info` for more information about usage and configuration of this plugin";
            if (new HiResOptions().parent(parent).message(msg).title(type + " information").buttons("More info", "OK").show() == 0) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ignore) {
                }
            }
        } else {
            new HiResOptions().parent(parent).message(description).title(type + " information").show();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addB;
    private javax.swing.JTextField artifact;
    private javax.swing.JLabel artifactL;
    private javax.swing.JButton cancelB;
    private javax.swing.JComboBox<Dependency> cmPlugin;
    private javax.swing.JPanel commandsP;
    private javax.swing.JPanel crossGP;
    private javax.swing.JPanel crossP;
    private javax.swing.JRadioButton crossSelection;
    private javax.swing.JPanel customLeftP;
    private javax.swing.JPanel customListP;
    private javax.swing.JPanel customP;
    private javax.swing.JPanel customRightP;
    private javax.swing.JRadioButton customSelection;
    private javax.swing.JPanel gradientP;
    private javax.swing.JTextField group;
    private javax.swing.JLabel groupL;
    private javax.swing.JButton info;
    private javax.swing.JPanel jPanel1;
    private javax.swing.ButtonGroup pluginSel;
    private javax.swing.JLabel prefixArtifact;
    private javax.swing.JTextField version;
    private javax.swing.JLabel versionL;
    // End of variables declaration//GEN-END:variables
}
