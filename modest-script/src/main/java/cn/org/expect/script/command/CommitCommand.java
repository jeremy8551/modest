package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.database.JdbcDao;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.internal.ScriptDataSource;
import cn.org.expect.util.ResourcesUtils;

/**
 * 提交数据库事务
 */
public class CommitCommand extends AbstractTraceCommand implements JumpCommandSupported, NohupCommandSupported {

    public CommitCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        ScriptDataSource dataSource = ScriptDataSource.get(context);
        JdbcDao dao = dataSource.getDao();
        if (dao.isConnected()) {
            dao.commit();
            return 0;
        } else {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message057", this.command));
            return UniversalScriptCommand.COMMAND_ERROR;
        }
    }

    public boolean enableNohup() {
        return true;
    }

    public boolean enableJump() {
        return true;
    }
}
