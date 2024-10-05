package cn.org.expect.script.spi;

import java.io.File;
import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.PatternConsoleAppender;
import cn.org.expect.log.apd.file.FileAppender;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import org.junit.Test;

public class ScriptEngineTest {

    /**
     * 使用JDK的脚本引擎接口测试
     */
    @Test
    public void test1() throws IOException, ScriptException {
        File logfile = FileUtils.createTempFile("UniversalScriptEngine.log");
        System.out.println("脚本引擎输出日志, file://" + logfile.getAbsolutePath());
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
        File logfile = FileUtils.createTempFile("UniversalScriptEngine.log");
        System.out.println("脚本引擎输出日志, file://" + logfile.getAbsolutePath());
        FileUtils.clearFile(logfile);

        LogContext logContext = LogFactory.getContext();
        logContext.updateLevel("*", LogLevel.DEBUG);
        logContext.removeAppender(PatternConsoleAppender.class);
        new FileAppender(logfile.getAbsolutePath(), Settings.getFileEncoding(), null, true).setup(logContext);

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("sql");
        engine.eval("help >> " + logfile.getAbsolutePath());
        engine.eval("exit 0 >> " + logfile.getAbsolutePath());

        System.out.println(logContext);
    }

    @Test
    public void test3() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        engine.eval("echo 'testvalue' > $temp/test.log; cat $temp/test.log; echo $temp ");
    }

    @Test
    public void test4() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("usl");

        File file = (File) engine.eval("cp classpath:/bhc_finish.del ${HOME}");
        System.out.println("复制后的文件: " + file.getAbsolutePath());
        FileUtils.assertFile(file);

        File file2 = (File) engine.eval("cp " + file.getAbsolutePath() + " $temp");
        System.out.println("复制后的文件: " + file2.getAbsolutePath());
        FileUtils.assertFile(file2);

        engine.eval("echo test > $temp/test1/test1.log");
        engine.eval("echo test > $temp/test1/test2/test2.log");

        File dir = (File) engine.eval("cp " + file2.getParent() + " ${HOME}");
        System.out.println("复制后的目录: " + dir);
        FileUtils.assertDirectory(dir);

        System.out.println("结果1: " + engine.eval("wc " + file.getAbsolutePath()) + "|");
        System.out.println("结果2: " + engine.eval("wc -l " + file.getAbsolutePath()));
    }

}