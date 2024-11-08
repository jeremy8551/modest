package cn.org.expect.maven.search;

public class PatternElement {
    private MavenSearch mavenFinder;
    private String pattern;

    public PatternElement(MavenSearch mavenFinder, String pattern) {
        this.mavenFinder = mavenFinder;
        this.pattern = pattern;
    }

    public MavenSearch getMavenFinder() {
        return mavenFinder;
    }

    public void setMavenFinder(MavenSearch mavenFinder) {
        this.mavenFinder = mavenFinder;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
