/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import com.panayotis.hrgui.HiResEmptyBorder;
import org.crossmobile.gui.actives.ActiveLabel;

public class PluginConfigEntryJ extends javax.swing.JPanel {

    public PluginConfigEntryJ(String name) {
        initComponents();
        label.setText(name);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new ActiveLabel();
        javax.swing.JPanel rightP = new javax.swing.JPanel();
        javax.swing.JButton removeB = new javax.swing.JButton();

        setBorder(new HiResEmptyBorder(6,12,6,0));
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());
        add(label, java.awt.BorderLayout.WEST);

        rightP.setOpaque(false);

        removeB.setText("Remove");
        rightP.add(removeB);

        add(rightP, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}
