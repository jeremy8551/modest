package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.database.JdbcObjectConverter;
import cn.org.expect.database.export.ExtractUserListener;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.io.TextTable;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptUsage;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.StringUtils;

@ScriptCommand(name = "db", keywords = {})
public class DBExportCommandCompiler extends AbstractTraceCommandCompiler {

    public final static String REGEX = "^(?i)db\\s+export\\s+to\\s*.*";

    private Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    public UniversalCommandCompilerResult match(String name, String script) {
        return pattern.matcher(script).find() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readMultilineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("db");
        it.assertNext("export");
        it.assertNext("to");
        String filepath = analysis.unQuotation(it.readUntil("of"));
        String filetype = it.next();

        CommandAttribute attrs = new CommandAttribute( //
                "charset:", "codepage:", "rowdel:", "coldel:", "escape:", //
                "chardel:", "column:", "colname:", "catalog:", "message:", //
                "listener:", "convert:", "charhide:", "writebuf:", "append", //
                "maxrows:", "dateformat:", "timeformat:", "timestampformat:", //
                "progress:", "escapes:", "title", "sleep:" //
        );

        if (it.isNext("modified")) {
            it.assertNext("modified");
            it.assertNext("by");

            while (!it.isNext("select")) { // 如果下一个单词不是 select
                String word = it.next();
                String[] array = StringUtils.splitProperty(word);
                if (array == null) {
                    attrs.setAttribute(word, ""); // 无值参数
                } else {
                    attrs.setAttribute(array[0], array[1]);
                }
            }
        }

        String sql = it.readOther();
        return new DBExportCommand(this, command, filepath, filetype, sql, attrs);
    }

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) {
        // 查找接口对应的的实现类
        List<EasyBeanInfo> list1 = context.getContainer().getBeanInfoList(TextTableFile.class);
        CharTable ct1 = new CharTable(context.getCharsetName());
        ct1.addTitle("");
        ct1.addTitle("");
        ct1.addTitle("");
        for (EasyBeanInfo beanInfo : list1) {
            ct1.addCell(beanInfo.getName());
            ct1.addCell(beanInfo.getDescription());
            ct1.addCell(beanInfo.getType().getName());
        }

        // 查找接口对应的的实现类
        List<EasyBeanInfo> list2 = context.getContainer().getBeanInfoList(ExtractWriter.class);
        CharTable ct2 = new CharTable(context.getCharsetName());
        ct2.addTitle("");
        ct2.addTitle("");
        ct2.addTitle("");
        for (EasyBeanInfo beanInfo : list2) {
            ct2.addCell(beanInfo.getName());
            ct2.addCell(beanInfo.getDescription());
            ct2.addCell(beanInfo.getType().getName());
        }

        out.println(new ScriptUsage(this.getClass() //
                , TextTable.class.getName() // 0
                , cn.org.expect.annotation.EasyBean.class.getName() // 1
                , ExtractUserListener.class.getName() // 2
                , JdbcObjectConverter.class.getName() // 3
                , ExtractWriter.class.getName() // 4
                , ct1.toString(CharTable.Style.simple) // 5
                , ct2.toString(CharTable.Style.simple) // 6
                , TextTable.class.getName() // 7
        ));
    }

}
