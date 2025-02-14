package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.sort.OrderByExpression;
import cn.org.expect.util.StringUtils;

/**
 * 对表格型文件进行排序 <br>
 * <p>
 * sort table file ~/file/name.txt of del [modified by filecount=2 keeptmp readbuf=8192] order by int(1) desc,2,3 {asc | desc}
 *
 * @author jeremy8551@gmail.com
 */
@EasyCommandCompiler(name = "sort", keywords = {"sort"})
public class SortTableFileCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception {
        WordIterator it = analysis.parse(analysis.unQuotation(analysis.replaceShellVariable(session, context, command, true, true)));
        it.assertNext("sort");
        it.assertNext("table");
        it.assertNext("file");
        String filepath = analysis.unQuotation(it.readUntil("of")); // 文件路径
        String filetype = it.next(); // 文件类型
        CommandAttribute attrs = new CommandAttribute( //
            "charset:", "codepage:", "rowdel:", "coldel:", "escape:", //
            "chardel:", "column:", "colname:", "readbuf:", "writebuf:", //
            "thread:", "maxrow:", "maxfile:", "keeptemp", "covsrc", "temp:" //
        );

        if (it.isNext("modified")) {
            it.assertNext("modified");
            it.assertNext("by");

            while (!it.isNext("order")) { // 如果下一个单词不是 select
                String word = it.next();
                String[] array = StringUtils.splitProperty(word);
                if (array == null) {
                    attrs.setAttribute(word, ""); // 无值参数
                } else {
                    attrs.setAttribute(array[0], array[1]);
                }
            }
        }
        it.assertNext("order");
        it.assertNext("by");

        EasyContext ioc = context.getContainer();
        String position = it.readOther();
        String[] array = StringUtils.split(StringUtils.trimBlank(position), analysis.getSegment()); // int(1) desc,2, 4,5
        OrderByExpression[] orders = new OrderByExpression[array.length];
        for (int i = 0; i < array.length; i++) {
            orders[i] = new OrderByExpression(ioc, analysis, array[i]);
        }
        return new SortTableFileCommand(this, orginalScript, filepath, filetype, orders, attrs);
    }
}
