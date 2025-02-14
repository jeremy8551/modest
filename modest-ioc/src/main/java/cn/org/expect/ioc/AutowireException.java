package cn.org.expect.ioc;

import cn.org.expect.ModestRuntimeException;

/**
 * 注入发生错误
 */
public class AutowireException extends ModestRuntimeException {

    public AutowireException(String message, Object... args) {
        super(message, args);
    }
}
