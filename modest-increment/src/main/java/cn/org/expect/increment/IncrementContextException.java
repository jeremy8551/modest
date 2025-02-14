package cn.org.expect.increment;

import cn.org.expect.ModestRuntimeException;

/**
 * 增量剥离上下文信息错误
 */
public class IncrementContextException extends ModestRuntimeException {

    public IncrementContextException(String message, Object... args) {
        super(message, args);
    }
}
