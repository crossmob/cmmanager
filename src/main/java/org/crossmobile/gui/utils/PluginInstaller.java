/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.utils;

import org.crossmobile.gui.init.InitializationWizard;
import org.crossmobile.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.crossmobile.bridge.system.BaseUtils.listFiles;
import static org.crossmobile.utils.SystemDependent.getMetaPluginsDir;

public class PluginInstaller {
    public static final Predicate<File> isPlugin = f -> f.isFile() && f.getName().toLowerCase().endsWith(".cmp");

    private final Queue<Runnable> actions = new LinkedList<>();
    private final InitializationWizard initW;
    private final File outDir;

    public static void installPlugin(Window parentWindow, File plugin) throws ProjectException {
        File pluginFile = plugin.isDirectory()
                ? listFiles(plugin).stream().filter(isPlugin).findFirst().orElseThrow(() -> new ProjectException("Unable to locate CrossMobile Plugin"))
                : plugin;

        InitializationWizard initW = new InitializationWizard(parentWindow);
        initW.setTitle("Installing plugin " + plugin.getName());
        initW.gotoCard(InitializationWizard.Card.Info);
        initW.hideActionButton();
        initW.setRunning(true);
        initW.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                EventUtils.postAction(() -> new PluginInstaller(pluginFile, initW).next()); // start the installer with the first action (create temporary folder)
            }
        });
        initW.setVisible(true);
    }

    // Create a list of actions
    private PluginInstaller(File cmp, InitializationWizard initW) {
        this.initW = initW;
        outDir = FileUtils.createTempDir();
        // first create a temporary folder to store the extracted files
        actions.add(() -> {
            if (outDir == null)
                displayError("Unable to initialize extraction directory");
            else {
                displayInfo("Extracting plugin files");
                actions.add(() -> {
                    // Then unzip the plugin to the temporary folder
                    if (!FileUtils.unzip(cmp, outDir))
                        displayError("Unable to extract plugin files");
                    else {
                        listFiles(outDir).stream().filter(f -> f.isFile() && f.getName().endsWith(".jar") && !f.getName().endsWith("-sources.jar")).map(jar -> (Runnable) () -> {
                            // For every file, add an action to install this file in sequence
                            displayInfo("Installing " + jar.getName());
                            File parent = jar.getParentFile();
                            String base = jar.getName().substring(0, jar.getName().length() - 4);
                            File pom = new File(parent, base + ".pom");
                            File sources = new File(parent, base + "-sources.jar");
                            if (!pom.exists()) {
                                displayError("Unable to find POM " + pom.getName());
                            } else {
                                Commander cmd = CMMvnActions.installArtifact(jar, pom, sources.isFile() ? sources : null);
                                cmd.setCharOutListener(initW.getStreamManager()::incomingOutChar);
                                cmd.setCharErrListener(initW.getStreamManager()::incomingOutChar);
                                cmd.setDebug(true);
                                cmd.setEndListener(code -> {
                                    if (!code.equals(0))
                                        displayError("Error while installing " + jar.getName());
                                    else {
                                        String error = installPluginMetaInfo(jar);
                                        if (error != null)
                                            displayError(error);
                                        else
                                            next(); // finish current file; go to next installable file (if exists)
                                    }
                                });
                                cmd.exec();
                            }
                        }).forEachOrdered(actions::add);
                        next(); // finish unzip; go to first file (if exists)
                    }
                });
                next(); // finish create temporary folder; go to unzip
            }
        });
    }

    public static String installPluginMetaInfo(File jar) {
        File pluginsDir = new File(getMetaPluginsDir());
        pluginsDir.mkdirs();
        String outName = jar.getName().substring(0, jar.getName().length() - 4) + "-plugin.xml";
        File metaFile = new File(pluginsDir, outName);

        try (JarInputStream is = new JarInputStream(new FileInputStream(jar))) {
            JarEntry next;
            while ((next = is.getNextJarEntry()) != null)
                if (PluginMetaData.CURRENT_PLUGIN_REGISTRY.equals(next.getName())) {
                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(metaFile))) {
                        if (!FileUtils.copyStream(is, os))
                            throw new IOException("Unable to read plugin meta data");
                        else break;
                    } catch (IOException e) {
                        Log.error(e);
                        return "Unable to extract meta info from " + jar.getName();
                    }
                }
        } catch (IOException e) {
            Log.error(e);
            return "Unable to extract meta info from " + jar.getName();
        }
        if (metaFile.exists())
            PluginRegistry.importFilePlugin(metaFile);
        return null;
    }

    private void next() {
        if (actions.isEmpty()) {
            FileUtils.delete(outDir);
            initW.setVisible(false);
        } else
            SwingUtilities.invokeLater(actions.poll());
    }

    private void displayError(String error) {
        initW.setRunning(false);
        initW.setSubtitle(error);
        initW.gotoCard(InitializationWizard.Card.Details);
    }

    private void displayInfo(String info) {
        initW.setSubtitle(info);
    }
}
