package cn.org.expect;

import cn.org.expect.util.ErrorUtils;
import cn.org.expect.util.ResourcesUtils;

/**
 * 运行时异常信息
 */
public class ModestRuntimeException extends RuntimeException {

    /**
     * 初始化异常信息
     *
     * @param message 异常信息，可以是国际化资源属性
     * @param args    国际化资源属性值中的参数，如果最后一个元素是异常，则表示异常原因
     */
    public ModestRuntimeException(String message, Object... args) {
        super(ResourcesUtils.getMessage(message, args), ErrorUtils.getThrowable(args));
    }
}
