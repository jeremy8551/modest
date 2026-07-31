package cn.org.expect.ioc;

import cn.org.expect.ModestRuntimeException;

/**
 * 表示当前模块执行失败时抛出的异常
 */
public class IocException extends ModestRuntimeException {

    /**
     * 初始化 IocException
     */
    public IocException(String message, Object... args) {
        super(message, args);
    }
}
