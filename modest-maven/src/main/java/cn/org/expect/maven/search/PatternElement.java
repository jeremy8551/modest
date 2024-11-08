package cn.org.expect.maven.search;

public class PatternElement {
    
    private SearchOperation mavenFinder;
    private String pattern;

    public PatternElement(SearchOperation mavenFinder, String pattern) {
        this.mavenFinder = mavenFinder;
        this.pattern = pattern;
    }

    public SearchOperation getMavenFinder() {
        return mavenFinder;
    }

    public void setMavenFinder(SearchOperation mavenFinder) {
        this.mavenFinder = mavenFinder;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
