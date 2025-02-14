package indi.jeremy.bean;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import org.springframework.stereotype.Component;

@Component("bean2")
public class Bean2 {
    private final static Log log = LogFactory.getLog(Bean2.class);

    Bean1 value;

    public Bean2(Bean1 val) {
        value = val;
    }

    public void hello() {
        log.info("{} {}", this.getClass().getName(), value);
    }
}
