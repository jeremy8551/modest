package cn.org.expect.ioc;

import cn.org.expect.ModestRuntimeException;

public class IocException extends ModestRuntimeException {

    public IocException(String message, Object... args) {
        super(message, args);
    }
}
