package cn.org.expect.modest.idea.plugin;

import java.util.concurrent.atomic.AtomicLong;
import javax.swing.*;

import cn.org.expect.util.Ensure;
import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;

public class MavenFinderItemPresentation implements ItemPresentation {

    private final MavenFinderItem item;

    private final static AtomicLong NUMBER = new AtomicLong(0);

    private long id;

    public MavenFinderItemPresentation(MavenFinderItem item) {
        this.id = NUMBER.incrementAndGet();
        this.item = Ensure.notNull(item);
    }

    public MavenFinderItem getItem() {
        return item;
    }

    public String getPresentableText() {
        return this.item.getPresentableText();
    }

    public String getLocationString() {
        return this.item.getLocationString();
    }

    public Icon getIcon(boolean unused) {
        return AllIcons.Actions.Find;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
