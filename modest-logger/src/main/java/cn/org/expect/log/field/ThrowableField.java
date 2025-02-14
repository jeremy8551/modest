package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;
import cn.org.expect.util.ErrorUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * %ex: 同 %throwable 都是输出异常信息。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class ThrowableField extends AbstractField {

    public String format(LogEvent event) {
        Throwable e = event.getThrowable();
        if (e == null) {
            return "";
        } else {
            String exception = ErrorUtils.toString(e);
            if (!StringUtils.startWithLineSeparator(exception)) {
                exception = Settings.LINE_SEPARATOR + exception;
            }
            return this.format(exception);
        }
    }
}
