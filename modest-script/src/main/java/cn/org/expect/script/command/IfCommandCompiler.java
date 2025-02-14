package cn.org.expect.script.command;

import java.io.IOException;
import java.util.List;

import cn.org.expect.expression.Word;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.expression.WordQuery;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "if", keywords = {"if", "then", "elseif", "else", "fi"})
public class IfCommandCompiler extends AbstractCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("if", "fi");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        IfCommand ifcommand = new IfCommand(this, command);
        QueryTail rule = new QueryTail();
        WordIterator it = analysis.parse(command);
        it.assertNext("if");
        it.assertLast("fi");

        String condition = it.readUntil("then");
        if (analysis.isBlank(condition)) {
            throw new UniversalScriptException("script.stderr.message072", command);
        }

        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message030", "if", condition);
        }

        // 搜索 elseif 与 else 关键字
        String ifBody = it.read(rule); // 读取 then 关键到 elseif 或 else 关键字之间的逻辑

        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message031", "if", ifBody);
        }

        ifcommand.setIf(condition, parser.read(ifBody));

        while (true) {
            if (it.equals("elseif")) { // 如果下一个起始单词是 elseif 关键字
                condition = it.readUntil("then");
                if (analysis.isBlank(condition)) {
                    throw new UniversalScriptException("script.stderr.message072", command);
                }

                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message030", "elseif", condition);
                }

                String elseIfBody = it.read(rule);

                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message031", "elseif", elseIfBody);
                }

                ifcommand.addElseIf(condition, parser.read(elseIfBody));
            } else { // 下一个起始单词是 else 关键字
                String elseBody = it.readOther();
                ifcommand.setElse(parser.read(elseBody));
                break;
            }
        }

        return ifcommand;
    }

    /**
     * 搜索 if 语句中代码逻辑块的结束位置
     */
    static class QueryTail implements WordQuery {

        public int indexOf(CharSequence src, List<Word> list, int index, int last) {
            int i = index;
            for (; i <= last && i < list.size(); i++) {
                Word obj = list.get(i);
                String word = obj.getContent();

                // 如果是 elseif 或 else 关键字
                if (StringUtils.inArrayIgnoreCase(word, "elseif", "else")) {
                    return i;
                }

                // 嵌套 if 语句
                if (word.equalsIgnoreCase("if")) {
                    for (int j = i + 1; j <= last && j < list.size(); j++) {
                        obj = list.get(j);
                        word = obj.getContent();
                        if (word.equalsIgnoreCase("fi")) { // 嵌套语句结束位置
                            i = j;
                            break;
                        }
                    }
                    continue;
                }
            }

            return i;
        }
    }
}
