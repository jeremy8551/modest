package indi.jeremy.bean;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class TestFile {
    private final static Log log = LogFactory.getLog(TestFile.class);

    public void hello() {
        log.info("hello world!");
    }
}
