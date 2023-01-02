/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import org.crossmobile.gui.actives.ThemeManager;
import org.crossmobile.prefs.Prefs;
import org.crossmobile.utils.SystemDependent;

import java.awt.*;

public final class Theme {

    private static final Theme bright;
    private static final Theme dark;

    private static Theme current;

    private final boolean isDark;
    public final Color areaPrimary;
    public final Color areaSecondary;
    public final Color line;
    public final Color shortLine;
    public final Color tableLine;
    public final Color backCell;
    public final Color text;
    public final Color textBack;
    public final Color textBorder;
    public final Color textInfo;
    public final Color textError;
    public final Color textWarning;
    public final Color textDebug;
    public final Color backgroundRunning;
    public final Color backgroundWarning;
    public final Color backgroundError;
    public final Color backgroundSuccess;
    public final Color subinfo;
    public final Color backCellSelected;
    public final Color textSelCell;
    public final Color infoSelCell;
    public final Color rolloverButton;
    public final Color pressedButton;
    public final Color selectedButton;
    public final Color icontop;
    public final Color iconbottom;
    public final Color disabled;

    static {
        bright = new Theme(0.49f, 0.57f, false);
        dark = new Theme(0.49f, 0.57f, true);
        SystemDependent.setUiFont("/fonts/DroidSans.ttf");
        switch (Prefs.getUserTheme()) {
            case "dark":
                current = dark;
                break;
            case "bright":
                current = bright;
                break;
            default:
                updateThemeFromSystem();
        }
    }

    public static Theme current() {
        return current;
    }

    public static Theme bright() {
        return bright;
    }

    public static Theme dark() {
        return dark;
    }

    public static void setDark() {
        current = dark;
        Prefs.setUserTheme("dark");
        updateWindows();
    }

    public static void setBright() {
        current = bright;
        Prefs.setUserTheme("bright");
        updateWindows();
    }

    public static void setAuto() {
        Prefs.setUserTheme("auto");
        updateThemeFromSystem();
    }

    public static void setSystemTheme(String name) {
        Prefs.setSystemTheme(name);
        if ("auto".equals(Prefs.getUserTheme()))
            updateThemeFromSystem();
    }

    private static void updateThemeFromSystem() {
        if (Prefs.getSystemTheme().toLowerCase().contains("dark"))
            current = dark;
        else
            current = bright;
        updateWindows();
    }

    private static void updateWindows() {
        ThemeManager.notifyListeners(current().isDark);
        Window[] windows = Window.getWindows();
        if (windows != null && windows.length > 0)
            for (Window w : windows)
                w.repaint();
    }


    private Color c(float hue, float saturation, float value) {
        return isDark ?
                Color.getHSBColor(hue, saturation, 1 - (value * 8 / 9)) :
                Color.getHSBColor(hue, saturation, value);
    }

    private Theme(float primary, float secondary, boolean dark) {
        isDark = dark;
        areaPrimary = c(primary, 0.117f, 0.937f);
        areaSecondary = c(secondary, 0.155f, 0.937f);
        line = c(primary, 0.1f, 0.427f);
        shortLine = c(primary, 0.8f, 0.683f);
        tableLine = areaSecondary;
        backCell = c(primary, 0, isDark ? 0.93f : 1);
        text = c(primary, 0, isDark ? 0.1f : 0);
        textBorder = c(primary, 0, 0.8f);
        textBack = backCell;
        subinfo = c(primary, 0, 0.5f);
        disabled = c(primary, 0, 0.8f);
        backCellSelected = c(primary, 0.1f, 0.8f);
        textSelCell = text;
        infoSelCell = subinfo;
        textInfo = c(0.65f, dark ? 0.35f : 0.93f, dark ? 0f : 0.55f);
        textError = c(0, dark ? 0.5f : 0.93f, dark ? 0.1f : 0.55f);
        textWarning = c(0.15f, 0.93f, dark ? 0.1f : 0.55f);
        textDebug = subinfo;
        rolloverButton = c(primary, 0.09f, 1f);
        pressedButton = c(primary, 0.079f, 0.596f);
        selectedButton = c(secondary, 0, isDark ? 0.85f : 1);
        icontop = c(secondary, 0.307f, 0.204f);
        iconbottom = c(secondary, 0.313f, 0.513f);
        backgroundRunning = c(0.65f, 0.5f, dark ? 0.5f : 1f);
        backgroundWarning = c(0.15f, 0.5f, dark ? 0.5f : 0.9f);
        backgroundError = c(0f, 0.5f, dark ? 0.5f : 0.9f);
        backgroundSuccess = c(0.35f, 0.5f, dark ? 0.5f : 0.9f);
    }

}
