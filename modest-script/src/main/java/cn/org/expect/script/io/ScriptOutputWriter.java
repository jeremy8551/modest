package cn.org.expect.script.io;

import java.io.IOException;
import java.io.OutputStreamWriter;

import cn.org.expect.printer.OutputStreamPrinter;
import cn.org.expect.script.UniversalScriptStdout;

public class ScriptOutputWriter extends OutputStreamWriter {

    public ScriptOutputWriter(UniversalScriptStdout stdout, String charsetName) throws IOException {
        super(new OutputStreamPrinter(stdout, charsetName), charsetName);
    }
}
