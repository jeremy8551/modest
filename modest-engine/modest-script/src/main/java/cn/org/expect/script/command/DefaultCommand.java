package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandRepository;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.DefaultCommandSupported;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 设置脚本引擎的默认命令 <br>
 * default sql;
 */
public class DefaultCommand extends AbstractTraceCommand implements UniversalScriptInputStream {

    /** 默认命令的语句信息 */
    private String script;

    public DefaultCommand(UniversalCommandCompiler compiler, String command, String script) {
        super(compiler, command);
        this.script = script;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlankline(this.script)) {
            this.script = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr014", this.command, "default", this.script));
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptCompiler compiler = session.getCompiler();
        UniversalCommandRepository repository = compiler.getRepository();

        UniversalScriptAnalysis analysis = session.getAnalysis();
        String script = StringUtils.trimBlank(analysis.replaceShellVariable(session, context, this.script, true, true, true, false));
        boolean print = session.isEchoEnable() || forceStdout;
        if (StringUtils.isBlank(script)) { // 打印默认命令
            UniversalCommandCompiler obj = repository.getDefault();
            if (obj != null && print) {
                stdout.println(ResourcesUtils.getMessage("script.message.stdout025", obj.getClass().getName()));
            }
            return 0;
        } else { // 设置默认命令
            UniversalCommandCompiler obj = repository.get(script);
            if (!(obj instanceof DefaultCommandSupported)) {
                throw new UnsupportedOperationException(StringUtils.toString(obj));
            }

            repository.setDefault(obj);
            if (obj != null && print) {
                stdout.println(ResourcesUtils.getMessage("script.message.stdout025", obj.getClass().getName()));
            }
            return 0;
        }
    }

    public void terminate() throws Exception {
    }

}
