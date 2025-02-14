package cn.org.expect.database.load;

import cn.org.expect.ModestRuntimeException;

public class LoadException extends ModestRuntimeException {

    public LoadException(String message, Object... args) {
        super(message, args);
    }
}
