package cn.org.expect.database;

import cn.org.expect.ModestRuntimeException;

/**
 * 数据库错误
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-03-06
 */
public class DatabaseException extends ModestRuntimeException {

    public DatabaseException(String message, Object... args) {
        super(message, args);
    }
}
