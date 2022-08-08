/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import com.panayotis.hrgui.*;
import org.crossmobile.gui.actives.*;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.crossmobile.prefs.Config.JAVA_RANGE;
import static org.crossmobile.utils.SystemDependent.Execs.*;

public class Config extends HiResDialog {

    public static final LocationTarget Netbeans = new LocationTarget(NETBEANS, NETBEANS.filename());
    public static final LocationTarget Studio = new LocationTarget(STUDIO, STUDIO.filename(), STUDIO64.filename());
    public static final LocationTarget IntelliJ = new LocationTarget(IDEA, IDEA.filename());
    public static final LocationTarget VSCode = new LocationTarget(CODE, CODE.filename(), CODIUM.filename());
    public static final LocationTarget Android = new LocationTarget("tools/bin/sdkmanager.bat", "tools/bin/sdkmanager", "platform-tools/adb", "platform-tools/adb.exe");
    public static final LocationTarget JDK = new LocationTarget(f -> {
        File javac = new File(f, "bin/" + JAVAC.filename());
        if (!javac.isFile())
            return false;
        Commander exec = new Commander(javac.getAbsolutePath(), "-version");
        AtomicBoolean correct = new AtomicBoolean(false);
        Consumer<String> consumer = l -> {
            if (l.startsWith("javac "))
                correct.set(SystemDependent.isJavaValid(l.substring(6).trim()));
        };
        exec.setOutListener(consumer);
        exec.setErrListener(consumer);
        exec.exec();
        exec.waitFor();
        return correct.get();
    }, "lib/dt.jar", "lib/tools.jar", "jmods/jdk.compiler.jmod");

    private final static Config INSTANCE = new Config();

    public static void showConfig() {
        INSTANCE.setVisible(true);
        INSTANCE.setResizable(false);
    }

    private float scale = new ActivePreferences().scaleFactor();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    private Config() {
        super((Dialog) null, true);
        initComponents();

        if (!SystemDependent.getDefaultTheme().equals("auto"))
            systemB.setVisible(false);
        switch (Prefs.getUserTheme()) {
            case "dark":
                themeGroup.setSelected(darkB.getModel(), true);
                break;
            case "bright":
                themeGroup.setSelected(lightB.getModel(), true);
                break;
            default:
                themeGroup.setSelected(systemB.getModel(), true);
        }

        UIUtils.syncWidth(Arrays.asList(jdkL, androidL, netbeansL, intellijL, studioL, keystoreL));
        UIUtils.syncWidth(Arrays.asList(jdkB, androidB, netbeansB, intellijB, studioB, keystoreB));

        try {
            setIconImage(ImageIO.read(requireNonNull(getClass().getClassLoader().getResourceAsStream("images/logo-small@2x.png"))));
        } catch (IOException ignored) {
        }
        setLocationRelativeTo(null);
    }

    private void updateScale(float scaleFactor, boolean save, boolean updateSlider) {
        if (save) new ActivePreferences().storePrefs(scaleFactor);
        if (updateSlider) scaleSlider.setValue((int) (scaleFactor * 10));
        scaleViewer.setText(Float.toString(scaleFactor));
    }

    private void refreshPlugins() {
        boxedP.removeAll();
        for (Dependency dep : PluginRegistry.getExternalPlugins())
            boxedP.add(new DependencyEntry(dep)
                    .init()
                    .setDeleteCallback(() -> {
                        PluginRegistry.removeExternalPlugin(dep);
                        refreshPlugins();
                    }));
        boxedP.revalidate();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            netbeansT.setText(Prefs.getNetbeansLocation());
            intellijT.setText(Prefs.getIntelliJLocation());
            jdkT.setText(Prefs.getJDKLocation());
            androidT.setText(Prefs.getAndroidSDKLocation());
            studioT.setText(Prefs.getAndroidStudioLocation());
            keyT.setText(Prefs.getAndroidKeyLocation());
            vscodeT.setText(Prefs.getVSCodeLocation());
            refreshPlugins();
            updateScale(scale, false, true);
        } else {
            float newScale = scaleSlider.getValue() / 10f;
            if (Math.abs(scale - newScale) > 0.001) {
                new HiResOptions().parent(this)
                        .message("Scaling has changed from " + scale + " to " + newScale + ".\nChanges will appear next time the application is launched")
                        .info()
                        .show();
                scale = newScale;
            }
        }
        super.setVisible(visible);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        themeGroup = new javax.swing.ButtonGroup();
        cardGroup = new javax.swing.ButtonGroup();
        javax.swing.JPanel backgroundP = new GradientPanel();
        contentP = new javax.swing.JPanel();
        javax.swing.JPanel pathconfigP = new javax.swing.JPanel();
        javax.swing.JPanel jPanel10 = new HiResPanel();
        javax.swing.JLabel jLabel3 = new ActiveLabel();
        javax.swing.JPanel jPanel16 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel14 = new javax.swing.JPanel();
        jdkL = new ActiveLabel();
        androidL = new ActiveLabel();
        javax.swing.JPanel jPanel13 = new javax.swing.JPanel();
        jdkT = new ActiveTextField();
        androidT = new ActiveTextField();
        javax.swing.JPanel jPanel12 = new javax.swing.JPanel();
        jdkB = new HiResButton();
        androidB = new HiResButton();
        javax.swing.JPanel jPanel5 = new HiResPanel();
        javax.swing.JLabel jLabel2 = new ActiveLabel();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JPanel eselectP = new HiResPanel();
        intellijL = new ActiveLabel();
        netbeansL = new ActiveLabel();
        vscodeL = new ActiveLabel();
        javax.swing.JPanel etextP = new HiResPanel();
        intellijT = new ActiveTextField();
        netbeansT = new ActiveTextField();
        vscodeT = new ActiveTextField();
        javax.swing.JPanel ebuttonP = new HiResPanel();
        intellijB = new HiResButton();
        netbeansB = new HiResButton();
        vscodeB = new HiResButton();
        javax.swing.JPanel jPanel11 = new HiResPanel();
        javax.swing.JLabel jLabel6 = new ActiveLabel();
        javax.swing.JPanel jPanel17 = new HiResPanel();
        javax.swing.JPanel jPanel15 = new HiResPanel();
        studioL = new ActiveLabel();
        keystoreL = new ActiveLabel();
        javax.swing.JPanel jPanel18 = new HiResPanel();
        studioT = new ActiveTextField();
        keyT = new ActiveTextField();
        javax.swing.JPanel jPanel19 = new HiResPanel();
        studioB = new HiResButton();
        keystoreB = new HiResButton();
        javax.swing.JPanel jPanel20 = new HiResPanel();
        javax.swing.JLabel jLabel7 = new ActiveLabel();
        javax.swing.JPanel jPanel22 = new HiResPanel();
        lightB = new ActiveRadioButton();
        darkB = new ActiveRadioButton();
        systemB = new ActiveRadioButton();
        jPanel1 = new HiResPanel();
        jLabel1 = new ActiveLabel();
        scaleSlider = new javax.swing.JSlider();
        scaleViewer = new ActiveLabel();
        javax.swing.JPanel pluginsContainerP = new javax.swing.JPanel();
        scrollP = new javax.swing.JScrollPane();
        topP = new GradientPanel();
        boxedP = new javax.swing.JPanel();
        javax.swing.JPanel cardSelectP = new javax.swing.JPanel();
        javax.swing.JToggleButton pathsB = new ActiveToggleButton();
        pluginsB = new ActiveToggleButton();
        javax.swing.JPanel closeP = new HiResPanel();
        javax.swing.JButton closeB = new HiResButton();

        setTitle("CrossMobile Configuration");
        setResizable(false);

        backgroundP.setBorder(new HiResEmptyBorder(12, 12, 0, 12));
        backgroundP.setLayout(new java.awt.BorderLayout());

        contentP.setOpaque(false);
        contentP.setLayout(new java.awt.CardLayout());

        pathconfigP.setOpaque(false);
        pathconfigP.setLayout(new javax.swing.BoxLayout(pathconfigP, javax.swing.BoxLayout.Y_AXIS));

        jPanel10.setBorder(new HiResEmptyBorder(8, 0, 0, 0));
        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel3.setText("Development Environments");
        jPanel10.add(jLabel3, java.awt.BorderLayout.NORTH);

        jPanel16.setOpaque(false);
        jPanel16.setLayout(new java.awt.BorderLayout());

        jPanel14.setOpaque(false);
        jPanel14.setLayout(new java.awt.GridLayout(0, 1));

        jdkL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jdkL.setText("Java JDK");
        jPanel14.add(jdkL);

        androidL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        androidL.setText("Android SDK");
        jPanel14.add(androidL);

        jPanel16.add(jPanel14, java.awt.BorderLayout.WEST);

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridLayout(0, 1));

        jdkT.setEditable(false);
        jdkT.setColumns(28);
        jPanel13.add(jdkT);

        androidT.setEditable(false);
        androidT.setColumns(28);
        jPanel13.add(androidT);

        jPanel16.add(jPanel13, java.awt.BorderLayout.CENTER);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridLayout(0, 1));

        jdkB.setText("Locate");
        jdkB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdkBActionPerformed(evt);
            }
        });
        jPanel12.add(jdkB);

        androidB.setText("Locate");
        androidB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                androidBActionPerformed(evt);
            }
        });
        jPanel12.add(androidB);

        jPanel16.add(jPanel12, java.awt.BorderLayout.EAST);

        jPanel10.add(jPanel16, java.awt.BorderLayout.CENTER);

        pathconfigP.add(jPanel10);

        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(new HiResMatteBorder(1, 0, 0, 0, Theme.current().line), new HiResEmptyBorder(16, 0, 8, 0)));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setText("IDE Editors");
        jPanel5.add(jLabel2, java.awt.BorderLayout.NORTH);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.BorderLayout());

        eselectP.setOpaque(false);
        eselectP.setLayout(new java.awt.GridLayout(0, 1));

        intellijL.setText("IntelliJ IDEA");
        eselectP.add(intellijL);

        netbeansL.setText("Netbeans");
        eselectP.add(netbeansL);

        vscodeL.setText("VS Code");
        eselectP.add(vscodeL);

        jPanel6.add(eselectP, java.awt.BorderLayout.WEST);

        etextP.setOpaque(false);
        etextP.setLayout(new java.awt.GridLayout(0, 1));

        intellijT.setEditable(false);
        intellijT.setColumns(28);
        etextP.add(intellijT);

        netbeansT.setEditable(false);
        netbeansT.setColumns(28);
        etextP.add(netbeansT);

        vscodeT.setEditable(false);
        vscodeT.setColumns(28);
        etextP.add(vscodeT);

        jPanel6.add(etextP, java.awt.BorderLayout.CENTER);

        ebuttonP.setOpaque(false);
        ebuttonP.setLayout(new java.awt.GridLayout(0, 1));

        intellijB.setText("Locate");
        intellijB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intellijBActionPerformed(evt);
            }
        });
        ebuttonP.add(intellijB);

        netbeansB.setText("Locate");
        netbeansB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netbeansBActionPerformed(evt);
            }
        });
        ebuttonP.add(netbeansB);

        vscodeB.setText("Locate");
        vscodeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vscodeBActionPerformed(evt);
            }
        });
        ebuttonP.add(vscodeB);

        jPanel6.add(ebuttonP, java.awt.BorderLayout.EAST);

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        pathconfigP.add(jPanel5);

        jPanel11.setBorder(javax.swing.BorderFactory.createCompoundBorder(new HiResMatteBorder(1, 0, 1, 0, Theme.current().line), new HiResEmptyBorder(16, 0, 8, 0)));
        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel6.setText("Android tools");
        jPanel11.add(jLabel6, java.awt.BorderLayout.NORTH);

        jPanel17.setOpaque(false);
        jPanel17.setLayout(new java.awt.BorderLayout());

        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.GridLayout(0, 1));

        studioL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        studioL.setText("Android Studio");
        jPanel15.add(studioL);

        keystoreL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        keystoreL.setText("Default Keystore  ");
        jPanel15.add(keystoreL);

        jPanel17.add(jPanel15, java.awt.BorderLayout.WEST);

        jPanel18.setOpaque(false);
        jPanel18.setLayout(new java.awt.GridLayout(0, 1));

        studioT.setEditable(false);
        studioT.setColumns(28);
        jPanel18.add(studioT);

        keyT.setEditable(false);
        keyT.setColumns(28);
        jPanel18.add(keyT);

        jPanel17.add(jPanel18, java.awt.BorderLayout.CENTER);

        jPanel19.setOpaque(false);
        jPanel19.setLayout(new java.awt.GridLayout(0, 1));

        studioB.setText("Locate");
        studioB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studioBActionPerformed(evt);
            }
        });
        jPanel19.add(studioB);

        keystoreB.setText("Browse");
        keystoreB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keystoreBActionPerformed(evt);
            }
        });
        jPanel19.add(keystoreB);

        jPanel17.add(jPanel19, java.awt.BorderLayout.EAST);

        jPanel11.add(jPanel17, java.awt.BorderLayout.CENTER);

        pathconfigP.add(jPanel11);

        jPanel20.setBorder(new HiResEmptyBorder(8, 0, 8, 0));
        jPanel20.setOpaque(false);
        jPanel20.setLayout(new java.awt.BorderLayout());

        jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel7.setText("Theme");
        jPanel20.add(jLabel7, java.awt.BorderLayout.NORTH);

        jPanel22.setOpaque(false);
        jPanel22.setLayout(new java.awt.GridLayout(0, 3));

        themeGroup.add(lightB);
        lightB.setText("Light");
        lightB.setActionCommand("light");
        lightB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeActionPerformed(evt);
            }
        });
        jPanel22.add(lightB);

        themeGroup.add(darkB);
        darkB.setText("Dark");
        darkB.setActionCommand("dark");
        darkB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeActionPerformed(evt);
            }
        });
        jPanel22.add(darkB);

        themeGroup.add(systemB);
        systemB.setSelected(true);
        systemB.setText("System");
        systemB.setActionCommand("auto");
        systemB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeActionPerformed(evt);
            }
        });
        jPanel22.add(systemB);

        jPanel20.add(jPanel22, java.awt.BorderLayout.CENTER);

        jPanel1.setBorder(new HiResEmptyBorder(8, 0, 0, 0));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Scale factor  ");
        jPanel1.add(jLabel1, java.awt.BorderLayout.WEST);

        scaleSlider.setMajorTickSpacing(10);
        scaleSlider.setMaximum(50);
        scaleSlider.setMinimum(10);
        scaleSlider.setMinorTickSpacing(5);
        scaleSlider.setValue(10);
        scaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scaleSliderStateChanged(evt);
            }
        });
        jPanel1.add(scaleSlider, java.awt.BorderLayout.CENTER);
        jPanel1.add(scaleViewer, java.awt.BorderLayout.EAST);

        jPanel20.add(jPanel1, java.awt.BorderLayout.SOUTH);

        pathconfigP.add(jPanel20);

        contentP.add(pathconfigP, "paths");

        pluginsContainerP.setOpaque(false);
        pluginsContainerP.setLayout(new java.awt.BorderLayout());

        topP.setLayout(new java.awt.BorderLayout());

        boxedP.setOpaque(false);
        boxedP.setLayout(new javax.swing.BoxLayout(boxedP, javax.swing.BoxLayout.Y_AXIS));
        topP.add(boxedP, java.awt.BorderLayout.NORTH);

        scrollP.setViewportView(topP);

        pluginsContainerP.add(scrollP, java.awt.BorderLayout.CENTER);

        contentP.add(pluginsContainerP, "plugins");

        backgroundP.add(contentP, java.awt.BorderLayout.CENTER);

        cardSelectP.setBorder(new HiResMatteBorder(0, 0, 1, 0, Theme.current().line));
        cardSelectP.setOpaque(false);
        cardSelectP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        cardGroup.add(pathsB);
        pathsB.setSelected(true);
        pathsB.setText("Paths");
        pathsB.setActionCommand("paths");
        pathsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCard(evt);
            }
        });
        cardSelectP.add(pathsB);

        cardGroup.add(pluginsB);
        pluginsB.setText("Plugins");
        pluginsB.setActionCommand("plugins");
        pluginsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCard(evt);
            }
        });
        cardSelectP.add(pluginsB);

        backgroundP.add(cardSelectP, java.awt.BorderLayout.PAGE_START);

        closeP.setOpaque(false);
        closeP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        closeB.setText("Close");
        closeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBActionPerformed(evt);
            }
        });
        closeP.add(closeB);

        backgroundP.add(closeP, java.awt.BorderLayout.SOUTH);

        getContentPane().add(backgroundP, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void netbeansBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netbeansBActionPerformed
        netbeansB.setEnabled(false);
        JWizard wiz = new JWizard("Netbeans");
        wiz.setCallback((String fname) -> {
            if (fname != null) {
                Prefs.setNetbeansLocation(fname);
                netbeansT.setText(fname);
            }
            netbeansB.setEnabled(true);
        });

        wiz.fire(Netbeans, Prefs.getNetbeansLocation());
    }//GEN-LAST:event_netbeansBActionPerformed

    private void closeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeBActionPerformed

    private void jdkBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdkBActionPerformed
        jdkB.setEnabled(false);
        JWizard wiz = new JWizard("Java Development Kit " + JAVA_RANGE);
        wiz.setCallback((String fname) -> {
            if (fname != null) {
                Prefs.setJDKLocation(fname);
                jdkT.setText(fname);
            }
            jdkB.setEnabled(true);
        });
        wiz.fire(JDK, Prefs.getJDKLocation());
    }//GEN-LAST:event_jdkBActionPerformed

    private void androidBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_androidBActionPerformed
        androidB.setEnabled(false);
        JWizard wiz = new JWizard("Android SDK");
        wiz.setCallback((String fname) -> {
            if (fname != null) {
                Prefs.setAndroidSDKLocation(fname);
                androidT.setText(fname);
            }
            androidB.setEnabled(true);
        });
        wiz.fire(Android, Prefs.getAndroidSDKLocation());
    }//GEN-LAST:event_androidBActionPerformed

    private void keystoreBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keystoreBActionPerformed
        String newkey = KeystoreManager.browseKeystore();
        if (newkey != null) {
            keyT.setText(newkey);
            Prefs.setAndroidKeyLocation(newkey);
        }
    }//GEN-LAST:event_keystoreBActionPerformed

    private void studioBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studioBActionPerformed
        studioB.setEnabled(false);
        JWizard wiz = new JWizard("Android Studio");
        wiz.setCallback((String fname) -> {
            if (fname != null) {
                Prefs.setStudioLocation(fname);
                studioT.setText(fname);
            }
            studioB.setEnabled(true);
        });
        wiz.fire(Studio, Prefs.getAndroidStudioLocation());
    }//GEN-LAST:event_studioBActionPerformed

    private void intellijBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intellijBActionPerformed
        JWizard wiz = new JWizard("IntelliJ IDEA");
        wiz.setCallback((String fname) -> {
            if (fname != null) {
                Prefs.setIntelliJLocation(fname);
                intellijT.setText(fname);
            }
        });
        wiz.fire(IntelliJ, Prefs.getIntelliJLocation());
    }//GEN-LAST:event_intellijBActionPerformed

    private void themeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeActionPerformed
        switch (((ActiveRadioButton) evt.getSource()).getActionCommand()) {
            case "auto":
                Theme.setAuto();
                break;
            case "dark":
                Theme.setDark();
                break;
            default:
                Theme.setBright();
        }
    }//GEN-LAST:event_themeActionPerformed

    private void selectCard(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCard
        ((CardLayout) contentP.getLayout()).show(contentP, evt.getActionCommand());
    }//GEN-LAST:event_selectCard

    private void vscodeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vscodeBActionPerformed
        JWizard wiz = new JWizard("Visual Studio Code");
        wiz.setCallback((String fname) -> {
            if (fname != null) {
                Prefs.setVSCodeLocation(fname);
                vscodeT.setText(fname);
            }
        });
        wiz.fire(VSCode, Prefs.getVSCodeLocation());
    }//GEN-LAST:event_vscodeBActionPerformed

    private void scaleSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scaleSliderStateChanged
        updateScale(scaleSlider.getValue() / 10f, true, false);
    }//GEN-LAST:event_scaleSliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton androidB;
    private javax.swing.JLabel androidL;
    private javax.swing.JTextField androidT;
    private javax.swing.JPanel boxedP;
    private javax.swing.ButtonGroup cardGroup;
    private javax.swing.JPanel contentP;
    private javax.swing.JRadioButton darkB;
    private javax.swing.JButton intellijB;
    private javax.swing.JLabel intellijL;
    private javax.swing.JTextField intellijT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jdkB;
    private javax.swing.JLabel jdkL;
    private javax.swing.JTextField jdkT;
    private javax.swing.JTextField keyT;
    private javax.swing.JButton keystoreB;
    private javax.swing.JLabel keystoreL;
    private javax.swing.JRadioButton lightB;
    private javax.swing.JButton netbeansB;
    private javax.swing.JLabel netbeansL;
    private javax.swing.JTextField netbeansT;
    private javax.swing.JToggleButton pluginsB;
    private javax.swing.JSlider scaleSlider;
    private javax.swing.JLabel scaleViewer;
    private javax.swing.JScrollPane scrollP;
    private javax.swing.JButton studioB;
    private javax.swing.JLabel studioL;
    private javax.swing.JTextField studioT;
    private javax.swing.JRadioButton systemB;
    private javax.swing.ButtonGroup themeGroup;
    private javax.swing.JPanel topP;
    private javax.swing.JButton vscodeB;
    private javax.swing.JLabel vscodeL;
    private javax.swing.JTextField vscodeT;
    // End of variables declaration//GEN-END:variables
}
