package cn.org.expect.time;

import cn.org.expect.ModestRuntimeException;

/**
 * 定时器组件异常
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-05-04
 */
public class TimerException extends ModestRuntimeException {

    public TimerException(String message, Object... args) {
        super(message, args);
    }
}
