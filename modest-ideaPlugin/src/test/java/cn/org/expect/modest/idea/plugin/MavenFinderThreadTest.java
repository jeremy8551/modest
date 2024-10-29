package cn.org.expect.modest.idea.plugin;

import java.io.IOException;

import cn.org.expect.intellijidea.plugin.maven.impl.CentralMavenRepository;
import org.junit.jupiter.api.Test;

public class MavenFinderThreadTest {

    @Test
    public void test() throws IOException {
        CentralMavenRepository finder = new CentralMavenRepository();
        finder.query("icu.etl");
    }

    public static void main(String[] args) throws IOException {
        CentralMavenRepository finder = new CentralMavenRepository();
        finder.query("icu.etl");
    }
}
