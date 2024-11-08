package cn.org.expect.modest.idea.plugin;

import cn.org.expect.maven.search.MavenSearchUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MavenFinderPatternTest {

    @Test
    public void parseTest() {
        Assertions.assertEquals("", MavenSearchUtils.parse(""));
        Assertions.assertEquals("1", MavenSearchUtils.parse("1"));
        Assertions.assertEquals("ab", MavenSearchUtils.parse("ab"));
        Assertions.assertEquals("<artifactId>a", MavenSearchUtils.parse("<artifactId>a"));
        Assertions.assertEquals("a</artifactId>", MavenSearchUtils.parse("a</artifactId>"));
        Assertions.assertEquals("a", MavenSearchUtils.parse("<artifactId>a</artifactId>"));
        Assertions.assertEquals("org.test", MavenSearchUtils.parse("<artifactId>org.test</artifactId>"));
        Assertions.assertEquals("org.test", MavenSearchUtils.parse("<artifactId> org.test </artifactId>"));
        Assertions.assertEquals("org.test", MavenSearchUtils.parse("  <artifactId> org.test </artifactId>  "));
    }
}
