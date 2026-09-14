package cn.org.expect.increment;

import cn.org.expect.exception.ModestException;

/**
 * 表示当前模块执行失败时抛出的异常
 */
public class IncrementException extends ModestException {

    /**
     * 初始化 IncrementException
     */
    public IncrementException(String message, Object... args) {
        super(message, args);
    }
}
