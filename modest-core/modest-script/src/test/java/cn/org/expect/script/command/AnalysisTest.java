package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class AnalysisTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void testReadBreak() throws IOException {
        File logfile = FileUtils.createTempFile("testReadBreak.log");
        FileUtils.delete(logfile);
        System.out.println("日志文件: file://" + logfile.getAbsolutePath());

        UniversalScriptEngineFactory manager = new UniversalScriptEngineFactory(this.context);
        UniversalScriptEngine engine = manager.getScriptEngine();
        UniversalScriptContext context = engine.getContext();
        context.getEngine().setWriter(IO.getFileWriter(logfile, Settings.getFileEncoding(), false));
        UniversalScriptStdout stdout = context.getEngine().getStdout();
        try {
            new CallProcudureCommandCompiler().usage(context, stdout);
            new EchoCommandCompiler().usage(context, stdout);
            new SetCommandCompiler().usage(context, stdout);
            new ExportCommandCompiler().usage(context, stdout);
            new ExecuteFileCommandCompiler().usage(context, stdout);
            new CommitCommandCompiler().usage(context, stdout);
            new RollbackCommandCompiler().usage(context, stdout);
            new SSH2CommandCompiler().usage(context, stdout);
            new JavaCommandCompiler().usage(context, stdout);
            new StepCommandCompiler().usage(context, stdout);
            new JumpCommandCompiler().usage(context, stdout);
            new SQLCommandCompiler().usage(context, stdout);
            new QuietCommandCompiler().usage(context, stdout);
            new WaitCommandCompiler().usage(context, stdout);
            new FunctionCommandCompiler().usage(context, stdout);
            new DeclareHandlerCommandCompiler().usage(context, stdout);
            new UndeclareHandlerCommandCompiler().usage(context, stdout);
            new DeclareCursorCommandCompiler().usage(context, stdout);
            new DeclareStatementCommandCompiler().usage(context, stdout);
            new ExecuteFunctionCommandCompiler().usage(context, stdout);
            new TerminateCommandCompiler().usage(context, stdout);
            new IfCommandCompiler().usage(context, stdout);
            new WhileCommandCompiler().usage(context, stdout);
        } finally {
            IO.close(engine);
        }
    }
}
