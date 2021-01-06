/*
 * (c) 2021 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.elements;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Pattern;

public class IdDocumentFilter extends DocumentFilter {

    public static final String TOOLTIP = "The id format is text(.text)* where text is [a-z][a-z0-9\\-]*";
    public static final String TOOLTIP_SIMPLE = "The name format is [a-z][a-z0-9\\-]*";
    public static final Pattern PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9\\-]*(\\.[a-zA-Z][a-zA-Z0-9\\-]*)*");
    private static final Pattern PATTERN_SINGLE = Pattern.compile("[a-zA-Z][a-zA-Z0-9\\-]*");

    private final Pattern check;
    private final boolean simple;

    public IdDocumentFilter() {
        this(false);
    }

    public IdDocumentFilter(boolean asSimple) {
        check = asSimple ? PATTERN_SINGLE : PATTERN;
        this.simple = asSimple;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        int size = fb.getDocument().getLength();
        text = text.toLowerCase();
        String newString = fb.getDocument().getText(0, offset) + text
                + (length >= size ? "" : fb.getDocument().getText(offset + length, fb.getDocument().getLength() - offset - length));
        newString = ((!simple && newString.endsWith(".")) ? newString.substring(0, newString.length() - 1) : newString).toLowerCase();
        if (newString.isEmpty() || check.matcher(newString).matches())
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

}
