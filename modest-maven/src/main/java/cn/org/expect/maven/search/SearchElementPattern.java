package cn.org.expect.maven.search;

import cn.org.expect.util.Ensure;

public class SearchElementPattern implements SearchElement {

    private final MavenSearch search;
    
    private final String pattern;

    public SearchElementPattern(MavenSearch search, String pattern) {
        this.search = Ensure.notNull(search);
        this.pattern = Ensure.notNull(pattern);
    }

    public MavenSearch getSearch() {
        return search;
    }

    public String getPattern() {
        return pattern;
    }
}
