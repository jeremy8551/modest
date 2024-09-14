package cn.org.expect.script.command;

import java.util.Iterator;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.io.ScriptWriterFactory;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 带日志输出的脚本引擎命令模版类 <br>
 * 命令中需要指定日志输出时，可以在 {@linkplain AbstractTraceCommandCompiler} 类基础上实现功能 <br>
 * {@linkplain AbstractTraceCommandCompiler} 类提供了对命令中 {@literal >>} logfile 表达式的解析 <br>
 * {@linkplain UniversalScriptCommand#execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean)} 接口中 stdout 与 stderr 分别表示标准输出与错误信息输出 <br>
 * 如果命令中指定了输出日志则 stdout 表示向日志文件写入的标准信息输出流 <br>
 * 如果命令中指定了输出日志则 stderr 表示向日志文件写入的错误信息输出流 <br>
 *
 * @author jeremy8551@qq.com
 * @createtime 2020-10-14
 */
public abstract class AbstractTraceCommandCompiler extends AbstractCommandCompiler {

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        // command 1>> file 2>file 2>&1
        int index = analysis.indexOf(command, ">", 0, 2, 2);
        if (index != -1) {
            AbstractTraceCommandConfiguration config = this.parse(session, context, analysis, command, index);
            AbstractTraceCommand cmd = this.compile(session, context, parser, analysis, command, config.getCommand());
            cmd.setPrinter(config.getStdout(), config.getStderr(), config.isSame());
            return cmd;
        } else {
            return this.compile(session, context, parser, analysis, command, command);
        }
    }

    /**
     * 将脚本语句编译成命令对象
     *
     * @param session       用户会话信息
     * @param context       脚本引擎上下文信息
     * @param parser        语法分析器
     * @param analysis      语句分析器
     * @param orginalScript 原始脚本语句（带日志输出语句, 如: {@literal echo "" > ~/log.txt）}
     * @param command       脚本语句（不带日志输出语句，如: echo ""）
     * @return 脚本命令
     * @throws Exception 编译命令发生错误
     */
    public abstract AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception;

    /**
     * 解析输出日志语句 xxxx >> file.log
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param analysis 语句分析器
     * @param command  脚本命令
     * @param from     符号 ‘>’ 所在位置
     * @return 脚本命令配置信息
     * @throws Exception 解析语句发生错误
     */
    private AbstractTraceCommandConfiguration parse(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptAnalysis analysis, String command, int from) throws Exception {
        ScriptWriterFactory stdout = null;
        ScriptWriterFactory stderr = null;
        boolean same = false;

        String str = analysis.trim(command, 3, 1);
        int prefix = from - 1;
        if (prefix >= 0 && StringUtils.inArray(str.charAt(prefix), '1', '2')) {
            from = prefix;
        }

        String handle = str.substring(from);
        List<String> list = analysis.split(handle);
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            String cmd = it.next();

            if (cmd.startsWith(">")) { // stdout
                boolean append = cmd.startsWith(">>");
                int length = append ? 2 : 1;

                ScriptWriterFactory out = new ScriptWriterFactory(cmd.length() > length ? cmd.substring(length) : this.readLogfile(it, command), append);
                if (cmd.charAt(0) == '2') {
                    if (stderr != null) {
                        throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr063", command));
                    } else {
                        stderr = out;
                    }
                } else {
                    if (stdout != null) {
                        throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr063", command));
                    } else {
                        stdout = out;
                    }
                }
            } else if (cmd.equals("2>&1")) {
                stderr = stdout;
                same = true;
            } else if (cmd.startsWith("2>") || cmd.startsWith("1>")) {
                boolean append = cmd.length() >= 3 && cmd.charAt(2) == '>';
                int length = append ? 3 : 2;

                ScriptWriterFactory out = new ScriptWriterFactory(cmd.length() > length ? cmd.substring(length) : this.readLogfile(it, command), append);
                if (cmd.charAt(0) == '2') {
                    if (stderr != null) {
                        throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr063", command));
                    } else {
                        stderr = out;
                    }
                } else {
                    if (stdout != null) {
                        throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr063", command));
                    } else {
                        stdout = out;
                    }
                }
            } else {
                throw new UnsupportedOperationException(ResourcesUtils.getMessage("script.message.stderr063", cmd));
            }
        }

        return new AbstractTraceCommandConfiguration(stdout, stderr, same, command.substring(0, from));
    }

    /**
     * 读取下一个日志文件路径
     *
     * @param it      便利器
     * @param command 命令
     * @return 返回日志文件
     */
    private String readLogfile(Iterator<String> it, String command) {
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr063", command));
        }
    }

}
