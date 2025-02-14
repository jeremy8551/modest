package cn.org.expect.javax.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.file.FileAppender;
import cn.org.expect.log.internal.PatternConsoleAppender;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ScriptEngineTest {

    /**
     * 使用JDK的脚本引擎接口测试
     */
    @Test
    public void test1() throws IOException, ScriptException {
        File logfile = FileUtils.createTempFile("ScriptHelp.md");
        FileUtils.clearFile(logfile);

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        engine.eval("help >> " + logfile.getAbsolutePath());
        engine.eval("exit 0 >> " + logfile.getAbsolutePath());
    }

    /**
     * 测试使用标准日志输出脚本引擎日志
     */
    @Test
    public void test2() throws IOException, ScriptException {
        LogContext logContext = LogFactory.getContext();
        logContext.reset();
        try {
            File logfile = FileUtils.createTempFile("ScriptHelp.log");
            FileUtils.clearFile(logfile);

            logContext.updateLevel("*", LogLevel.INFO);
            logContext.removeAppender(PatternConsoleAppender.class);

            FileUtils.deleteFile(logfile);
            new FileAppender(logfile.getAbsolutePath(), Settings.getFileEncoding(), null, true).setup(logContext);

            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByExtension("sql");
            engine.eval("help");
            engine.eval("exit 0");
        } finally {
            logContext.reset();
        }
    }

    @Test
    public void test3() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        String str = (String) engine.eval("echo 'testvalue' > $temp/test.log; cat $temp/test.log > /dev/null");
        Assert.assertEquals("testvalue", StringUtils.rtrimBlank(str));
    }

    @Test
    public void test4() throws ScriptException, IOException {
        LogFactory.getContext().updateLevel("*", LogLevel.OFF);
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByExtension("usl");

            File file = (File) engine.eval("cp classpath:/bhc_finish.del ${temp}");
            FileUtils.assertFile(file);

            File file2 = (File) engine.eval("mkdir $temp/bhc; cp " + file.getAbsolutePath() + " $temp/bhc");
            FileUtils.assertFile(file2);

            engine.eval("echo test > $temp/test1/test1.log");
            engine.eval("echo test > $temp/test1/test2/test2.log");

            String text = FileUtils.readline(file, CharsetName.UTF_8, 0);
            Object str1 = engine.eval("wc -w " + file.getAbsolutePath());
            Assert.assertEquals(text.length(), Integer.parseInt(StringUtils.trimBlank(str1)));

            List<String> list = StringUtils.splitLines(text, new ArrayList<String>());
            Object str2 = engine.eval("wc -l " + file.getAbsolutePath());
            Assert.assertEquals(list.size(), Integer.parseInt(StringUtils.trimBlank(str2)));
        } finally {
            LogFactory.getContext().reset();
        }
    }
}
