/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import org.crossmobile.gui.ProjectFrame;
import org.crossmobile.gui.actives.ActiveButton;
import org.crossmobile.gui.actives.ActiveCheckBox;
import org.crossmobile.gui.actives.ActivePanel;
import org.crossmobile.gui.actives.ActiveTextArea;

public class SendStackTrace extends javax.swing.JDialog {

    public static ActivePanel getPanel() {
        BottomPanel bpanel = new BottomPanel("Send stack trace", "images/send", SendStackTrace::new);
        bpanel.setEnabled(false);
        return bpanel;
    }

    public static ActiveButton getButton(ProjectFrame provider) {
        ActiveButton button = new ActiveButton();
        button.setText("Send stack trace");
        button.setIcon("images/send");
        button.addActionListener(e -> new SendStackTrace(provider).setVisible(true));
        button.setEnabled(false);
        return button;
    }

    private SendStackTrace(ProjectFrame frame) {
        super(frame, true);
        initComponents();
        DebugInfo info = frame.getDebugInfo();
        outA.setText(info.output);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundP = new GradientPanel();
        actionP = new ActivePanel();
        sendB = new javax.swing.JButton();
        componentP = new ActivePanel();
        sendInfoB = new ActiveCheckBox();
        outerrorP = new ActivePanel();
        outP = new ActivePanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        outA = new ActiveTextArea();
        userP = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        userinfoA = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Send stack trace");

        backgroundP.setLayout(new java.awt.BorderLayout());

        actionP.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 8, 12));
        actionP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        sendB.setText("Send");
        sendB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendBActionPerformed(evt);
            }
        });
        actionP.add(sendB);

        backgroundP.add(actionP, java.awt.BorderLayout.SOUTH);

        componentP.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 0, 12));
        componentP.setLayout(new java.awt.BorderLayout(0, 8));

        sendInfoB.setSelected(true);
        sendInfoB.setText("Send system information (strongly suggested)");
        componentP.add(sendInfoB, java.awt.BorderLayout.NORTH);

        outerrorP.setLayout(new java.awt.GridLayout(0, 1));

        outP.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Output");
        outP.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        outA.setColumns(30);
        outA.setRows(15);
        jScrollPane1.setViewportView(outA);

        outP.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        outerrorP.add(outP);

        componentP.add(outerrorP, java.awt.BorderLayout.CENTER);

        userP.setLayout(new java.awt.BorderLayout());

        jLabel3.setText("Please add any additional information");
        userP.add(jLabel3, java.awt.BorderLayout.NORTH);

        userinfoA.setColumns(20);
        userinfoA.setRows(4);
        jScrollPane3.setViewportView(userinfoA);

        userP.add(jScrollPane3, java.awt.BorderLayout.SOUTH);

        componentP.add(userP, java.awt.BorderLayout.SOUTH);

        backgroundP.add(componentP, java.awt.BorderLayout.CENTER);

        getContentPane().add(backgroundP, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBActionPerformed
        setVisible(false);
    }//GEN-LAST:event_sendBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionP;
    private javax.swing.JPanel backgroundP;
    private javax.swing.JPanel componentP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea outA;
    private javax.swing.JPanel outP;
    private javax.swing.JPanel outerrorP;
    private javax.swing.JButton sendB;
    private javax.swing.JCheckBox sendInfoB;
    private javax.swing.JPanel userP;
    private javax.swing.JTextArea userinfoA;
    // End of variables declaration//GEN-END:variables

}
