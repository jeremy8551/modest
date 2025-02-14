package cn.org.expect.log;

import cn.org.expect.ModestRuntimeException;

/**
 * 日志错误
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/29
 */
public class LogException extends ModestRuntimeException {

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }
}
