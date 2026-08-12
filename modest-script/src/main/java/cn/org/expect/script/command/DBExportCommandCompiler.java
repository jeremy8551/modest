package cn.org.expect.script.command;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

/**
 * 编译 db 脚本命令并创建对应的可执行命令
 */
@EasyCommandCompiler(name = "db")
public class DBExportCommandCompiler extends AbstractTraceCommandCompiler {

    public final static String REGEX = "^(?i)\\s*db\\s+export\\s+to\\s*.*";

    private final Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL | Pattern.MULTILINE);

    /** {@inheritDoc} */
    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        return pattern.matcher(script).matches() ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    /** {@inheritDoc} */
    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readMultilineScript();
    }

    /** {@inheritDoc} */
    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("db");
        it.assertNext("export");
        it.assertNext("to");
        String filepath = it.readUntil("of");
        String filetype = it.next();

        CommandAttribute attrs = new CommandAttribute(session, context, //
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
}
