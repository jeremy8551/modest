package cn.org.expect.maven.intellij.idea.navigation;

import java.awt.*;
import javax.swing.*;

import com.intellij.ide.util.PSIRenderingUtils;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

public class NavigationCell extends ColoredListCellRenderer<Object> {

    private String text;

    private Icon icon;

    private int style;

    private Color fgColor;

    public NavigationCell(String text, int style, Color fgColor) {
        this.text = text;
        this.icon = null;
        this.style = style;
        this.fgColor = fgColor;
    }

    public NavigationCell(String text, int style, Color fgColor, Icon icon) {
        this.text = text;
        this.icon = icon;
        this.style = style;
        this.fgColor = fgColor;
    }

    protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
        SimpleTextAttributes simple = null;

        TextAttributes attributes = PSIRenderingUtils.getNavigationItemAttributesStatic(value);
        if (attributes != null) {
            simple = SimpleTextAttributes.fromTextAttributes(attributes);
        }

        if (simple == null) {
        }

        // this.append(locationString, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
        this.append(this.text, new SimpleTextAttributes(this.style, this.fgColor));
        this.setIcon(this.icon == null ? IconUtil.getEmptyIcon(false) : this.icon);

        Color bgColor = UIUtil.getListBackground();
        this.setBackground(selected ? UIUtil.getListSelectionBackground(true) : bgColor);
    }
}
