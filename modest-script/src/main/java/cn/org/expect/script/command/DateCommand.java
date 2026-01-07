package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptExpression;
import cn.org.expect.script.UniversalScriptInputStream;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.Dates;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 日期命令 <br>
 * date -d [日期字符串] [日期格式表达式] [ +|- 数字 day|month|year] <br>
 * date yyyyMMdd +1day, 当前日期 + 1 天 <br>
 * date yyyyMMdd + 1day <br>
 * date yyyyMMdd + 1 day <br>
 */
public class DateCommand extends AbstractTraceCommand implements UniversalScriptInputStream, NohupCommandSupported {

    /** 日期运算表达式: +1day -2hour */
    private String formula;

    /** 日期表达式: 20200101 or null */
    private String dateStr;

    /** 输出日期格式: yyyyMMdd hh:mm:ss */
    private String pattern;

    public DateCommand(UniversalCommandCompiler compiler, String command, String formula, String dateStr, String pattern) {
        super(compiler, command);
        this.formula = formula;
        this.dateStr = dateStr;
        this.pattern = pattern;
    }

    public void read(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, Reader in) throws IOException {
        if (analysis.isBlank(this.dateStr)) {
            this.dateStr = StringUtils.trimBlank(IO.read(in, new StringBuilder()));
        } else {
            throw new UniversalScriptException("script.stderr.message012", this.command, "date", this.dateStr);
        }
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        Date date = this.dateStr != null ? Dates.parse(analysis.replaceShellVariable(session, context, analysis.unQuotation(this.dateStr), true, !analysis.containsQuotation(this.dateStr))) : new Date();
        String formula = analysis.replaceShellVariable(session, context, this.formula, true, true);
        String pattern = analysis.replaceShellVariable(session, context, analysis.unQuotation(this.pattern), true, !analysis.containsQuotation(this.pattern));

        if (!analysis.isBlank(formula)) { // 执行日期运算
            // 转为日期计算的表达式: '2020-01-01 00:00:00:000'+1day-1month
            UniversalScriptExpression expression = new UniversalScriptExpression(session, context, stdout, stderr, "'" + Dates.format21(date) + "'" + formula);
            date = expression.dateValue();
        }

        if (session.isEchoEnable() || forceStdout) {
            stdout.println(pattern == null ? Dates.format19(date) : Dates.format(date, pattern));
        }

        session.setValue(date);
        return 0;
    }

    public boolean enableNohup() {
        return true;
    }
}
