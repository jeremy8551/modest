package cn.org.expect.intellijidea.plugin.maven.search;

import cn.org.expect.intellijidea.plugin.maven.MavenFinder;

public class PatternElement {
    private MavenFinder mavenFinder;
    private String pattern;

    public PatternElement(MavenFinder mavenFinder, String pattern) {
        this.mavenFinder = mavenFinder;
        this.pattern = pattern;
    }

    public MavenFinder getMavenFinder() {
        return mavenFinder;
    }

    public void setMavenFinder(MavenFinder mavenFinder) {
        this.mavenFinder = mavenFinder;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
