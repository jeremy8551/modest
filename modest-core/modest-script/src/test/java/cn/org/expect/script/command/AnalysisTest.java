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
        context.setWriter(IO.getFileWriter(logfile, Settings.getFileEncoding(), false));
        UniversalScriptStdout p = context.getStdout();
        try {
            new CallProcudureCommandCompiler().usage(context, p);
            new EchoCommandCompiler().usage(context, p);
            new SetCommandCompiler().usage(context, p);
            new ExportCommandCompiler().usage(context, p);
            new ExecuteFileCommandCompiler().usage(context, p);
            new CommitCommandCompiler().usage(context, p);
            new RollbackCommandCompiler().usage(context, p);
            new SSH2CommandCompiler().usage(context, p);
            new JavaCommandCompiler().usage(context, p);
            new StepCommandCompiler().usage(context, p);
            new JumpCommandCompiler().usage(context, p);
            new SQLCommandCompiler().usage(context, p);
            new QuietCommandCompiler().usage(context, p);
            new WaitCommandCompiler().usage(context, p);
            new FunctionCommandCompiler().usage(context, p);
            new DeclareHandlerCommandCompiler().usage(context, p);
            new UndeclareHandlerCommandCompiler().usage(context, p);
            new DeclareCursorCommandCompiler().usage(context, p);
            new DeclareStatementCommandCompiler().usage(context, p);
            new ExecuteFunctionCommandCompiler().usage(context, p);
            new TerminateCommandCompiler().usage(context, p);
            new IfCommandCompiler().usage(context, p);
            new WhileCommandCompiler().usage(context, p);
        } finally {
            IO.close(engine);
        }
    }
}
