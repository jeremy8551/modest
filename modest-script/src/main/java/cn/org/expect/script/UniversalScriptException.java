package cn.org.expect.script;

import cn.org.expect.ModestRuntimeException;

/**
 * 脚本引擎异常信息
 *
 * @author jeremy8551@gmail.com
 */
public class UniversalScriptException extends ModestRuntimeException {

    /** 脚本命令所在行号 */
    private long lineNumber;

    /** 发生错误的脚本命令 */
    private String script;

    public UniversalScriptException(String message, Object... args) {
        super(message, args);
    }

    /**
     * 创建一个脚本异常信息
     *
     * @param script     发生异常错误的脚本语句
     * @param lineNumber 脚本命令发生异常时，脚本语句所在行号
     * @param e          异常信息
     */
    public UniversalScriptException(String script, long lineNumber, Throwable e) {
        this(script, e);
        this.lineNumber = lineNumber;
        this.script = script;
    }

    /**
     * 返回脚本命令发生异常时，脚本语句所在行号
     *
     * @return 行号，从1开始
     */
    public long getLineNumber() {
        return this.lineNumber;
    }

    /**
     * 返回发生异常的脚本语句
     *
     * @return 脚本语句
     */
    public String getScript() {
        return this.script;
    }
}
