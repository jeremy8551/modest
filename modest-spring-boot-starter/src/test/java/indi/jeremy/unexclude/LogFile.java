package indi.jeremy.unexclude;

import cn.org.expect.io.CommonTextTableFile;
import cn.org.expect.ioc.annotation.EasyBean;

@EasyBean("log")
public class LogFile extends CommonTextTableFile {

    public LogFile() {
        super();
    }
}
