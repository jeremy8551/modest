package cn.org.expect.modest.idea.plugin;

import cn.org.expect.intellijidea.plugin.maven.MavenFinderPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MavenFinderPatternTest {

    @Test
    public void parseTest() {
        Assertions.assertEquals("", MavenFinderPattern.parse(""));
        Assertions.assertEquals("1", MavenFinderPattern.parse("1"));
        Assertions.assertEquals("ab", MavenFinderPattern.parse("ab"));
        Assertions.assertEquals("a", MavenFinderPattern.parse("<artifactId>a"));
        Assertions.assertEquals("a", MavenFinderPattern.parse("a</artifactId>"));
        Assertions.assertEquals("a", MavenFinderPattern.parse("<artifactId>a</artifactId>"));
        Assertions.assertEquals("org.test", MavenFinderPattern.parse("<artifactId>org.test</artifactId>"));
        Assertions.assertEquals("org.test", MavenFinderPattern.parse("<artifactId> org.test </artifactId>"));
        Assertions.assertEquals("org.test", MavenFinderPattern.parse("  <artifactId> org.test </artifactId>  "));
    }
}
