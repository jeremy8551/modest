package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;
import cn.org.expect.util.Settings;

/**
 * %n：输出一个回车换行符，Windows平台为”rn”，Unix平台为”n”。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class NewlineField extends AbstractField {

    public String format(LogEvent event) {
        return Settings.getLineSeparator();
    }
}
