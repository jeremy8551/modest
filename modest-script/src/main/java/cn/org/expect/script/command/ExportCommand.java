package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.internal.FunctionSet;
import cn.org.expect.util.ResourcesUtils;

/**
 * 使变量或数据库编目信息可以被子脚本继承并访问 <br>
 * export set name=value <br>
 * export set count=select count(*) from table; <br>
 * export function name <br>
 *
 * @author jeremy8551@qq.com
 */
public class ExportCommand extends AbstractCommand {

    /** 全局命令 */
    private AbstractGlobalCommand subcommand;

    /** 用户自定义方法名 */
    private String functionName;

    /**
     * 赋值语句的初始化方法
     *
     * @param compiler
     * @param command
     * @param subcommand
     */
    public ExportCommand(UniversalCommandCompiler compiler, String command, AbstractGlobalCommand subcommand) {
        super(compiler, command);
        this.functionName = null;
        this.subcommand = subcommand;
    }

    /**
     * 用户自定义方法名的初始化方法
     *
     * @param compiler
     * @param command
     * @param name
     */
    public ExportCommand(UniversalCommandCompiler compiler, String command, String name) {
        super(compiler, command);
        this.functionName = name;
        this.subcommand = null;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (this.subcommand == null) { // 表示将局部用户自定义方法转为全局自定义方法
            UniversalScriptAnalysis analysis = session.getAnalysis();
            String name = analysis.replaceShellVariable(session, context, this.functionName, true, true, true, false);

            CommandList body = FunctionSet.get(context).get(name);
            if (body == null) {
                throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr034", name));
            } else {
                FunctionSet.get(context, true).add(body); // 添加到全局域
                FunctionSet.get(context, false).remove(body.getName()); // 从局部域中移除
                return 0;
            }
        } else { // 表示 set name=value 赋值表达式
            this.subcommand.setGlobal(true);
            return this.subcommand.execute(session, context, stdout, stderr, forceStdout);
        }
    }

    public void terminate() throws Exception {
    }

}
