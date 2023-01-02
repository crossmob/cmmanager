/*
 * (c) 2023 by Panayotis Katsaloulis
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.crossmobile.gui.parameters;

import com.panayotis.hrgui.*;
import org.crossmobile.gui.actives.*;
import org.crossmobile.utils.Param;
import org.crossmobile.utils.ParamList;

import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class FreeTextParameter extends ProjectParameter {

    private String value;
    private String tooltip;
    private HiResTextComponent text;
    private DocumentFilter filter;
    private final Type textType;
    private final boolean editable;

    public FreeTextParameter(ParamList list, Param key) {
        this(list, key, Type.TEXTFIELD);
    }

    public FreeTextParameter(ParamList list, Param key, Type textArea) {
        this(list, key, textArea, true);
    }

    public FreeTextParameter(ParamList list, Param key, boolean editable, String defaultText) {
        this(list, key, Type.TEXTFIELD, editable, defaultText);
    }

    public FreeTextParameter(ParamList list, Param key, Type textArea, boolean editable) {
        this(list, key, textArea, editable, null);
    }

    public FreeTextParameter(ParamList list, Param key, Type textType, boolean editable, String defaultText) {
        super(list, key);
        this.value = defaultText == null ? key.deflt : defaultText;
        try {
            value = list.get(key).trim();
        } catch (Exception ex) {
        }
        this.textType = textType;
        this.editable = editable;
    }

    public void setFilter(DocumentFilter filter) {
        this.filter = filter;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public String getValue() {
        return value;
    }

    /*
     * If this method is called in the constructor, no fireValueUpdated is
     * called
     */
    public void setValue(String newValue) {
        if (newValue == null)
            newValue = "";
        if (newValue.equals(value))
            return;
        value = newValue;
        if (text != null) {
            text.comp().setText(value);
            fireValueUpdated();
        }
    }

    @Override
    protected boolean isSingleLineVisual() {
        return textType != Type.TEXTAREA;
    }

    @Override
    protected HiResComponent initVisuals() {
        boolean asArea = textType == Type.TEXTAREA;
        HiResComponent data = asArea ? new HiResScrollPane((text = new ActiveTextArea(value, 30, 1)).comp()) : (text = new ActivePasswordField(value));
        switch (textType) {
            case TEXTAREA:
                data.comp().setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                data.comp().setPreferredSize(new Dimension(100, 100));
                break;
            case PASSWORDFIELD:
                data = new ActivePanel(new BorderLayout());
                ActiveToggleButton visible = new ActiveToggleButton("", new ActiveIcon("images/show"));
                int delta = (int) (6 * ScreenUtils.getScaleFactor());
                visible.setBorder(new EmptyBorder(delta, delta * 2 / 3, delta, delta * 2 / 3));
                char c = ((ActivePasswordField) text).getEchoChar();
                visible.addActionListener(evt -> ((HiResPasswordField) text.comp()).setEchoChar(visible.isSelected() ? '\0' : c));
                ((ActivePanel) data).add(text.comp(), BorderLayout.CENTER);
                ((ActivePanel) data).add(visible.comp(), BorderLayout.EAST);
                break;
            case TEXTFIELD:
            default:
                ((HiResPasswordField) text).setEchoChar('\0');
                text.comp().setPreferredSize(new Dimension(10, (int) (28 * ScreenUtils.getScaleFactor())));
                break;
        }
        if (filter != null) {
            Document doc = text.comp().getDocument();
            if (doc instanceof AbstractDocument)
                ((AbstractDocument) doc).setDocumentFilter(filter);
        }
        if (editable)
            text.comp().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    value = text.comp().getText();
                    fireValueUpdated();
                }
            });
        text.comp().setEditable(editable);
        text.comp().setToolTipText(tooltip);
        text.setFont(getTextFont(text.getFont()));
        return data;
    }

    protected HiResFont getTextFont(Font font) {
        return font instanceof HiResFont? (HiResFont) font :new HiResFont(font);
    }

    public enum Type {
        TEXTFIELD,
        PASSWORDFIELD,
        TEXTAREA
    }
}
