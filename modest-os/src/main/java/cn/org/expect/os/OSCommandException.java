package cn.org.expect.os;

public class OSCommandException extends OSException {

    public OSCommandException(String message, Object... args) {
        super(message, args);
    }
}
