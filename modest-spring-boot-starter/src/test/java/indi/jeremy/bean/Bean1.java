package indi.jeremy.bean;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.springframework.stereotype.Component;

@Component("bean1")
public class Bean1 {
    private final static Log log = LogFactory.getLog(Bean1.class);

    public void hello() {
        log.info(this.getClass().getName());
    }
}
