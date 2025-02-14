package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;

/**
 * %L 输出代码中的行号。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class LinenoField extends MethodField {

    public String format(LogEvent event) {
        return this.format(String.valueOf(event.getStackTraceElement().getLineNumber()));
    }
}
