package cn.org.expect.modest.idea.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.intellij.openapi.diagnostic.Logger;

public class MavenFinderResult {

    private static final Logger log = Logger.getInstance(MavenFinderResult.class);

    private final String pattern;

    private final List<MavenFinderItem> list;

    private final AtomicInteger count;

    public MavenFinderResult(String pattern) {
        this.list = new ArrayList<MavenFinderItem>();
        this.pattern = pattern;
        this.count = new AtomicInteger(0);
    }

    public MavenFinderResult canGetItems() {
        this.count.set(0);
        return this;
    }

    public MavenFinderResult add(MavenFinderItem item) {
        this.list.add(item);
        return this;
    }

    public MavenFinderResult addAll(List<MavenFinderItem> list) {
        this.list.addAll(list);
        return this;
    }

    public MavenFinderResult clear() {
        this.list.clear();
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public String[] getNames() {
        String[] array = new String[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            array[i] = "getNames1() " + this.list.get(i).getPresentableText();
        }
        return array;
    }

    public MavenFinderNavigationItem[] getItems() {
        if (this.count.getAndIncrement() >= 1) {
//            return new MavenFinderNavigationItem[0];
        }

        System.out.println("getItemsByName() " + pattern);
        MavenFinderNavigationItem[] array = new MavenFinderNavigationItem[this.list.size()];
        for (int i = 0; i < this.list.size(); i++) {
            MavenFinderItem item = this.list.get(i);
            array[i] = new MavenFinderNavigationItem(item);
        }
        return array;
    }
}
