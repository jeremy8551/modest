package cn.org.expect.modest.idea.plugin;

import cn.org.expect.maven.search.MavenUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MavenFinderPatternTest {

    @Test
    public void parseTest() {
        Assertions.assertEquals("", MavenUtils.parse(""));
        Assertions.assertEquals("1", MavenUtils.parse("1"));
        Assertions.assertEquals("ab", MavenUtils.parse("ab"));
        Assertions.assertEquals("<artifactId>a", MavenUtils.parse("<artifactId>a"));
        Assertions.assertEquals("a</artifactId>", MavenUtils.parse("a</artifactId>"));
        Assertions.assertEquals("a", MavenUtils.parse("<artifactId>a</artifactId>"));
        Assertions.assertEquals("org.test", MavenUtils.parse("<artifactId>org.test</artifactId>"));
        Assertions.assertEquals("org.test", MavenUtils.parse("<artifactId> org.test </artifactId>"));
        Assertions.assertEquals("org.test", MavenUtils.parse("  <artifactId> org.test </artifactId>  "));
    }
}
