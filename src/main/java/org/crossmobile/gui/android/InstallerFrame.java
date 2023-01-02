/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.android;

import com.panayotis.hrgui.HiResButton;
import com.panayotis.hrgui.HiResOptions;
import org.crossmobile.gui.actives.ActiveTextPane;
import org.crossmobile.gui.elements.GradientPanel;
import org.crossmobile.gui.utils.StreamManager;
import org.crossmobile.gui.utils.StreamQuality;

import javax.swing.*;
import java.awt.*;

public final class InstallerFrame extends JDialog {
    private static final String Java11MissingModuleSignature = "java.lang.ClassNotFoundException: javax.xml.bind.annotation.XmlSchema";
    private static boolean showReminder = true;

    private boolean cancelHasBeenPressed = false;
    private boolean stillRunning = true;
    private InstallerThread installer;

    public InstallerFrame() {
        super((Frame) null, true);
        initComponents();
        yesB.setVisible(false);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Background = new GradientPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaT = new ActiveTextPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        yesB = new HiResButton();
        jPanel4 = new javax.swing.JPanel();
        cancelB = new HiResButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Android License Agreement");

        Background.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 0, 8));
        jPanel1.setMinimumSize(new java.awt.Dimension(400, 300));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 300));
        jPanel1.setLayout(new java.awt.BorderLayout());

        areaT.setEditable(false);
        areaT.setFont(areaT.getFont().deriveFont((float)12));
        jScrollPane1.setViewportView(areaT);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        Background.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setOpaque(false);

        yesB.setText("Say \"yes\"");
        yesB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesBActionPerformed(evt);
            }
        });
        jPanel3.add(yesB);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setOpaque(false);

        cancelB.setText("Cancel");
        cancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBActionPerformed(evt);
            }
        });
        jPanel4.add(cancelB);

        jPanel2.add(jPanel4, java.awt.BorderLayout.EAST);

        Background.add(jPanel2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(Background, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBActionPerformed
        if (stillRunning) {
            showReminder = false;
            cancelHasBeenPressed = true;
            if (installer != null)
                installer.sendCancel();
        }
        SwingUtilities.invokeLater(() -> {
            if (showReminder)
                new HiResOptions().message("Please remember to rerun the build procedure").title( "Android License Agreement").warning().show();
        });
        setVisible(false);
    }//GEN-LAST:event_cancelBActionPerformed

    private void yesBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesBActionPerformed
        yesB.setVisible(false);
        if (installer != null)
            installer.sendYes();
    }//GEN-LAST:event_yesBActionPerformed

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        dispose();
    }

    public StreamManager getStreamManager() {
        return ((ActiveTextPane) areaT).getStreamManager();
    }

    public void enableYes() {
        yesB.setVisible(true);

    }

    public void launch() {
        installer = new InstallerThread(this);
        installer.start();
        setVisible(true);
    }

    public boolean isCancelled() {
        return cancelHasBeenPressed;
    }

    void addChar(Character c) {
        if (stillRunning) {
            ((ActiveTextPane) areaT).addChar(c, StreamQuality.BASIC);
            if (areaT.getText().startsWith("Exception in thread") && areaT.getText().contains(Java11MissingModuleSignature)) {
                finish();
                cancelB.setText("Cancel");
                showReminder = false;
                areaT.setText("Current SDK Manager tool for the Android platform requires Java version 8.\n\nTo use JDK versions 9 and later, you need to upgrade to latest Android Tools. Otherwise you need to select JDK version 8 for accepting the license.\n\nAfter accepting the license it is possible to upgrade the JDK version again.\n");
            }
        }
    }

    public void finish() {
        if (stillRunning) {
            stillRunning = false;
            cancelB.setText("Finish");
            cancelB.setEnabled(true);
            installer = null;
            yesB.setVisible(false);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Background;
    private javax.swing.JTextPane areaT;
    private javax.swing.JButton cancelB;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton yesB;
    // End of variables declaration//GEN-END:variables

}
