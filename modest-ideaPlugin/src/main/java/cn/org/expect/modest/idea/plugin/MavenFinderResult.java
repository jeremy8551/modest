package cn.org.expect.modest.idea.plugin;

import java.util.List;

public class MavenFinderResult {

    private final String pattern;

    private final List<MavenFinderItem> list;

    public MavenFinderResult(String pattern, List<MavenFinderItem> list) {
        this.pattern = pattern;
        this.list = list;
    }

    public MavenFinderResult addAll(List<MavenFinderItem> list) {
        this.list.addAll(list);
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public String[] getNames() {
        String[] array = new String[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            MavenFinderItem item = this.list.get(i);
            array[i] = item.getPresentableText();
        }
        return array;
    }

    public MavenFinderNavigationItem[] getNavigationItems() {
        MavenFinderNavigationItem[] array = new MavenFinderNavigationItem[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            MavenFinderItem item = this.list.get(i);
            array[i] = new MavenFinderNavigationItem(item);
        }
        return array;
    }

    public List<MavenFinderItem> getItems() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }
}
