package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import javax.swing.*;

import com.intellij.ide.actions.SearchEverywherePsiRenderer;

public class MavenFinderRenderer extends SearchEverywherePsiRenderer {

    public MavenFinderRenderer(MavenFinderContributor contributor) {
        super(contributor);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        System.out.println("getListCellRendererComponent()");
        JListRenderer.setList(list);
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
