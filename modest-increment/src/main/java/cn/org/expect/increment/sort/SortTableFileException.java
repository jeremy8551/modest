package cn.org.expect.increment.sort;

import cn.org.expect.ModestException;

/**
 * 表示当前模块执行失败时抛出的异常
 */
public class SortTableFileException extends ModestException {

    /**
     * 初始化 SortTableFileException
     */
    public SortTableFileException(String message, Object... args) {
        super(message, args);
    }
}
