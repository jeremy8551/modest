package cn.org.expect.intellij.idea.plugin.maven.navigation;

import java.awt.*;
import javax.swing.*;

import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

public class NavigationCell extends ColoredListCellRenderer<Object> {

    private final String text;

    private final Icon icon;

    private final int style;

    private final Color fgColor;

    public NavigationCell(String text, int style, Color fgColor) {
        super();
        this.text = text;
        this.icon = null;
        this.style = style;
        this.fgColor = fgColor;
    }

    protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
        this.append(this.text, new SimpleTextAttributes(this.style, this.fgColor));
        this.setIcon(this.icon == null ? IconUtil.getEmptyIcon(false) : this.icon);

        Color bgColor = UIUtil.getListBackground();
        this.setBackground(selected ? UIUtil.getListSelectionBackground(true) : bgColor);
    }
}
