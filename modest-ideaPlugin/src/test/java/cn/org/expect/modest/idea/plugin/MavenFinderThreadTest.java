package cn.org.expect.modest.idea.plugin;

import java.io.IOException;

import cn.org.expect.modest.idea.plugin.maven.MavenFinderQueryByCentral;
import org.junit.jupiter.api.Test;

public class MavenFinderThreadTest {

    @Test
    public void test() throws IOException {
        MavenFinderQueryByCentral finder = new MavenFinderQueryByCentral();
        finder.execute("icu.etl");
    }

    public static void main(String[] args) throws IOException {
        MavenFinderQueryByCentral finder = new MavenFinderQueryByCentral();
        finder.execute("icu.etl");
    }
}
