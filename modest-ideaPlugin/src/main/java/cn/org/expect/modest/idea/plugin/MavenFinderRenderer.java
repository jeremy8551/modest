package cn.org.expect.modest.idea.plugin;

import java.awt.*;
import javax.swing.*;

import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.util.TextWithIcon;

public class MavenFinderRenderer extends SearchEverywherePsiRenderer {

    public MavenFinderRenderer(MavenFinderContributor contributor) {
        super(contributor);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JListRenderer.INSTANCE.setList(list);
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    protected TextWithIcon getItemLocation(Object value) {
        if (value instanceof MavenFinderNavigationItem) {
            MavenFinderNavigationItem item = (MavenFinderNavigationItem) value;
            return new TextWithIcon(item.getPresentation().getItem().getRepository(), Icons.MAVEN_REPOSITORY_RIGHT);
        }
        return super.getItemLocation(value);
    }
}
