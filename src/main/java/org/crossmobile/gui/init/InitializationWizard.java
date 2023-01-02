/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.init;

import com.panayotis.hrgui.HiResButton;
import com.panayotis.hrgui.HiResDialog;
import com.panayotis.hrgui.HiResIcon;
import org.crossmobile.gui.actives.ActiveLabel;
import org.crossmobile.gui.actives.ActiveMenuItem;
import org.crossmobile.gui.actives.ActivePopupMenu;
import org.crossmobile.gui.actives.ActiveTextPane;
import org.crossmobile.gui.elements.GradientPanel;
import org.crossmobile.gui.elements.JWait;
import org.crossmobile.gui.utils.StreamManager;
import org.crossmobile.gui.utils.StreamQuality;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.TreeWalker.Active;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.crossmobile.prefs.Config.MIN_JAVA_VERSION_FULL;

public class InitializationWizard extends HiResDialog implements Active {

    private static final HiResIcon WAITICON = new HiResIcon("images/spin", false);

    private Runnable action;
    private Card currentCard = Card.Welcome;
    private final JWait waiting = new JWait(WAITICON);
    private final Collection<File> listJDK = new TreeSet<>();
    private final Collection<File> listNetbeans = new TreeSet<>();
    private final Collection<File> listIntelliJ = new TreeSet<>();
    private final Collection<File> listVSCode = new TreeSet<>();
    private final Collection<File> listStudio = new TreeSet<>();
    private final Collection<File> listAndroid = new TreeSet<>();
    private final AtomicReference<File> selectedJDK = new AtomicReference<>();
    private final AtomicReference<File> selectedNetbeans = new AtomicReference<>();
    private final AtomicReference<File> selectedIntelliJ = new AtomicReference<>();
    private final AtomicReference<File> selectedVSCode = new AtomicReference<>();
    private final AtomicReference<File> selectedStudio = new AtomicReference<>();
    private final AtomicReference<File> selectedAndroid = new AtomicReference<>();

    private boolean active = true;
    private boolean treewalking = true;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public InitializationWizard(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();
        jdkL.setIcon(new HiResIcon("images/wait", false));
        netbeansL.setIcon(new HiResIcon("images/wait", false));
        androidL.setIcon(new HiResIcon("images/wait", false));
        studioL.setIcon(new HiResIcon("images/wait", false));
        vscodeL.setIcon(new HiResIcon("images/wait", false));
        intellijL.setIcon(new HiResIcon("images/wait", false));
        jdkB.setVisible(false);
        androidB.setVisible(false);
        netbeansB.setVisible(false);
        vscodeB.setVisible(false);
        intellijB.setVisible(false);
        studioB.setVisible(false);
        ((ActiveLabel) titleL).setIcon("images/logo-small", false);
        subP.add(waiting, BorderLayout.WEST);
        setLocationRelativeTo(null);
    }

    public void gotoCard(Card c) {
        if (currentCard == c)
            return;
        currentCard = c;
        ((CardLayout) cards.getLayout()).show(cards, c.name());
    }

    public void setMainTitle(String title) {
        titleL.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleL.setText(subtitle);
    }

    public void setWelcomeInfo(String info1, String info2, String info3, String info4, String info5) {
        infoL1.setText(info1);
        infoL2.setText(info2);
        infoL3.setText(info3);
        infoL4.setText(info4);
        infoL5.setText(info5);
    }

    public void setRunning(boolean isWorking) {
        waiting.setRunning(isWorking);
    }

    public StreamManager getStreamManager() {
        return ((ActiveTextPane) detailT).getStreamManager();
    }

    public void appendText(String txt) {
        ((ActiveTextPane) detailT).addLine(txt, StreamQuality.INFO);
    }

    public void hideActionButton() {
        actionB.setVisible(false);
    }

    public void setAction(String buttonLabel, Runnable action) {
        buttonLabel = buttonLabel == null ? "" : buttonLabel;
        actionB.setText(buttonLabel);
        actionB.setEnabled(action != null);
        this.action = action;
    }

    public void foundJDK(File found) {
        updateVisualsIfFound(jdkL, null, null, listJDK, found);
    }

    public void foundNetbeans(File found) {
        updateVisualsIfFound(netbeansL, null, null, listNetbeans, found);
    }

    public void foundIntelliJ(File found) {
        updateVisualsIfFound(intellijL, null, null, listIntelliJ, found);
    }

    public void foundVSCode(File found) {
        updateVisualsIfFound(vscodeL, null, null, listVSCode, found);
    }

    public void foundStudio(File found) {
        updateVisualsIfFound(studioL, null, null, listStudio, found);
    }

    public void foundAndroid(File found) {
        updateVisualsIfFound(androidL, null, null, listAndroid, found);
    }

    private boolean updateVisualsIfFound(JLabel widget, JButton resolve, AtomicReference<File> fixedRef, Collection<File> where, File found) {
        if (found != null)
            where.add(found);
        if (resolve != null && where.size() > 1)
            resolve.setVisible(true);
        boolean didFound = (fixedRef != null && fixedRef.get() != null) || where.size() == 1;
        widget.setIcon(new HiResIcon(didFound ? "images/found" : (where.isEmpty() ? (treewalking ? "images/wait" : "images/notfound") : "images/warning"), false));
        return didFound;
    }

    private boolean isAppSelected(AtomicReference<File> selected, Collection<File> list) {
        return selected.get() != null || list.size() < 2;
    }

    public void resolveApps() {
        boolean allAppsAreSelected = isAppSelected(selectedJDK, listJDK)
                & isAppSelected(selectedNetbeans, listNetbeans)
                & isAppSelected(selectedIntelliJ, listIntelliJ)
                & isAppSelected(selectedVSCode, listVSCode)
                & isAppSelected(selectedStudio, listStudio)
                & isAppSelected(selectedAndroid, listAndroid);

        boolean allAppsAreResolved = updateVisualsIfFound(jdkL, jdkB, selectedJDK, listJDK, null)
                & updateVisualsIfFound(netbeansL, netbeansB, selectedNetbeans, listNetbeans, null)
                & updateVisualsIfFound(intellijL, intellijB, selectedIntelliJ, listIntelliJ, null)
                & updateVisualsIfFound(vscodeL, vscodeB, selectedVSCode, listVSCode, null)
                & updateVisualsIfFound(studioL, studioB, selectedStudio, listStudio, null)
                & updateVisualsIfFound(androidL, androidB, selectedAndroid, listAndroid, null);

        setRunning(treewalking);
        if (allAppsAreSelected && !treewalking)
            setAction("Done", () -> {
                Prefs.setJDKLocation(resolveUnique(selectedJDK, listJDK));
                Prefs.setAndroidSDKLocation(resolveUnique(selectedAndroid, listAndroid));
                Prefs.setNetbeansLocation(resolveUnique(selectedNetbeans, listNetbeans));
                Prefs.setIntelliJLocation(resolveUnique(selectedIntelliJ, listIntelliJ));
                Prefs.setVSCodeLocation(resolveUnique(selectedVSCode, listVSCode));
                Prefs.setStudioLocation(resolveUnique(selectedStudio, listStudio));
                Prefs.setWizardExecuted(true);
                setVisible(false);
            });
        else
            setAction("Choose alternatives...", null);
        subtitleL.setText(allAppsAreSelected ? (allAppsAreResolved ? "All applications have been successfully resolved" : "Please open Settings after initialization to solve unresolved applications.") : "Please resolve external applications");
    }

    private void popupDisplay(JButton parent, AtomicReference<File> fixedRef, Collection<File> execs) {
        execsM.removeAll();
        for (File f : execs) {
            JMenuItem m = new ActiveMenuItem(f.getAbsolutePath());
            m.setActionCommand(f.getAbsolutePath());
            m.addActionListener(e -> {
                fixedRef.set(f);
                resolveApps();
            });
            execsM.add(m);
        }
        execsM.show(parent, -20, (int) (parent.getHeight() * 0.8));
    }

    private String resolveUnique(AtomicReference<File> ref, Collection<File> collection) {
        return ref.get() == null ? (collection.size() > 0 ? collection.iterator().next().getAbsolutePath() : null) : ref.get().getAbsolutePath();
    }

    public void setActive(boolean status) {
        active = status;
        resolveApps();
    }

    public void setTreeWalking(boolean treewalking) {
        this.treewalking = treewalking;
        resolveApps();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        execsM = new ActivePopupMenu();
        jPanel4 = new GradientPanel();
        titleL = new ActiveLabel();
        mainP = new javax.swing.JPanel();
        subP = new javax.swing.JPanel();
        subtitleL = new ActiveLabel();
        cards = new javax.swing.JPanel();
        welcomeP = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        infoL1 = new ActiveLabel();
        infoL2 = new ActiveLabel();
        infoLSpace1 = new ActiveLabel();
        infoL3 = new ActiveLabel();
        infoL4 = new ActiveLabel();
        infoLSpace2 = new ActiveLabel();
        infoL5 = new ActiveLabel();
        externalsP = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel1 = new ActiveLabel();
        jPanel2 = new javax.swing.JPanel();
        jdkL = new ActiveLabel();
        jPanel9 = new javax.swing.JPanel();
        jdkB = new HiResButton();
        jPanel6 = new javax.swing.JPanel();
        androidL = new ActiveLabel();
        jPanel10 = new javax.swing.JPanel();
        androidB = new HiResButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel2 = new ActiveLabel();
        jPanel5 = new javax.swing.JPanel();
        intellijL = new ActiveLabel();
        jPanel12 = new javax.swing.JPanel();
        intellijB = new HiResButton();
        jPanel16 = new javax.swing.JPanel();
        vscodeL = new ActiveLabel();
        jPanel17 = new javax.swing.JPanel();
        vscodeB = new HiResButton();
        jPanel3 = new javax.swing.JPanel();
        netbeansL = new ActiveLabel();
        jPanel11 = new javax.swing.JPanel();
        netbeansB = new HiResButton();
        jPanel8 = new javax.swing.JPanel();
        studioL = new ActiveLabel();
        jPanel13 = new javax.swing.JPanel();
        studioB = new HiResButton();
        infoP = new javax.swing.JPanel();
        detailsButtonP = new javax.swing.JPanel();
        detailsB = new HiResButton();
        detailS = new javax.swing.JScrollPane();
        detailT = new ActiveTextPane();
        javax.swing.JPanel jPanel7 = new javax.swing.JPanel();
        actionB = new HiResButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 400));
        setResizable(false);

        jPanel4.setLayout(new java.awt.BorderLayout());

        titleL.setFont(titleL.getFont().deriveFont((float) 20));
        titleL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleL.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 16, 0));
        jPanel4.add(titleL, java.awt.BorderLayout.NORTH);

        mainP.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 20));
        mainP.setOpaque(false);
        mainP.setLayout(new java.awt.BorderLayout(0, 8));

        subP.setOpaque(false);
        subP.setLayout(new java.awt.BorderLayout(4, 0));
        subP.add(subtitleL, java.awt.BorderLayout.CENTER);

        mainP.add(subP, java.awt.BorderLayout.NORTH);

        cards.setOpaque(false);
        cards.setLayout(new java.awt.CardLayout());

        welcomeP.setOpaque(false);
        welcomeP.setLayout(new java.awt.BorderLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        infoL1.setFont(infoL1.getFont().deriveFont((infoL1.getFont().getStyle() | java.awt.Font.ITALIC)));
        jPanel1.add(infoL1);

        infoL2.setFont(infoL2.getFont().deriveFont((infoL2.getFont().getStyle() | java.awt.Font.ITALIC)));
        jPanel1.add(infoL2);

        infoLSpace1.setFont(infoLSpace1.getFont().deriveFont((infoLSpace1.getFont().getStyle() | java.awt.Font.ITALIC)));
        infoLSpace1.setText(" ");
        jPanel1.add(infoLSpace1);

        infoL3.setFont(infoL3.getFont().deriveFont((infoL3.getFont().getStyle() | java.awt.Font.ITALIC)));
        jPanel1.add(infoL3);

        infoL4.setFont(infoL4.getFont().deriveFont((infoL4.getFont().getStyle() | java.awt.Font.ITALIC)));
        jPanel1.add(infoL4);

        infoLSpace2.setFont(infoLSpace2.getFont().deriveFont((infoLSpace2.getFont().getStyle() | java.awt.Font.ITALIC)));
        infoLSpace2.setText(" ");
        jPanel1.add(infoLSpace2);

        infoL5.setFont(infoL5.getFont().deriveFont((infoL5.getFont().getStyle() | java.awt.Font.ITALIC)));
        jPanel1.add(infoL5);

        welcomeP.add(jPanel1, java.awt.BorderLayout.NORTH);

        cards.add(welcomeP, "Welcome");

        externalsP.setOpaque(false);
        externalsP.setLayout(new javax.swing.BoxLayout(externalsP, javax.swing.BoxLayout.Y_AXIS));

        jPanel14.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 0, 0));
        jPanel14.setOpaque(false);
        jPanel14.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(jLabel1.getFont().deriveFont((jLabel1.getFont().getStyle() | java.awt.Font.ITALIC)));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Required Components");
        jPanel14.add(jLabel1, java.awt.BorderLayout.CENTER);

        externalsP.add(jPanel14);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        jdkL.setText("Java Development Kit");
        jdkL.setIconTextGap(8);
        jPanel2.add(jdkL, java.awt.BorderLayout.WEST);

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        jdkB.setText("Choose");
        jdkB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdkBActionPerformed(evt);
            }
        });
        jPanel9.add(jdkB);

        jPanel2.add(jPanel9, java.awt.BorderLayout.EAST);

        externalsP.add(jPanel2);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.BorderLayout());

        androidL.setText("Android SDK");
        androidL.setIconTextGap(8);
        jPanel6.add(androidL, java.awt.BorderLayout.WEST);

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        androidB.setText("Choose");
        androidB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                androidBActionPerformed(evt);
            }
        });
        jPanel10.add(androidB);

        jPanel6.add(jPanel10, java.awt.BorderLayout.EAST);

        externalsP.add(jPanel6);

        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(jLabel2.getFont().deriveFont((jLabel2.getFont().getStyle() | java.awt.Font.ITALIC)));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Optional Components");
        jPanel15.add(jLabel2, java.awt.BorderLayout.CENTER);

        externalsP.add(jPanel15);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        intellijL.setText("IntelliJ Idea");
        intellijL.setIconTextGap(8);
        jPanel5.add(intellijL, java.awt.BorderLayout.WEST);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        intellijB.setText("Choose");
        intellijB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intellijBActionPerformed(evt);
            }
        });
        jPanel12.add(intellijB);

        jPanel5.add(jPanel12, java.awt.BorderLayout.EAST);

        externalsP.add(jPanel5);

        jPanel16.setOpaque(false);
        jPanel16.setLayout(new java.awt.BorderLayout());

        vscodeL.setText("Visual Studio Code");
        vscodeL.setIconTextGap(8);
        jPanel16.add(vscodeL, java.awt.BorderLayout.WEST);

        jPanel17.setOpaque(false);
        jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        vscodeB.setText("Choose");
        vscodeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vscodeBActionPerformed(evt);
            }
        });
        jPanel17.add(vscodeB);

        jPanel16.add(jPanel17, java.awt.BorderLayout.EAST);

        externalsP.add(jPanel16);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.BorderLayout());

        netbeansL.setText("Netbeans");
        netbeansL.setIconTextGap(8);
        jPanel3.add(netbeansL, java.awt.BorderLayout.WEST);

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        netbeansB.setText("Choose");
        netbeansB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netbeansBActionPerformed(evt);
            }
        });
        jPanel11.add(netbeansB);

        jPanel3.add(jPanel11, java.awt.BorderLayout.EAST);

        externalsP.add(jPanel3);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.BorderLayout());

        studioL.setText("Android Studio");
        studioL.setIconTextGap(8);
        jPanel8.add(studioL, java.awt.BorderLayout.WEST);

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        studioB.setText("Choose");
        studioB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studioBActionPerformed(evt);
            }
        });
        jPanel13.add(studioB);

        jPanel8.add(jPanel13, java.awt.BorderLayout.EAST);

        externalsP.add(jPanel8);

        cards.add(externalsP, "Externals");

        infoP.setOpaque(false);
        infoP.setLayout(new java.awt.BorderLayout());

        detailsButtonP.setOpaque(false);
        detailsButtonP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        detailsB.setText("Details");
        detailsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsBActionPerformed(evt);
            }
        });
        detailsButtonP.add(detailsB);

        infoP.add(detailsButtonP, java.awt.BorderLayout.CENTER);

        cards.add(infoP, "Info");

        detailT.setEditable(false);
        detailS.setViewportView(detailT);

        cards.add(detailS, "Details");

        mainP.add(cards, java.awt.BorderLayout.CENTER);

        jPanel4.add(mainP, java.awt.BorderLayout.CENTER);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        actionB.setText("Cancel");
        actionB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionBActionPerformed(evt);
            }
        });
        jPanel7.add(actionB);

        jPanel4.add(jPanel7, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void detailsBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsBActionPerformed
        gotoCard(Card.Details);
    }//GEN-LAST:event_detailsBActionPerformed

    private void actionBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionBActionPerformed
        if (action != null)
            action.run();
    }//GEN-LAST:event_actionBActionPerformed

    private void jdkBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdkBActionPerformed
        popupDisplay(jdkB, selectedJDK, listJDK);
    }//GEN-LAST:event_jdkBActionPerformed

    private void androidBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_androidBActionPerformed
        popupDisplay(androidB, selectedAndroid, listAndroid);
    }//GEN-LAST:event_androidBActionPerformed

    private void netbeansBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netbeansBActionPerformed
        popupDisplay(netbeansB, selectedNetbeans, listNetbeans);
    }//GEN-LAST:event_netbeansBActionPerformed

    private void intellijBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intellijBActionPerformed
        popupDisplay(intellijB, selectedIntelliJ, listIntelliJ);
    }//GEN-LAST:event_intellijBActionPerformed

    private void studioBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studioBActionPerformed
        popupDisplay(studioB, selectedStudio, listStudio);
    }//GEN-LAST:event_studioBActionPerformed

    private void vscodeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vscodeBActionPerformed
        popupDisplay(vscodeB, selectedVSCode, listVSCode);
    }//GEN-LAST:event_vscodeBActionPerformed

    public enum Card {
        Welcome,
        Externals,
        Info,
        Details
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actionB;
    private javax.swing.JButton androidB;
    private javax.swing.JLabel androidL;
    private javax.swing.JPanel cards;
    private javax.swing.JScrollPane detailS;
    private javax.swing.JTextPane detailT;
    private javax.swing.JButton detailsB;
    private javax.swing.JPanel detailsButtonP;
    private javax.swing.JPopupMenu execsM;
    private javax.swing.JPanel externalsP;
    private javax.swing.JLabel infoL1;
    private javax.swing.JLabel infoL2;
    private javax.swing.JLabel infoL3;
    private javax.swing.JLabel infoL4;
    private javax.swing.JLabel infoL5;
    private javax.swing.JLabel infoLSpace1;
    private javax.swing.JLabel infoLSpace2;
    private javax.swing.JPanel infoP;
    private javax.swing.JButton intellijB;
    private javax.swing.JLabel intellijL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton jdkB;
    private javax.swing.JLabel jdkL;
    private javax.swing.JPanel mainP;
    private javax.swing.JButton netbeansB;
    private javax.swing.JLabel netbeansL;
    private javax.swing.JButton studioB;
    private javax.swing.JLabel studioL;
    private javax.swing.JPanel subP;
    private javax.swing.JLabel subtitleL;
    private javax.swing.JLabel titleL;
    private javax.swing.JButton vscodeB;
    private javax.swing.JLabel vscodeL;
    private javax.swing.JPanel welcomeP;
    // End of variables declaration//GEN-END:variables

}
