package cn.org.expect.modest.idea.plugin;

import javax.swing.*;

import cn.org.expect.util.Ensure;
import com.intellij.navigation.ItemPresentation;

public class MavenFinderItemPresentation implements ItemPresentation {

    private final MavenFinderItem item;

    public MavenFinderItemPresentation(MavenFinderItem item) {
        this.item = Ensure.notNull(item);
    }

    public MavenFinderItem getItem() {
        return item;
    }

    /**
     * 在搜索结果中，显示为项目名称
     *
     * @return 文本
     */
    public String getPresentableText() {
        return this.item.getPresentableText();
    }

    /**
     * 在搜索结果中，显示为项目名称旁边的灰色文本
     *
     * @return 文本
     */
    public String getLocationString() {
        return this.item.getLocationString();
    }

    public Icon getIcon(boolean unused) {
        return Icons.MAVEN_REPOSITORY_LEFT;
    }

    public boolean equals(Object o) {
        return false;
    }
}
