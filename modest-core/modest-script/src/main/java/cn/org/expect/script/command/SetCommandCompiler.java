package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.annotation.ScriptCommand;
import cn.org.expect.expression.CommandExpression;
import cn.org.expect.os.linux.Linuxs;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.script.internal.ScriptUsage;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

@ScriptCommand(name = {"set", "var"}, keywords = {"set", "var"})
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
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr147", command));
        }

        // name=value
        String name = StringUtils.trimBlank(str.substring(0, index)); // 截取变量名
        if (!context.getEngine().getChecker().isVariableName(name) || name.startsWith("$")) {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr088", command, name));
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

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) {
        out.println(new ScriptUsage(this.getClass() //
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_PWD, 15, ' ') // 0
                , UniversalScriptVariable.SESSION_VARNAME_PWD // 1
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_SCRIPTNAME, 15, ' ') // 2
                , UniversalScriptVariable.SESSION_VARNAME_SCRIPTNAME // 3
                , StringUtils.left(UniversalScriptVariable.VARNAME_CHARSET, 15, ' ') // 4
                , StringUtils.CHARSET // 5
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_LINESEPARATOR, 15, ' ')// 6
                , StringUtils.escapeLineSeparator(FileUtils.lineSeparator) // 7
                , StringUtils.left(UniversalScriptVariable.VARNAME_EXCEPTION, 15, ' ')// 8
                , UniversalScriptVariable.VARNAME_EXCEPTION // 9
                , StringUtils.left(UniversalScriptVariable.VARNAME_ERRORSCRIPT, 15, ' ')// 10
                , UniversalScriptVariable.VARNAME_ERRORSCRIPT // 11
                , StringUtils.left(UniversalScriptVariable.VARNAME_ERRORCODE, 15, ' ')// 12
                , UniversalScriptVariable.VARNAME_ERRORCODE // 13
                , StringUtils.left(UniversalScriptVariable.VARNAME_SQLSTATE, 15, ' ') // 14
                , UniversalScriptVariable.VARNAME_SQLSTATE // 15
                , StringUtils.left(UniversalScriptVariable.VARNAME_EXITCODE, 15, ' ') // 16
                , StringUtils.left(UniversalScriptVariable.VARNAME_UPDATEROWS, 15, ' ') // 17
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_JUMP, 15, ' ') // 18
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_STEP, 15, ' ') // 19
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_TEMP, 15, ' ') // 20
                , StringUtils.left(UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE, 15, ' ') // 21
                , StringUtils.left(UniversalScriptVariable.VARNAME_CATALOG, 15, ' ') // 22
        ));
    }
}
