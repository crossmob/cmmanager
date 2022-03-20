/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import com.panayotis.appenh.AFileChooser;
import com.panayotis.hrgui.HiResButton;
import com.panayotis.hrgui.HiResEmptyBorder;
import org.crossmobile.gui.actives.*;
import org.crossmobile.utils.LocationTarget;
import org.crossmobile.utils.TreeWalker;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.panayotis.appenh.AFileChooser.FileSelectionMode.FilesAndDirectories;
import static java.util.Collections.singletonList;

public class JWizard extends JDialog {

    private int cardid = 1;
    private final String name;
    private LocationTarget target;
    private Consumer<String> callback;
    private List<File> result;
    private String oldLocation;

    /**
     * Creates new form JWizard
     *
     * @param name
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public JWizard(String name) {
        super((Frame) null, true);
        this.name = name;
        initComponents();
        resolveTypeP.setVisible(false);
        // Extra WelcomeText configuration which is not inherited (?)
        ((ActiveTextArea) WelcomeText).setInactiveColorFollowsActive(true);
        ((ActiveTextArea) WelcomeText).removeMouseListeners();

        setLocationRelativeTo(null);
    }

    public void fire(LocationTarget target, String oldLocation) {
        this.target = target;
        this.oldLocation = oldLocation;
        pleaseWaitL.setVisible(false);
        resolveTypeP.setVisible(true);
        CancelB.setEnabled(true);
        ContinueB.setEnabled(true);
        setVisible(true);
    }

    public void setCallback(Consumer<String> successCallback) {
        this.callback = successCallback;
    }

    private void clickContinue() {
        switch (cardid) {
            case 1:
                CancelB.setEnabled(false);
                ContinueB.setEnabled(false);
                boolean isManual = ManualB.isSelected();
                Collection<String> locations = isManual ? singletonList(FilenameT.getText()) : null;
                new Thread(() -> {
                    Collection<File> set = new HashSet<>();
                    TreeWalker.searchExecutable(singletonList(target.makeRequest(oldLocation, set::add)), locations, !ManualB.isSelected(), () -> true);
                    result = new ArrayList<>(set);
                    clickContinue();
                }).start();
                break;
            case 2:
                boolean foundFile = !result.isEmpty();
                CancelB.setEnabled(!foundFile);
                ContinueB.setEnabled(foundFile);
                ContinueB.setText("Finish");
                FinishTitle.setText(foundFile ? name + " has been resolved" : "Unable to locate " + name);
                ResultS.setVisible(result.size() > 1);
                if (foundFile) {
                    ResultL.setModel(new AbstractListModel<String>() {
                        @Override
                        public int getSize() {
                            return result.size();
                        }

                        @Override
                        public String getElementAt(int index) {
                            return result.get(index).getAbsolutePath();
                        }
                    });
                    ResultL.setSelectedIndex(0);
                }
                break;
            case 3:
                performCallback();
        }
        ((CardLayout) CardsP.getLayout()).show(CardsP, "card" + (++cardid));
    }

    private void performCallback() {
        setVisible(false);
        if (callback != null)
            callback.accept((result == null || result.isEmpty()) ? null : result.get(ResultL.getSelectedIndex()).getAbsolutePath());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AutoSel = new javax.swing.ButtonGroup();
        Background = new GradientPanel();
        CardsP = new ActivePanel();
        WelcomeP = new ActivePanel();
        jPanel2 = new ActivePanel();
        WelcomeTitle = new ActiveLabel();
        resolveTypeP = new ActivePanel();
        AutoSelL = new ActiveLabel();
        AutoB = new ActiveRadioButton();
        jPanel4 = new ActivePanel();
        FilenameT = new ActiveTextField();
        BrowseB = new HiResButton();
        ManualB = new ActiveRadioButton();
        WelcomeText = new ActiveTextArea();
        pleaseWaitL = new ActiveLabel();
        SearchP = new ActivePanel();
        jPanel3 = new ActivePanel();
        SearchTitle = new ActiveLabel();
        AutoProgress = new ActiveProgressBar();
        FinishP = new ActivePanel();
        FinishTitle = new ActiveLabel();
        ResultS = new ActiveScrollPane();
        ResultL = new ActiveList();
        LowerP = new ActivePanel();
        ButtonsP = new ActivePanel();
        CancelB = new HiResButton();
        ContinueB = new HiResButton();
        jLabel1 = new ActiveLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("External plugin Wizard");
        setResizable(false);

        Background.setLayout(new java.awt.BorderLayout());

        CardsP.setBorder(new HiResEmptyBorder(20, 30, 20, 20));
        CardsP.setLayout(new java.awt.CardLayout());

        WelcomeP.setLayout(new java.awt.BorderLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        WelcomeTitle.setFont(WelcomeTitle.getFont().deriveFont(WelcomeTitle.getFont().getStyle() | java.awt.Font.BOLD, WelcomeTitle.getFont().getSize() + 2));
        WelcomeTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        WelcomeTitle.setText(name);
        WelcomeTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 12, 0));
        WelcomeTitle.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jPanel2.add(WelcomeTitle, java.awt.BorderLayout.NORTH);

        resolveTypeP.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 2, 0, 0));
        resolveTypeP.setOpaque(false);
        resolveTypeP.setLayout(new java.awt.BorderLayout());

        AutoSelL.setFont(AutoSelL.getFont().deriveFont(AutoSelL.getFont().getStyle() | java.awt.Font.BOLD));
        AutoSelL.setText("How should this issue been resolved?");
        AutoSelL.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 2, 0, 0));
        resolveTypeP.add(AutoSelL, java.awt.BorderLayout.NORTH);

        AutoSel.add(AutoB);
        AutoB.setSelected(true);
        AutoB.setText("Automatically search for the executable");
        AutoB.setOpaque(false);
        AutoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutoBActionPerformed(evt);
            }
        });
        resolveTypeP.add(AutoB, java.awt.BorderLayout.SOUTH);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 12, 0));
        jPanel4.setLayout(new java.awt.BorderLayout());

        FilenameT.setEditable(false);
        FilenameT.setColumns(20);
        FilenameT.setToolTipText("The absolute path of the executable. Use the Browse button to change it");
        jPanel4.add(FilenameT, java.awt.BorderLayout.CENTER);

        BrowseB.setText("Browse");
        BrowseB.setToolTipText("Open a file dialog to select the filename of the executable");
        BrowseB.setEnabled(false);
        BrowseB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BrowseBActionPerformed(evt);
            }
        });
        jPanel4.add(BrowseB, java.awt.BorderLayout.EAST);

        AutoSel.add(ManualB);
        ManualB.setText("Manually browse for the executable");
        ManualB.setOpaque(false);
        ManualB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManualBActionPerformed(evt);
            }
        });
        jPanel4.add(ManualB, java.awt.BorderLayout.NORTH);

        resolveTypeP.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel2.add(resolveTypeP, java.awt.BorderLayout.SOUTH);

        WelcomeText.setEditable(false);
        WelcomeText.setBackground(new Color(0, true));
        WelcomeText.setColumns(20);
        WelcomeText.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        WelcomeText.setRows(3);
        WelcomeText.setText("CrossMobile needs " + name + "\nto continue with the requested action.");
        WelcomeText.setBorder(null);
        WelcomeText.setOpaque(false);
        jPanel2.add(WelcomeText, java.awt.BorderLayout.CENTER);

        WelcomeP.add(jPanel2, java.awt.BorderLayout.NORTH);

        pleaseWaitL.setFont(pleaseWaitL.getFont().deriveFont((pleaseWaitL.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, pleaseWaitL.getFont().getSize() + 2));
        pleaseWaitL.setText("Please wait while initializing wizard...");
        WelcomeP.add(pleaseWaitL, java.awt.BorderLayout.CENTER);

        CardsP.add(WelcomeP, "card1");

        SearchP.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        SearchTitle.setFont(SearchTitle.getFont().deriveFont(SearchTitle.getFont().getStyle() | java.awt.Font.BOLD, SearchTitle.getFont().getSize() + 2));
        SearchTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        SearchTitle.setText("Locating " + name + "...");
        SearchTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 12, 0));
        SearchTitle.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jPanel3.add(SearchTitle, java.awt.BorderLayout.NORTH);

        AutoProgress.setIndeterminate(true);
        jPanel3.add(AutoProgress, java.awt.BorderLayout.SOUTH);

        SearchP.add(jPanel3, java.awt.BorderLayout.NORTH);

        CardsP.add(SearchP, "card2");

        FinishP.setLayout(new java.awt.BorderLayout());

        FinishTitle.setFont(FinishTitle.getFont().deriveFont(FinishTitle.getFont().getStyle() | java.awt.Font.BOLD, FinishTitle.getFont().getSize() + 2));
        FinishTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        FinishTitle.setText(name + " executable");
        FinishTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 12, 0));
        FinishTitle.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        FinishP.add(FinishTitle, java.awt.BorderLayout.NORTH);

        ResultS.setOpaque(false);

        ResultL.setFont(ResultL.getFont().deriveFont(ResultL.getFont().getStyle() & ~java.awt.Font.BOLD));
        ResultL.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ResultS.setViewportView(ResultL);

        FinishP.add(ResultS, java.awt.BorderLayout.CENTER);

        CardsP.add(FinishP, "card3");

        Background.add(CardsP, java.awt.BorderLayout.CENTER);

        LowerP.setBorder(new HiResEmptyBorder(1, 1, 8, 12));
        LowerP.setLayout(new java.awt.BorderLayout());

        ButtonsP.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        CancelB.setText("Cancel");
        CancelB.setEnabled(false);
        CancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelBActionPerformed(evt);
            }
        });
        ButtonsP.add(CancelB);

        ContinueB.setText("Continue");
        ContinueB.setEnabled(false);
        ContinueB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContinueBActionPerformed(evt);
            }
        });
        ButtonsP.add(ContinueB);

        LowerP.add(ButtonsP, java.awt.BorderLayout.EAST);

        Background.add(LowerP, java.awt.BorderLayout.SOUTH);

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setIcon(new ActiveIcon("images/wizard"));
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(80, 12, 50, 0));
        Background.add(jLabel1, java.awt.BorderLayout.WEST);

        getContentPane().add(Background, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BrowseBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BrowseBActionPerformed
        File selectedFile = new AFileChooser().setMode(FilesAndDirectories).openSingle();
        if (selectedFile != null) {
            FilenameT.setText(selectedFile.getAbsolutePath());
            ContinueB.setEnabled(true);
        } else {
            FilenameT.setText("");
            ContinueB.setEnabled(false);
        }
    }//GEN-LAST:event_BrowseBActionPerformed

    private void ContinueBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContinueBActionPerformed
        clickContinue();
    }//GEN-LAST:event_ContinueBActionPerformed

    private void CancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelBActionPerformed
        FilenameT.setText("");
        performCallback();
    }//GEN-LAST:event_CancelBActionPerformed

    private void AutoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutoBActionPerformed
        BrowseB.setEnabled(false);
        ContinueB.setEnabled(true);
    }//GEN-LAST:event_AutoBActionPerformed

    private void ManualBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManualBActionPerformed
        BrowseB.setEnabled(true);
        ContinueB.setEnabled(!FilenameT.getText().isEmpty());
    }//GEN-LAST:event_ManualBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton AutoB;
    private javax.swing.JProgressBar AutoProgress;
    private javax.swing.ButtonGroup AutoSel;
    private javax.swing.JLabel AutoSelL;
    private javax.swing.JPanel Background;
    private javax.swing.JButton BrowseB;
    private javax.swing.JPanel ButtonsP;
    private javax.swing.JButton CancelB;
    private javax.swing.JPanel CardsP;
    private javax.swing.JButton ContinueB;
    private javax.swing.JTextField FilenameT;
    private javax.swing.JPanel FinishP;
    private javax.swing.JLabel FinishTitle;
    private javax.swing.JPanel LowerP;
    private javax.swing.JRadioButton ManualB;
    private javax.swing.JList ResultL;
    private javax.swing.JScrollPane ResultS;
    private javax.swing.JPanel SearchP;
    private javax.swing.JLabel SearchTitle;
    private javax.swing.JPanel WelcomeP;
    private javax.swing.JTextArea WelcomeText;
    private javax.swing.JLabel WelcomeTitle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel pleaseWaitL;
    private javax.swing.JPanel resolveTypeP;
    // End of variables declaration//GEN-END:variables
}
