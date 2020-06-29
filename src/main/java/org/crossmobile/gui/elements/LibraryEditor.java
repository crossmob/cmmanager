/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.gui.actives.ActiveList;
import org.crossmobile.gui.actives.ActivePanel;
import org.crossmobile.gui.codehound.source.AndroidParser;
import org.crossmobile.utils.Dependency;
import org.crossmobile.utils.Pom;
import org.robovm.objc.block.VoidBlock1;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static org.crossmobile.utils.CollectionUtils.asList;

public class LibraryEditor extends ActivePanel {

    private final File projectRoot;
    private final VoidBlock1<String> callback;
    private List<Dependency> dependencies;

    public LibraryEditor(File projectRoot, List<Dependency> libraries, VoidBlock1<String> callback) {
        this.projectRoot = projectRoot;
        this.callback = callback;
        initComponents();
        updateModel(libraries);
    }

    private void updateModel(List<Dependency> model) {
        dependencies = model;
        List<Dependency> filtered = AndroidParser.filterShadow(dependencies);
        deplist.setModel(new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return filtered.size();
            }

            @Override
            public String getElementAt(int index) {
                Dependency dependency = filtered.get(index);
                return dependency.groupId + " : " + dependency.artifactId;
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel topP = new javax.swing.JPanel();
        javax.swing.JLabel titleL = new ActiveLabel();
        javax.swing.JButton recalculateB = new javax.swing.JButton();
        javax.swing.JScrollPane scrollS = new javax.swing.JScrollPane();
        deplist = new ActiveList<>();

        setLayout(new java.awt.BorderLayout());

        topP.setOpaque(false);
        topP.setLayout(new java.awt.BorderLayout());

        titleL.setText("List of shadowed dependencies");
        topP.add(titleL, java.awt.BorderLayout.WEST);

        recalculateB.setText("Recalculate");
        recalculateB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recalculateBActionPerformed(evt);
            }
        });
        topP.add(recalculateB, java.awt.BorderLayout.EAST);

        add(topP, java.awt.BorderLayout.NORTH);

        deplist.setModel(new DefaultListModel<>());
        deplist.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        deplist.setVisibleRowCount(4);
        scrollS.setViewportView(deplist);

        add(scrollS, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void recalculateBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recalculateBActionPerformed
        updateModel(dependencies = asList(AndroidParser.parseProject(projectRoot)));
        callback.invoke(Pom.packDependencies(dependencies));

    }//GEN-LAST:event_recalculateBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> deplist;

    // End of variables declaration//GEN-END:variables
}
