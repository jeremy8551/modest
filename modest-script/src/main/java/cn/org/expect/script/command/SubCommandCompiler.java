package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;

@EasyCommandCompiler(name = "*")
public class SubCommandCompiler extends AbstractCommandCompiler {

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String line) {
        return line.charAt(0) == '`' ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSymmetryScript("`");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws IOException {
        return new SubCommand(this, command, this.trim(command));
    }

    /**
     * 将参数obj转为字符串并删除字符串左右端的空白字符（半角空格,全角空格,\r,\n,\t等）
     *
     * @param str 字符串
     * @return 字符串
     */
    private String trim(String str) {
        int sp = 0, len = str.length(), ep = len - 1;
        while (sp < len && Character.isWhitespace(str.charAt(sp))) {
            sp++;
        }
        if (str.charAt(sp) == '`') {
            sp++;
        }

        while (sp <= ep && ep >= 0 && Character.isWhitespace(str.charAt(ep))) {
            ep--;
        }
        return str.substring(sp, ep);
    }
}
