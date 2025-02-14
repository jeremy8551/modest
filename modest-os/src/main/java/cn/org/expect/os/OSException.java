package cn.org.expect.os;

import cn.org.expect.ModestRuntimeException;

/**
 * 操作系统异常
 */
public class OSException extends ModestRuntimeException {
    private final static long serialVersionUID = 1L;

    public OSException(String message, Object... args) {
        super(message, args);
    }
}
