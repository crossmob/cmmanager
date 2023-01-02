/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Pattern;

public class VersionDocumentFilter extends DocumentFilter {

    private static final int MAX_PARTS = 4;
    public static final String TOOLTIP = "The version format is m[.n[.n[.n]]] where 0=<m<=127 and 0=<n<=255\nNote that in a 3-part or 4-part version, the last part will be used as the release number.";
    public static final Pattern PATTERN = Pattern.compile("[0-9]?[.0-9]*");

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        int size = fb.getDocument().getLength();
        text = text.toLowerCase();
        String newString = fb.getDocument().getText(0, offset) + text
                + (length >= size ? "" : fb.getDocument().getText(offset + length, fb.getDocument().getLength() - offset - length));
        if (!PATTERN.matcher(newString).matches())
            return;
        if (isValid(newString))
            super.replace(fb, offset, length, text, attrs);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        replace(fb, offset, 0, text, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        replace(fb, offset, length, "", null);
    }

    private static boolean isValid(String input) {
        if (input.contains("..") || input.equals("."))
            return false;
        if (input.isEmpty())
            return true;
        String[] parts = input.split("\\.");
        if (parts.length > MAX_PARTS)
            return false;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.equals("0") && part.startsWith("0"))
                return false;
            try {
                int v = Integer.parseInt(part);
                if ((i == 0 && v > 127) || v > 255)
                    return false;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        if (parts.length >= MAX_PARTS && input.endsWith("."))
            return false;
        return true;
    }
}
