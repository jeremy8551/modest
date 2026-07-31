package cn.org.expect.os;

/**
 * 表示当前模块执行失败时抛出的异常
 */
public class OSCommandException extends OSException {

    /**
     * 初始化 OSCommandException
     */
    public OSCommandException(String message, Object... args) {
        super(message, args);
    }
}
