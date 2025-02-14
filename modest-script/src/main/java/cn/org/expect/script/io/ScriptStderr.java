package cn.org.expect.script.io;

import java.io.Writer;
import java.text.Format;

import cn.org.expect.script.UniversalScriptStderr;

/**
 * 错误信息输出接口的实现类
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptStderr extends ScriptStdout implements UniversalScriptStderr {

    /**
     * 初始化
     */
    public ScriptStderr() {
        super();
        this.useError(true);
    }

    /**
     * 初始化
     *
     * @param writer 输出流
     * @param format 格式化工具
     */
    public ScriptStderr(Writer writer, Format format) {
        super(writer, format);
        this.useError(true);
    }
}
