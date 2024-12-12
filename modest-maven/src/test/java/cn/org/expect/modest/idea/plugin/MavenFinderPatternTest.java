package cn.org.expect.modest.idea.plugin;

import cn.org.expect.maven.search.ArtifactSearchPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MavenFinderPatternTest {

    @Test
    public void parseTest() {
        ArtifactSearchPattern pattern = new ArtifactSearchPattern();
        Assertions.assertEquals("", pattern.parse(""));
        Assertions.assertEquals("1", pattern.parse("1"));
        Assertions.assertEquals("ab", pattern.parse("ab"));
        Assertions.assertEquals("<artifactId>a", pattern.parse("<artifactId>a"));
        Assertions.assertEquals("a</artifactId>", pattern.parse("a</artifactId>"));
        Assertions.assertEquals("a", pattern.parse("<artifactId>a</artifactId>"));
        Assertions.assertEquals("org.test", pattern.parse("<artifactId>org.test</artifactId>"));
        Assertions.assertEquals("org.test", pattern.parse("<artifactId> org.test </artifactId>"));
        Assertions.assertEquals("org.test", pattern.parse("  <artifactId> org.test </artifactId>  "));
    }
}
