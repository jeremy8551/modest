package cn.org.expect.script.io;

import java.io.Writer;
import java.text.Format;

import cn.org.expect.printer.StandardPrinter;
import cn.org.expect.script.UniversalScriptStdout;

/**
 * 标准信息输出接口的实现类
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptStdout extends StandardPrinter implements UniversalScriptStdout {

    /**
     * 初始化
     */
    public ScriptStdout() {
        super();
    }

    /**
     * 初始化
     *
     * @param writer 输出流
     * @param format 格式化工具
     */
    public ScriptStdout(Writer writer, Format format) {
        this();
        this.setWriter(writer);
        this.setFormatter(format);
    }
}
