package cn.org.expect.modest.idea.plugin;

import cn.org.expect.maven.search.ArtifactSearchUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MavenFinderPatternTest {

    @Test
    public void parseTest() {
        Assertions.assertEquals("", ArtifactSearchUtils.parse(""));
        Assertions.assertEquals("1", ArtifactSearchUtils.parse("1"));
        Assertions.assertEquals("ab", ArtifactSearchUtils.parse("ab"));
        Assertions.assertEquals("<artifactId>a", ArtifactSearchUtils.parse("<artifactId>a"));
        Assertions.assertEquals("a</artifactId>", ArtifactSearchUtils.parse("a</artifactId>"));
        Assertions.assertEquals("a", ArtifactSearchUtils.parse("<artifactId>a</artifactId>"));
        Assertions.assertEquals("org.test", ArtifactSearchUtils.parse("<artifactId>org.test</artifactId>"));
        Assertions.assertEquals("org.test", ArtifactSearchUtils.parse("<artifactId> org.test </artifactId>"));
        Assertions.assertEquals("org.test", ArtifactSearchUtils.parse("  <artifactId> org.test </artifactId>  "));
    }
}
