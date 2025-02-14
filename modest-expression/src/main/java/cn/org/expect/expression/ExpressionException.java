package cn.org.expect.expression;

import cn.org.expect.ModestRuntimeException;

/**
 * 文本表达式异常错误
 *
 * @author jeremy8551@gmail.com
 */
public class ExpressionException extends ModestRuntimeException {

    public ExpressionException(String message, Object... args) {
        super(message, args);
    }
}
