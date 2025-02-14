package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;

/**
 * 删除数据库编目信息 <br>
 * undeclare name catalog configuration <br>
 */
public class UndeclareCatalogCommand extends AbstractGlobalCommand {

    /** 数据库编目名 */
    private String name;

    public UndeclareCatalogCommand(UniversalCommandCompiler compiler, String command, String name, boolean global) {
        super(compiler, command);
        this.name = name;
        this.setGlobal(global);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        if (this.isGlobal()) {
            context.removeGlobalCatalog(this.name);
        }
        context.removeLocalCatalog(this.name);
        return 0;
    }
}
