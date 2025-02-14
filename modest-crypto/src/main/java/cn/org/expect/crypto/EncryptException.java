package cn.org.expect.crypto;

import cn.org.expect.ModestRuntimeException;

/**
 * 加密工具发生错误
 */
public class EncryptException extends ModestRuntimeException {

    /**
     * 初始化异常信息
     *
     * @param message 异常信息，可以是国际化资源属性
     * @param args    国际化资源属性值中的参数，如果最后一个元素是异常，则表示异常原因
     */
    public EncryptException(String message, Object... args) {
        super(message, args);
    }
}
