package cn.org.expect.script.io;

import java.io.Writer;
import java.text.Format;

import cn.org.expect.printer.StandardPrinter;
import cn.org.expect.script.UniversalScriptSteper;

/**
 * 步骤信息输出的接口实现类
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptSteper extends StandardPrinter implements UniversalScriptSteper {

    /**
     * 初始化
     */
    public ScriptSteper() {
        super();
    }

    /**
     * 初始化
     *
     * @param writer 输出流
     * @param format 格式化工具
     */
    public ScriptSteper(Writer writer, Format format) {
        this();
        this.setWriter(writer);
        this.setFormatter(format);
    }
}
