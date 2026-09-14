package cn.org.expect.database.export;

import cn.org.expect.exception.ModestRuntimeException;

/**
 * 表示当前模块执行失败时抛出的异常
 */
public class ExtractException extends ModestRuntimeException {

    /**
     * 初始化 ExtractException
     */
    public ExtractException(String message, Object... args) {
        super(message, args);
    }
}
