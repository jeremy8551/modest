package cn.org.expect.maven.search;

public class PatternElement {

    private final MavenSearch search;
    private final String pattern;

    public PatternElement(MavenSearch search, String pattern) {
        this.search = search;
        this.pattern = pattern;
    }

    public MavenSearch getSearch() {
        return search;
    }

    public String getPattern() {
        return pattern;
    }
}
