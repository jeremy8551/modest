package cn.org.expect.log.field;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.org.expect.log.LogEvent;

/**
 * %d：输出日志时间点的日期或时间，也可以在其后指定格式，如：%d{yyyy/MM/dd HH:mm:ss,SSS}。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class DateField extends AbstractField {

    private final String pattern;

    private final SimpleDateFormat format;

    public DateField(String pattern) {
        this.pattern = pattern;
        this.format = new SimpleDateFormat(pattern);
    }

    public String format(LogEvent event) {
        return this.format(this.format.format(new Date()));
    }

    public String toString() {
        return DateField.class.getSimpleName() + "[" + this.pattern + "]";
    }
}
