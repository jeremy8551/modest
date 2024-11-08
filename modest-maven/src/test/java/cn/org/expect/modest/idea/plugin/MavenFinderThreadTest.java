package cn.org.expect.modest.idea.plugin;

import java.io.IOException;

import cn.org.expect.maven.repository.central.CentralRepository;
import org.junit.jupiter.api.Test;

public class MavenFinderThreadTest {

    @Test
    public void test() throws IOException {
        CentralRepository finder = new CentralRepository();
        finder.query("icu.etl", 1);
    }

    public static void main(String[] args) throws IOException {
        CentralRepository finder = new CentralRepository();
        finder.query("icu.etl", 1);
    }
}
