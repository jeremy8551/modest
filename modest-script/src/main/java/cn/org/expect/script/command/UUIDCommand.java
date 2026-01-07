package cn.org.expect.script.command;

import java.io.File;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.StringUtils;

/**
 * 生成一个32位的唯一字符串
 */
public class UUIDCommand extends AbstractTraceCommand implements NohupCommandSupported {

    public UUIDCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        if (session.isEchoEnable() || forceStdout) {
            String uuid = StringUtils.toRandomUUID();
            stdout.println(uuid);

            session.setValue(uuid);
        }
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
