package cn.org.expect.script.command;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.StringComparator;
import cn.org.expect.util.Terminator;

/**
 * 脚本引擎命令的模版类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2020-10-14
 */
public abstract class AbstractCommand extends Terminator implements UniversalScriptCommand {
    protected final static Log log = LogFactory.getLog(AbstractCommand.class);

    /** 命令的语句 */
    protected String command;

    /** 脚本命令对应的编译器 */
    protected UniversalCommandCompiler compiler;

    /**
     * 初始化
     *
     * @param compiler 脚本命令编译器
     * @param script   文本命令
     */
    public AbstractCommand(UniversalCommandCompiler compiler, String script) {
        this.compiler = compiler;
        this.command = script;
    }

    public abstract int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception;

    public UniversalCommandCompiler getCompiler() {
        return this.compiler;
    }

    public String getScript() {
        return this.command;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UniversalScriptCommand) || !obj.getClass().getName().equals(this.getClass().getName())) {
            return false;
        } else {
            String str1 = this.command;
            String str2 = ((UniversalScriptCommand) obj).getScript();
            return new StringComparator().compare(str1, str2) == 0;
        }
    }

    public String toString() {
        return this.command;
    }
}
