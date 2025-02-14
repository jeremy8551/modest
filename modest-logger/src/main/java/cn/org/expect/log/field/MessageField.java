package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;

/**
 * %m: 输出代码中指定的日志信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class MessageField extends AbstractField {

    public MessageField() {
    }

    public String format(LogEvent event) {
        String message = event.getMessage();
        if (message == null) {
            return this.format("");
        } else {
            return this.format(message);
        }
    }
}
