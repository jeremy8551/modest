package cn.org.expect.modest.idea.plugin;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.maven.repository.central.CentralMavenRepository;
import cn.org.expect.test.ModestRunner;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class MavenFinderThreadTest {

    @EasyBean
    EasyContext context;

    @Test
    public void test() throws Exception {
        CentralMavenRepository finder = new CentralMavenRepository(this.context);
        finder.query("icu.etl", 1);
    }
}
