package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.CommandExpression;
import cn.org.expect.os.linux.Linuxs;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = {"set", "var"}, keywords = {"set", "var"})
public class SetCommandCompiler extends AbstractGlobalCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        String line = in.previewline();
        int index = analysis.indexOf(line, "=", 0, 2, 2);
        if (index == -1) { // 没有赋值符号时，表示打印变量表达式（只有一个set关键字）
            return in.readSinglelineScript();
        } else if (analysis.indexOf(line, "select", index, 1, 0) != -1) { // 表示数据库查询赋值语句
            return in.readMultilineScript();
        } else {// 赋值语句
            return in.readSinglelineScript();
        }
    }

    public SetCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        CommandExpression expr = new CommandExpression(session.getAnalysis(), "set|var [-E|-e] {0|1}", command);
        int optionSize = expr.getOptionSize();
        int parameterSize = expr.getParameterSize();

        // set 打印所有变量
        if (optionSize == 0 && parameterSize == 0) {
            return new SetCommand(this, command, null, null, 2);
        }

        // 只有 -e 或 -E 选项: 检查命令返回值或不检查命令返回值
        if (parameterSize == 0) {
            return new SetCommand(this, command, null, null, expr.containsOption("-e") ? 4 : 5);
        }

        String str = expr.getParameter(); // name=value or name=SQL
        int index = str.indexOf('=');
        if (index == -1) {
            throw new UniversalScriptException("script.stderr.message109", command);
        }

        // name=value
        String name = StringUtils.trimBlank(str.substring(0, index)); // 截取变量名
        if (!context.getEngine().getChecker().isVariableName(name) || name.startsWith("$")) {
            throw new UniversalScriptException("script.stderr.message069", command, name);
        }

        // 变量值
        String value = StringUtils.trimBlank(Linuxs.removeShellNote(str.substring(index + 1), null));

        // set name select * from table 表示查询SQL语句
        if (analysis.indexOf(value, "select", 0, 1, 0) != -1) {
            return new SetCommand(this, command, name, value, 1);
        }

        // set name= 表示删除变量
        if (value.length() == 0) {
            return new SetCommand(this, command, name, value, 3);
        }

        // set name=value 变量赋值
        return new SetCommand(this, command, name, value, 0);
    }
}
