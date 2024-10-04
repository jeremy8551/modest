package cn.org.expect.log.apd;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 不带日志格式
 *
 * @author jeremy8551@qq.com
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
            buf.append(FileUtils.lineSeparator);
            return buf.toString();
        } else {
            String error = StringUtils.toString(e);
            StringBuilder buf = new StringBuilder(msg.length() + error.length() + 4);
            buf.append(msg);
            buf.append(FileUtils.lineSeparator);
            buf.append(error);
            buf.append(FileUtils.lineSeparator);
            return buf.toString();
        }
    }

    public String getPattern() {
        return "";
    }
}
