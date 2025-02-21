package indi.webapp.springboot.controler;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.org.expect.io.BufferedWriter;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.zh.ChineseRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/3
 */
@Controller
public class IncrementController {
    private final static Log log = LogFactory.getLog(IncrementController.class);

    @Autowired
    ScriptEngine engine;

    @RequestMapping("/inc")
    @ResponseBody
    public String inc() throws ScriptException, IOException {
        File[] files = this.createTempFiles(900000);
        File oldfile = files[0];
        File newfile = files[1];
        File resultfile = files[2];
        File logfile = new File(newfile.getParentFile(), FileUtils.changeFilenameExt(newfile.getName(), "log"));
        File incfile = new File(newfile.getParentFile(), "INC_" + newfile.getName());
        log.info(FileUtils.readline(newfile, CharsetUtils.get(), 1));

        // 当前目录
        log.info("新文件: {}", newfile);
        log.info("旧文件: {}", oldfile);
        log.info("增量文件: {}", incfile);
        log.info("日志文件: {}", logfile);
        log.info("正确文件: {}", resultfile);

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        try {
            engine.eval("echo 脚本引擎初始化完毕，执行增量剥离任务!");

            // 设置命令中使用的文件路径与索引字段位置信息
            engine.eval("set newfile='" + newfile.getAbsolutePath() + "'");
            engine.eval("set oldfile='" + oldfile.getAbsolutePath() + "'");
            engine.eval("set incfile='" + incfile.getAbsolutePath() + "'");
            engine.eval("set index='2'");
            engine.eval("set compare=");

            // 增量剥离命令
            String inccmd = "";
            inccmd += "extract increment compare ";
            inccmd += " $newfile of txt modified by index=$index keeptemp";
            inccmd += " and";
            inccmd += " $oldfile of txt modified by index=$index keeptemp";
            inccmd += " write new and upd and del into $incfile ";
            inccmd += " write log into " + logfile.getAbsolutePath();
            engine.eval(inccmd);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
            return "发生错误";
        } finally {
            engine.eval("exit 0");
        }

        // 判断剥离增量结果文件与正确文件是否相等
        long n = FileUtils.equalsIgnoreLineSeparator(incfile, CharsetUtils.get(), resultfile, CharsetUtils.get(), 0);
        if (n != 0) {
            String msg = "第 " + n + " 行不同!" + Settings.getLineSeparator();
            msg += FileUtils.readline(incfile, CharsetUtils.get(), n) + Settings.getLineSeparator(); // 读取文件中的指定行内容
            msg += FileUtils.readline(resultfile, CharsetUtils.get(), n); // 读取文件中的指定行内容
            log.error(msg);
            return "发生错误";
        }

        return "0";
    }

    public File[] createTempFiles(int rows) throws IOException {
        File tempDir = FileUtils.getTempDir("increment", "file");
        FileUtils.clearDirectory(tempDir);

        File newfile = FileUtils.allocate(tempDir, "NEWFILE.txt");
        FileUtils.assertCreateFile(newfile);

        File oldfile = FileUtils.allocate(tempDir, "OLDFILE.txt");
        FileUtils.assertCreateFile(oldfile);

        File resultfile = FileUtils.allocate(tempDir, "RESULT.txt");
        FileUtils.assertCreateFile(resultfile);

        Date start = Dates.parse("1960-01-01");
        Date end = new Date();
        String coldel = ",";

        // 写入旧文件
        BufferedWriter oldOut = new BufferedWriter(oldfile, CharsetUtils.get());
        try {
            StringBuilder buf = new StringBuilder(500);
            for (int i = 1; i <= rows; i++) {
                buf.setLength(0);
                buf.append("姓名").append(coldel);
                buf.append("Line").append(i).append(coldel);
                buf.append("身份证号").append(coldel);
                buf.append("手机号").append(coldel);
                buf.append(Dates.format19(start)).append(coldel);
                buf.append(Dates.format19(end)).append(coldel);
                for (int j = 1; j <= 20; j++) {
                    buf.append("Column").append(j).append(coldel);
                }
                oldOut.writeLine(buf.toString());
            }
        } finally {
            oldOut.close();
        }

        // 生成新文件
        BufferedWriter result = new BufferedWriter(resultfile, CharsetUtils.get());
        BufferedWriter newOut = new BufferedWriter(newfile, CharsetUtils.get());
        try {
            ChineseRandom cr = new ChineseRandom();
            StringBuilder buf = new StringBuilder(500);

            // 写入不变数据与变化数据，已删除数据
            for (int i = 1; i <= rows; i++) {
                boolean m = false;

                buf.setLength(0);
                buf.append("姓名").append(coldel);
                buf.append("Line").append(i).append(coldel);
                buf.append("身份证号").append(coldel);
                buf.append("手机号").append(coldel);

                if (i == 1000 || i == 2002 || i == 4003) {
                    buf.append(coldel);
                    m = true;
                } else {
                    buf.append(Dates.format19(start)).append(coldel);
                }
                buf.append(Dates.format19(end)).append(coldel);

                for (int j = 1; j <= 20; j++) {
                    buf.append("Column").append(j).append(coldel);
                }

                // 删除 2001 行与 4000 行内容
                if (i == 2001 || i == 4000) {
                    result.writeLine(buf.toString());
                    continue;
                }

                // 写入变化记录
                if (m) {
                    result.writeLine(buf.toString());
                }

                newOut.writeLine(buf.toString());
            }

            // 写入一行新增数据
            buf.setLength(0);
            buf.append(cr.nextName()).append(coldel);
            buf.append("M1").append(coldel);
            buf.append(cr.nextIdCard()).append(coldel);
            buf.append(cr.nextMobile()).append(coldel);
            buf.append(Dates.format19(cr.nextBirthday(start, end))).append(coldel);
            buf.append(Dates.format19(cr.nextBirthday(start, end))).append(coldel);
            for (int j = 1; j <= 20; j++) {
                buf.append("Column").append(j).append(coldel);
            }
            newOut.writeLine(buf.toString());
            result.writeLine(buf.toString());
        } finally {
            newOut.close();
            result.close();
        }

        return new File[]{oldfile, newfile, resultfile};
    }
}
