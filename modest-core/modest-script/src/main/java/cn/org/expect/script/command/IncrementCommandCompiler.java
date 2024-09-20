package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.io.TableColumnComparator;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.ioc.EasyBean;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptUsage;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.StringUtils;

@ScriptCommand(name = "extract", keywords = {"extract"})
public class IncrementCommandCompiler extends AbstractTraceCommandCompiler {

    public final static String REGEX = "^(?i)extract\\s+increment\\s+compare\\s+.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readMultilineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception {
        WordIterator it = analysis.parse(analysis.replaceShellVariable(session, context, command, false, true, true, false));
        it.assertNext("extract");
        it.assertNext("increment");
        it.assertNext("compare");
        String newfileExpr = it.readUntil("and");
        String oldfileExpr = it.readUntil("write");
        String script = it.readOther();
        String[] array = StringUtils.removeBlank(StringUtils.split(script, ArrayUtils.asList("write"), analysis.ignoreCase()));

        IncrementExpression newfileexpr = new IncrementExpression(session, context, newfileExpr);
        IncrementExpression oldfileexpr = new IncrementExpression(session, context, oldfileExpr);
        IncrementExpression[] writeExpr = new IncrementExpression[array.length];
        for (int i = 0; i < array.length; i++) {
            String expression = array[i];
            writeExpr[i] = new IncrementExpression(session, context, expression);
        }
        return new IncrementCommand(this, orginalScript, newfileexpr, oldfileexpr, writeExpr);
    }

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) { // 查找接口对应的的实现类
        List<EasyBean> list = context.getContainer().getBeanInfoList(TextTableFile.class);
        CharTable table = new CharTable(context.getCharsetName());
        table.addTitle("");
        table.addTitle("");
        table.addTitle("");
        for (EasyBean beanInfo : list) {
            table.addCell(beanInfo.getName());
            table.addCell(beanInfo.getDescription());
            table.addCell(beanInfo.getType().getName());
        }

        out.println(new ScriptUsage(this.getClass(), table.toString(CharTable.Style.simple), TableColumnComparator.class.getName()));
    }

}
