package cn.org.expect.maven;

public class MavenRuntimeException extends RuntimeException {

    public MavenRuntimeException(String message, Object... array) {
        super(MavenMessage.toString(message, array));
    }

    public MavenRuntimeException(Throwable cause, String message, Object... array) {
        super(MavenMessage.toString(message, array), cause);
    }
}
