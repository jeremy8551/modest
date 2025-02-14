package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;

/**
 * %M：输出产生日志信息的方法名。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class MethodField extends AbstractField {

    public String format(LogEvent event) {
        return this.format(event.getStackTraceElement().getMethodName());
    }
}
