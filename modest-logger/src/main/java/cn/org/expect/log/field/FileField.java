package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;

/**
 * %F 输出源文件名
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class FileField extends MethodField {

    public String format(LogEvent event) {
        return this.format(event.getStackTraceElement().getFileName());
    }
}
