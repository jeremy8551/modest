package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import javax.swing.*;

import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.openapi.Disposable;

public class MavenFinderRenderer extends SearchEverywherePsiRenderer {

    public MavenFinderRenderer(Disposable parent) {
        super(parent);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
