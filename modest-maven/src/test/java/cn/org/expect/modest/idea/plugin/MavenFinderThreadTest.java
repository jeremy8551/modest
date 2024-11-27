package cn.org.expect.modest.idea.plugin;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.maven.repository.central.CentralRepository;
import cn.org.expect.maven.repository.central.CentralRepositoryDatabaseSerializer;
import cn.org.expect.test.ModestRunner;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class MavenFinderThreadTest {

    @EasyBean
    ThreadSource threadSource;

    @EasyBean
    CentralRepositoryDatabaseSerializer serializer;

    @Test
    public void test() {
        CentralRepository finder = new CentralRepository(this.threadSource, this.serializer);
        finder.query("icu.etl", 1);
    }
}
