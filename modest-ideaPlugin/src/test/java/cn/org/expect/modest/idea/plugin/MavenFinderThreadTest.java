package cn.org.expect.modest.idea.plugin;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class MavenFinderThreadTest {

    @Test
    public void test() throws IOException {
        CentralMavenFinderQuery finder = new CentralMavenFinderQuery();
        finder.execute("icu.etl");
    }

    public static void main(String[] args) throws IOException {
        CentralMavenFinderQuery finder = new CentralMavenFinderQuery();
        finder.execute("icu.etl");
    }
}
