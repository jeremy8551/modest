package cn.org.expect.log.apd.field;

import cn.org.expect.log.apd.LogEvent;
import cn.org.expect.util.StringUtils;

/**
 * %m: 输出代码中指定的日志信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/20
 */
public class MessageField extends AbstractField {

    public MessageField() {
    }

    public String format(LogEvent event) {
        String message = event.getMessage();
        if (message == null) {
            message = "";
        }

        Object[] args = event.getArgs();
        String text = StringUtils.replaceEmptyHolder(message, args);
        return this.format(text);
    }
}
