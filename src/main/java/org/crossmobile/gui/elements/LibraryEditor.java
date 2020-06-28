/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.gui.actives.ActiveList;
import org.crossmobile.gui.actives.ActivePanel;

import javax.swing.*;
import java.io.File;
import java.util.List;


public class LibraryEditor extends ActivePanel {

    private final File projectRoot;

    public LibraryEditor(File projectRoot, List<String> libraries) {
        this.projectRoot = projectRoot;
        initComponents();
        updateModel(libraries);
    }

    private void updateModel(List<String> model) {
        deplist.setModel(new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return model.size();
            }

            @Override
            public String getElementAt(int index) {
                return model.get(index);
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

        titleL.setText("List of Android Dependencies");
        topP.add(titleL, java.awt.BorderLayout.WEST);

        recalculateB.setText("Recalculate");
        topP.add(recalculateB, java.awt.BorderLayout.EAST);

        add(topP, java.awt.BorderLayout.NORTH);

        deplist.setModel(new DefaultListModel<>());
        deplist.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        deplist.setVisibleRowCount(4);
        scrollS.setViewportView(deplist);

        add(scrollS, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> deplist;
    // End of variables declaration//GEN-END:variables
}
