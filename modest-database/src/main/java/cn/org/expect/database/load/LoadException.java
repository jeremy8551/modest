package cn.org.expect.database.load;

import cn.org.expect.exception.ModestRuntimeException;

/**
 * 表示当前模块执行失败时抛出的异常
 */
public class LoadException extends ModestRuntimeException {

    /**
     * 初始化 LoadException
     */
    public LoadException(String message, Object... args) {
        super(message, args);
    }
}
