package cn.org.expect.modest.idea.plugin;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class MavenFinderThreadTest {

    @Test
    public void test() throws IOException {
        MavenFinderQuery finder = new MavenFinderQuery();
        finder.execute("icu.etl");
    }

    public static void main(String[] args) throws IOException {
        MavenFinderQuery finder = new MavenFinderQuery();
        finder.execute("icu.etl");
    }
}
