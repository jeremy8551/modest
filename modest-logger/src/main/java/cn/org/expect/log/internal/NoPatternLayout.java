package cn.org.expect.log.internal;

import cn.org.expect.log.Layout;
import cn.org.expect.log.LogEvent;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 不带日志格式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/23
 */
public class NoPatternLayout implements Layout {

    public String format(LogEvent event) {
        String msg = event.getMessage();
        if (msg == null) {
            msg = "";
        }

        Throwable e = event.getThrowable();
        if (e == null) {
            StringBuilder buf = new StringBuilder(msg.length() + 2);
            buf.append(msg);
            buf.append(Settings.LINE_SEPARATOR);
            return buf.toString();
        } else {
            String error = StringUtils.toString(e);
            StringBuilder buf = new StringBuilder(msg.length() + error.length() + 4);
            buf.append(msg);
            buf.append(Settings.LINE_SEPARATOR);
            buf.append(error);
            buf.append(Settings.LINE_SEPARATOR);
            return buf.toString();
        }
    }

    public String getPattern() {
        return "";
    }
}
