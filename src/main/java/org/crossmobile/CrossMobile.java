/*
 * (c) 2020 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile;

import com.panayotis.appenh.Enhancer;
import com.panayotis.appenh.EnhancerManager;
import com.panayotis.hrgui.HiResOptions;
import com.panayotis.jupidator.Updater;
import org.crossmobile.backend.desktop.ResourceResolver;
import org.crossmobile.bridge.system.BaseUtils;
import org.crossmobile.gui.WelcomeFrame;
import org.crossmobile.gui.elements.About;
import org.crossmobile.gui.elements.Config;
import org.crossmobile.gui.elements.Theme;
import org.crossmobile.gui.init.InitializationWizard;
import org.crossmobile.gui.init.InitializationWizard.Card;
import org.crossmobile.gui.project.ProjectInfo;
import org.crossmobile.gui.project.ProjectLoader;
import org.crossmobile.gui.utils.Paths;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.crossmobile.Version.RELEASE;
import static org.crossmobile.Version.VERSION;
import static org.crossmobile.gui.elements.Config.*;
import static org.crossmobile.gui.init.ApplicationRequirements.isAndroidConfigured;
import static org.crossmobile.gui.init.ApplicationRequirements.isJDKconfigured;

public class CrossMobile {

    public static void main(final String[] args) {
        initEnhancer();
        System.setProperty("awt.useSystemAAFontSettings","on");

        java.awt.EventQueue.invokeLater(() -> {
            try {
                if (args == null || args.length == 0 || args[0].startsWith("-psn_")) {
                    Log.register(new Log.Default() {
                        @Override
                        public void info(String message) {
                            new HiResOptions().message(message).title("CrossMobile").show();
                        }

                        @Override
                        public void warning(String message) {
                            new HiResOptions().message(message).title("CrossMobile").warning().show();
                        }

                        @Override
                        public void error(String message, Throwable th) {
                            if (message != null || th != null) {
                                if (message != null)
                                    System.err.println(message);
                                if (th != null) {
                                    th.printStackTrace(System.err);
                                    if (message == null)
                                        message = th.toString();
                                }
                                new HiResOptions().message(message).title("CrossMobile").error().show();
                            }
                        }
                    });
                    WelcomeFrame frame = new WelcomeFrame();
                    frame.setVisible(true);
                    frame.setResizable(false);
                    SwingUtilities.invokeLater(() -> postInit(frame));
                } else if (args.length == 2 && args[0].equals("--project"))
                    ProjectLoader.showProject(ProjectInfo.load(args[1]), null);
                else if (args.length == 1 && args[0].equals("--help"))
                    showHelp(0);
                else
                    showError(args);
            } catch (IllegalStateException | ProjectException ex) {
                new HiResOptions().message(ex.getMessage()).title("Initialization error").buttons("OK", "Cancel").error().show();
                System.exit(-1);
            }
        });
    }

    private static void initEnhancer() {
        Enhancer enhancer = EnhancerManager.getDefault();
        enhancer.setSafeLookAndFeel();
        enhancer.registerPreferences(Config::showConfig);
        enhancer.registerAbout(About::showAbout);
        enhancer.setApplicationIcons("images/logo-frame.png");
        enhancer.registerApplication("CrossMobile", "create native iOS, Android, Windows 10 and Desktop Applications from a singe code base", "Development", "Building", "IDE", "Java");
        Theme.setSystemTheme(enhancer.getThemeName());
        enhancer.registerThemeChanged(name -> SwingUtilities.invokeLater(() -> Theme.setSystemTheme(name)));
    }

    private static void postInit(WelcomeFrame frame) {
        if (!Prefs.isWizardExecuted())
            executeWizard(frame);
        else if (!isAndroidConfigured() && !isJDKconfigured())
            frame.setLink("Configure CrossMobile environment", () -> executeWizard(frame));
        else if (!isAndroidConfigured())
            frame.setLink("Configure Android SDK", Config::showConfig);
        else if (!isJDKconfigured())
            frame.setLink("Configure JDK", Config::showConfig);
        new Thread(() -> {
            Updater updater = Updater.start("https://crossmobile.org/content/repositories/crossmobile/crossmobile.xml", Paths.getApplicationPath(), RELEASE, VERSION, frame, true, false);
            if (updater != null && updater.isUpdatable())
                frame.setLink("New version found", updater::actionDisplay);
        }).start();
        frame.updateProjects(null);

        ResourceResolver.getResources("META-INF/default-plugins/skins.xml", PluginRegistry::importPlugin);
        ResourceResolver.getResources("META-INF/default-plugins/plugins.xml", PluginRegistry::importPlugin);
        ResourceResolver.getResources("META-INF/default-plugins/cmtheme-bright.xml", PluginRegistry::importPlugin);
        BaseUtils.listFiles(new File(SystemDependent.getMetaPluginsDir())).forEach(PluginRegistry::importFilePlugin);
    }

    private static void executeWizard(WelcomeFrame frame) {
        InitializationWizard initW = new InitializationWizard(frame);
        initW.setMainTitle("Welcome to CrossMobile");
        initW.setSubtitle("Before we begin, it is required to check your system for installed components");
        initW.setWelcomeInfo("When you click on \"Continue\" the wizard will try to find installations of ",
                "required applications to run CrossMobile ",
                "These applications are Java JDK, Android SDK, IntelliJ IDEA, VS Code, ",
                "Netbeans and Android Studio. ",
                "Please press \"Continue\" to start searching for these applications. ");
        Runnable skip = () -> {
            initW.setActive(false);
            if (initW.isVisible())
                initW.setVisible(false);
            frame.setVisible(true);
        };
        initW.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                skip.run();
            }
        });
        initW.setAction("Continue", () -> {
            initW.gotoCard(Card.Externals);
            initW.setAction("Cancel", skip);
            initW.setSubtitle("Looking for installed components");
            initW.setRunning(true);
            Collection<LocationRequest> entries = asList(
                    Netbeans.makeRequest(initW::foundNetbeans),
                    JDK.makeRequest(initW::foundJDK),
                    IntelliJ.makeRequest(initW::foundIntelliJ),
                    VSCode.makeRequest(initW::foundVSCode),
                    Studio.makeRequest(initW::foundStudio),
                    Android.makeRequest(initW::foundAndroid)
            );
            new Thread(() -> {
                TreeWalker.searchExecutable(entries, null, true, initW);
                if (initW.isVisible())
                    initW.setTreeWalking(false);
            }).start();
        });
        initW.setVisible(true);
    }

    private static void showHelp(int exitCode) {
        System.err.println("CrossMobile frontend");
        System.err.println("Arguments:");
        System.err.println("  --help              : This help message");
        System.err.println("  --project [PROJECT] : Open a project at the specified location");
        SplashScreen ss = SplashScreen.getSplashScreen();
        if (ss != null)
            ss.close();
        System.exit(exitCode);
    }

    private static void showError(String[] args) {
        System.err.print("Unable to understand arguments: ");
        for (String arg : args)
            System.err.print("'" + arg + "' ");
        System.err.println();
        System.err.println();
        showHelp(1);
    }
}
