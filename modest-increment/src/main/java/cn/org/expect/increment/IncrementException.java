package cn.org.expect.increment;

import cn.org.expect.ModestException;

public class IncrementException extends ModestException {

    public IncrementException(String message, Object... args) {
        super(message, args);
    }
}
