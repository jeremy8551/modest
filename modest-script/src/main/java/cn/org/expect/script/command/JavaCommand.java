package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.JumpCommandSupported;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 执行 JAVA 类命令 <br>
 * 格式: java 类全名 参数1 参数2 <br>
 * java 类需要实现 {@linkplain AbstractJavaCommand} 抽象类
 */
public class JavaCommand extends AbstractTraceCommand implements UniversalScriptInputStream, JumpCommandSupported, NohupCommandSupported {

    /** JAVA 对象 */
    private AbstractJavaCommand obj;

    /** JAVA 类名 */
    private String className;

    /** JAVA 对象的参数 */
    private List<String> args;

    public JavaCommand(UniversalCommandCompiler compiler, String command, String className, List<String> args) {
        super(compiler, command);
        this.className = className;
        this.args = args;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (this.args != null && !this.args.isEmpty()) {
            throw new UniversalScriptException("script.stderr.message012", this.command, "java " + this.className, this.args);
        }
        analysis.split(StringUtils.trimBlank(IO.read(in, new StringBuilder())), this.args);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        StringBuilder buf = new StringBuilder(this.command.length() + 30);
        buf.append("java ");
        buf.append(this.className);

        UniversalScriptAnalysis analysis = session.getAnalysis();
        String[] array = new String[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            array[i] = analysis.unQuotation(analysis.replaceShellVariable(session, context, this.args.get(i), true, true));

            String value = array[i];
            if (StringUtils.indexOfBlank(value, 0, value.length() - 1) != -1) {
                buf.append(" \"").append(value).append('\"');
            } else {
                buf.append(' ').append(value);
            }
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(buf);
        }

        // 初始化
        Class<? extends AbstractJavaCommand> type = context.getContainer().forName(this.className);
        if (type == null) {
            throw new UniversalScriptException("script.stderr.message073", this.command, className, AbstractJavaCommand.class.getName());
        }

        this.obj = context.getContainer().newInstance(type);
        session.putValue(this.obj);

        // 执行命令
        return this.obj.execute(session, context, stdout, stderr, array);
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.obj != null) {
            this.obj.terminate();
        }
    }

    public boolean enableNohup() {
        return this.obj == null ? false : (this.obj instanceof NohupCommandSupported) && ((NohupCommandSupported) this.obj).enableNohup();
    }

    public boolean enableJump() {
        return this.obj == null ? false : (this.obj instanceof JumpCommandSupported) && ((JumpCommandSupported) this.obj).enableJump();
    }
}
