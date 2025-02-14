package cn.org.expect.database.export;

import cn.org.expect.ModestRuntimeException;

public class ExtractException extends ModestRuntimeException {

    public ExtractException(String message, Object... args) {
        super(message, args);
    }
}
