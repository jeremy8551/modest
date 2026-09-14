package cn.org.expect.script.io;

import java.io.IOException;
import java.io.OutputStreamWriter;

import cn.org.expect.printer.OutputStreamPrinter;
import cn.org.expect.script.UniversalScriptStdout;

/**
 * 将处理结果写入目标输出
 */
public class ScriptOutputWriter extends OutputStreamWriter {

    /**
     * 初始化 ScriptOutputWriter
     */
    public ScriptOutputWriter(UniversalScriptStdout stdout, String charsetName) throws IOException {
        super(new OutputStreamPrinter(stdout, charsetName), charsetName);
    }
}
