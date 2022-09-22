/*
 * (c) 2022 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui;

import com.panayotis.appenh.AFileChooser;
import com.panayotis.appenh.EnhancerManager;
import com.panayotis.hrgui.*;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.Updater;
import org.crossmobile.bridge.system.BaseUtils;
import org.crossmobile.gui.actives.*;
import org.crossmobile.gui.elements.*;
import org.crossmobile.gui.project.ProjectInfo;
import org.crossmobile.gui.project.ProjectListModel;
import org.crossmobile.gui.project.ProjectLoader;
import org.crossmobile.gui.project.RecentsProjectManager;
import org.crossmobile.gui.utils.Paths;
import org.crossmobile.gui.utils.Paths.HomeReference;
import org.crossmobile.gui.utils.PluginInstaller;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.Log;
import org.crossmobile.utils.ProjectException;

import javax.swing.*;
import javax.swing.JPopupMenu.Separator;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;

import static com.panayotis.appenh.AFileChooser.FileSelectionMode.FilesAndDirectories;
import static com.panayotis.hrgui.ScreenUtils.isHiDPI;
import static org.crossmobile.Version.RELEASE;
import static org.crossmobile.Version.VERSION;
import static org.crossmobile.gui.utils.PluginInstaller.isPlugin;

public class WelcomeFrame extends RegisteredFrame implements UpdatedApplication {

    private static final AFileChooser afc = new AFileChooser().setDirectory(Prefs.getCurrentDir()).setMode(FilesAndDirectories);

    private ProjectListModel projlist;
    private boolean textualVersion = true;
    private Updater updater;

    static {
        ScreenUtils.setTint(Theme.current().icontop, Theme.current().iconbottom);
    }

    @SuppressWarnings({"unchecked", "LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
    public WelcomeFrame() {
        initComponents();
        updateVersion();
        ProjectsL.setCellRenderer(new CustomListRenderer());
        EnhancerManager.getDefault().updateFrameIcons(this);
        setSize(640, 520);
        setLocationRelativeTo(null);
        ProjectsL.setTransferHandler(new DnDFileHandler(file -> addProject(file, false)));
        setEnabled(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        ProjectsL.setEnabled(enabled);
        newProjectB.setEnabled(enabled);
        openProjectB.setEnabled(enabled);
        settingsB.setEnabled(enabled);
        aboutB.setEnabled(enabled);
        openB.setEnabled(enabled);
        clearAllB.setEnabled(enabled);
    }

    public final void updateProjects(ProjectInfo defaultProject) {
        if (!isEnabled())
            setEnabled(true);
        if (projlist == null) {
            projlist = new ProjectListModel();
            ProjectsL.setModel(projlist);
        } else
            projlist.reload();
        if (defaultProject == null)
            ProjectsL.setSelectedIndex(0);
        else {
            RecentsProjectManager.addProject(defaultProject, false);
            ProjectsL.setSelectedIndex(projlist.getIndex(defaultProject));
            repaint();
        }
    }

    private void showProject(int index) {
        if (index >= 0 && index < projlist.getSize())
            ProjectLoader.showProject(projlist.getElementAt(index), this);
    }

    @Override
    public boolean requestRestart() {
        return true;
    }

    @Override
    public void receiveMessage(String message) {
        Log.debug(message);
    }

    public int getIndexFromPoint(Point p) {
        if (p != null) {
            Rectangle r = ProjectsL.getCellBounds(0, ProjectsL.getLastVisibleIndex());
            if (r != null && r.contains(p))
                return ProjectsL.locationToIndex(p);
        }
        return -1;
    }

    private void updateVersion() {
        versionL.setText(textualVersion ? "version " + VERSION + " " : "release " + RELEASE + " ");
    }

    private boolean addProject(File file, boolean alsoInformUser) {
        try {
            if (isPlugin.test(file) || BaseUtils.listFiles(file).stream().anyMatch(isPlugin))
                PluginInstaller.installPlugin(this, file);
            else
                addProject(file);
            return true;
        } catch (ProjectException ex) {
            if (alsoInformUser)
                new HiResOptions().parent(this).message(ex.getMessage()).title("Error while opening project").error().show();
        }
        return false;
    }

    private void addProject(File file) throws ProjectException {
        ProjectLoader.showProject(ProjectInfo.load(file.getAbsolutePath()), this);
    }

    public void setLink(String text, Runnable action) {
        actionL.setEnabled(text != null && !text.isEmpty());
        actionL.setText(text);
        ((ActiveLink) actionL).setAction(action);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        itemPopUp = new ActivePopupMenu();
        openM = new ActiveMenuItem();
        jSeparator1 = new ActiveMenuSeparator();
        removeM = new ActiveMenuItem();
        selectionB = new ButtonGroup();
        Background = new GradientPanel();
        jPanel3 = new JPanel();
        jLabel2 = new ActiveLabel();
        ProjectsSP = new JScrollPane();
        ProjectsL = new ActiveList();
        jPanel10 = new JPanel();
        clearAllB = new ActiveButton();
        openB = new ActiveButton();
        jPanel8 = new JPanel();
        jPanel4 = new JPanel();
        jPanel1 = new JPanel();
        jLabel3 = new ActiveLabel();
        jLabel4 = new ActiveLabel();
        versionL = new ActiveLabel();
        jLabel1 = new ActiveLabel();
        jPanel5 = new JPanel();
        jPanel7 = new JPanel();
        newProjectB = new ActiveButton();
        openProjectB = new ActiveButton();
        jPanel9 = new JPanel();
        settingsB = new ActiveButton();
        aboutB = new ActiveButton();
        jPanel6 = new JPanel();
        actionL = new ActiveLink();

        openM.setText("Open project");
        openM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openMActionPerformed(evt);
            }
        });
        itemPopUp.add(openM);
        itemPopUp.add(jSeparator1);

        removeM.setText("Remove from list");
        removeM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeMActionPerformed(evt);
            }
        });
        itemPopUp.add(removeM);

        setTitle("CrossMobile Manager");
        setMinimumSize(new Dimension(640, 408));
        getContentPane().setLayout(new BorderLayout());

        Background.setLayout(new BorderLayout());

        jPanel3.setBorder(new HiResEmptyBorder(2,2,2,24));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new BorderLayout(0, 2));

        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText("Recent Projects");
        jLabel2.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 8));
        jPanel3.add(jLabel2, BorderLayout.NORTH);

        ProjectsSP.setMinimumSize(new Dimension(260, 400));
        ProjectsSP.setPreferredSize(ProjectsSP.getMinimumSize());

        ProjectsL.setFont(ProjectsL.getFont().deriveFont(ProjectsL.getFont().getSize()+1f));
        ProjectsL.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                ProjectsLMouseClicked(evt);
            }
        });
        ProjectsL.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                ProjectsLValueChanged(evt);
            }
        });
        ProjectsSP.setViewportView(ProjectsL);

        jPanel3.add(ProjectsSP, BorderLayout.CENTER);

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new FlowLayout(2));

        clearAllB.setIcon(new ActiveIcon("images/trash"));
        clearAllB.setText("Clear all");
        clearAllB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clearAllBActionPerformed(evt);
            }
        });
        jPanel10.add(clearAllB);

        openB.setIcon(new ActiveIcon("images/arrow"));
        openB.setText("Open");
        openB.setEnabled(false);
        openB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openBActionPerformed(evt);
            }
        });
        jPanel10.add(openB);

        jPanel3.add(jPanel10, BorderLayout.SOUTH);

        Background.add(jPanel3, BorderLayout.CENTER);

        jPanel8.setBorder(new HiResEmptyBorder(16,8,8,4));
        jPanel8.setOpaque(false);
        jPanel8.setLayout(new BorderLayout());

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new BorderLayout(0, 2));

        jPanel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new GridLayout(0, 1));

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | Font.BOLD, jLabel3.getFont().getSize()+15));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("Welcome to");
        jPanel1.add(jLabel3);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | Font.BOLD, jLabel4.getFont().getSize()+15));
        jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel4.setText("CrossMobile");
        jPanel1.add(jLabel4);

        jPanel4.add(jPanel1, BorderLayout.NORTH);

        versionL.setFont(versionL.getFont().deriveFont((versionL.getFont().getStyle() | Font.ITALIC), versionL.getFont().getSize()-1));
        versionL.setHorizontalAlignment(SwingConstants.CENTER);
        versionL.setText("release");
        versionL.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                versionLMousePressed(evt);
            }
        });
        jPanel4.add(versionL, BorderLayout.SOUTH);

        jPanel8.add(jPanel4, BorderLayout.NORTH);

        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setIcon(new HiResIcon("images/logo", false));
        jLabel1.setBorder(new HiResEmptyBorder(16,0,30,0));
        jPanel8.add(jLabel1, BorderLayout.CENTER);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new GridLayout(0, 2, 0, 8));

        newProjectB.setIcon(new ActiveIcon("images/new"));
        newProjectB.setText("New ...");
        newProjectB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newProjectBActionPerformed(evt);
            }
        });
        jPanel7.add(newProjectB);

        openProjectB.setIcon(new ActiveIcon("images/open"));
        openProjectB.setText("Open ...");
        openProjectB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openProjectBActionPerformed(evt);
            }
        });
        jPanel7.add(openProjectB);

        jPanel5.add(jPanel7);

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new GridLayout(0, 2, 0, 8));

        settingsB.setIcon(new ActiveIcon("images/configure"));
        settingsB.setText("Settings");
        settingsB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                settingsBActionPerformed(evt);
            }
        });
        jPanel9.add(settingsB);

        aboutB.setIcon(new ActiveIcon("images/help"));
        aboutB.setText("About");
        aboutB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutBActionPerformed(evt);
            }
        });
        jPanel9.add(aboutB);

        jPanel5.add(jPanel9);

        jPanel6.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        jPanel6.setOpaque(false);
        jPanel6.setLayout(new FlowLayout(0));

        actionL.setEnabled(false);
        jPanel6.add(actionL);

        jPanel5.add(jPanel6);

        jPanel8.add(jPanel5, BorderLayout.SOUTH);

        Background.add(jPanel8, BorderLayout.WEST);

        getContentPane().add(Background, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void newProjectBActionPerformed(ActionEvent evt) {//GEN-FIRST:event_newProjectBActionPerformed
        try {
            NewProjectInfo newProjectInfo = new NewProjectInfo();
            newProjectInfo.setVisible(true);
            if (newProjectInfo.getProjectPath() != null)
                ProjectLoader.showProject(ProjectInfo.create(newProjectInfo.getProjectPath().getAbsolutePath(), newProjectInfo), this);
        } catch (ProjectException ex) {
            new HiResOptions().parent(this).message(ex.getMessage()).title("Error while opening project").error().show();
        }
    }//GEN-LAST:event_newProjectBActionPerformed

    private void ProjectsLValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_ProjectsLValueChanged
        openB.setEnabled(true);
    }//GEN-LAST:event_ProjectsLValueChanged

    private void openProjectBActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openProjectBActionPerformed
        Collection<File> result = afc.setDirectory(Prefs.getCurrentDir()).openMulti();
        for (File selected : result) {
            addProject(selected, true);
            Prefs.setCurrentDir(selected.getParentFile());
        }
    }//GEN-LAST:event_openProjectBActionPerformed

    private void openBActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openBActionPerformed
        showProject(ProjectsL.getSelectedIndex());
    }//GEN-LAST:event_openBActionPerformed

    private void ProjectsLMouseClicked(MouseEvent evt) {//GEN-FIRST:event_ProjectsLMouseClicked
        if (!ProjectsL.isEnabled())
            return;
        int sel = getIndexFromPoint(evt.getPoint());
        if (evt.getButton() == MouseEvent.BUTTON3 && evt.getClickCount() == 1) {
            if (sel >= 0 && sel < projlist.getSize()) {
                ProjectsL.setSelectedIndex(sel);
                itemPopUp.show(evt.getComponent(), evt.getX() - 10, evt.getY() - 10);
            }
        } else if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
            showProject(sel);
    }//GEN-LAST:event_ProjectsLMouseClicked

    private void clearAllBActionPerformed(ActionEvent evt) {//GEN-FIRST:event_clearAllBActionPerformed
        if (new HiResOptions().parent(this).message("Do you really want to clear all recent projects?").title("Clear projects").buttons("Yes", "No").warning().show() == 0) {
            RecentsProjectManager.clearProjects();
            updateProjects(null);
        }
    }//GEN-LAST:event_clearAllBActionPerformed

    private void aboutBActionPerformed(ActionEvent evt) {//GEN-FIRST:event_aboutBActionPerformed
        About.showAbout();
    }//GEN-LAST:event_aboutBActionPerformed

    private void settingsBActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingsBActionPerformed
        Config.showConfig();
    }//GEN-LAST:event_settingsBActionPerformed

    private void openMActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openMActionPerformed
        showProject(ProjectsL.getSelectedIndex());
    }//GEN-LAST:event_openMActionPerformed

    private void removeMActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeMActionPerformed
        int index = ProjectsL.getSelectedIndex();
        if (index >= 0 && index < projlist.getSize()) {
            RecentsProjectManager.deleteProject(projlist.getElementAt(index));
            updateProjects(null);
        }
    }//GEN-LAST:event_removeMActionPerformed

    private void versionLMousePressed(MouseEvent evt) {//GEN-FIRST:event_versionLMousePressed
        if (evt.getButton() == MouseEvent.BUTTON1) {
            textualVersion = !textualVersion;
            updateVersion();
        }
    }//GEN-LAST:event_versionLMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel Background;
    public JList ProjectsL;
    private JScrollPane ProjectsSP;
    private JButton aboutB;
    private JLabel actionL;
    private JButton clearAllB;
    private JPopupMenu itemPopUp;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JPanel jPanel10;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JPanel jPanel8;
    private JPanel jPanel9;
    private Separator jSeparator1;
    private JButton newProjectB;
    private JButton openB;
    private JMenuItem openM;
    private JButton openProjectB;
    private JMenuItem removeM;
    private ButtonGroup selectionB;
    private JButton settingsB;
    private JLabel versionL;
    // End of variables declaration//GEN-END:variables

}

final class CustomListRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object item, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel CellRenderer = new JPanel();
        JPanel TextPart = new JPanel();
        ActiveLabel icon = new ActiveLabel();
        ActiveLabel name = new ActiveLabel();
        ActiveLabel location = new ActiveLabel();
        Font font = list.getFont();
        ProjectInfo projectInfo = (ProjectInfo) item;

        int scaledSize = 32 * (isHiDPI() ? 2 : 1);
        BufferedImage image = projectInfo.getImageHound().findFore(scaledSize, true).withBackground(projectInfo.getImageHound().findBack(scaledSize, true)).image;
        icon.setIcon(new HiResIcon(image, 1));
        icon.setBorder(new HiResEmptyBorder(4, 4, 4, 8));

        name.setText(projectInfo.getName());
        name.setEnabled(list.isEnabled());
        name.setForeground(isSelected ? Theme.current().textSelCell : Theme.current().text);
        name.setFont(font);

        location.setText(Paths.getPath(projectInfo.getPath(), HomeReference.PATH_STYLE));
        location.setEnabled(list.isEnabled());
        location.setFont(font.deriveFont(font.getSize() - 1f).deriveFont(Font.ITALIC));
        location.setForeground(isSelected ? Theme.current().infoSelCell : Theme.current().subinfo);

        TextPart.setOpaque(false);
        TextPart.setLayout(new BorderLayout());
        TextPart.add(name, BorderLayout.CENTER);
        TextPart.add(location, BorderLayout.SOUTH);

        CellRenderer.setLayout(new BorderLayout());
        CellRenderer.setBorder(new CompoundBorder(new HiResMatteBorder(0, 0, 1, 0, Theme.current().tableLine), new HiResEmptyBorder(3, 2, 3, 2)));
        CellRenderer.setBackground(isSelected ? Theme.current().backCellSelected : Theme.current().backCell);
        CellRenderer.add(icon, BorderLayout.WEST);
        CellRenderer.add(TextPart, BorderLayout.CENTER);
        return CellRenderer;
    }
}
