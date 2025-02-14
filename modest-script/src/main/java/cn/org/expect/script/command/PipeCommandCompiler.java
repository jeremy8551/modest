package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "^")
public class PipeCommandCompiler extends AbstractCommandCompiler {

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String line) {
        return this.containsPipe(analysis, line) ? UniversalCommandCompilerResult.ACCEPT : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        List<UniversalScriptCommand> commands = new ArrayList<UniversalScriptCommand>();
        List<String> list = this.splitPipe(analysis, command);
        for (String script : list) {
            if (analysis.isBlank(script)) {
                throw new UniversalScriptException("script.stderr.message018", command);
            }

            List<UniversalScriptCommand> cmdList = parser.read(script);
            if (cmdList.size() != 1) {
                throw new UniversalScriptException("script.stderr.message026", command);
            } else {
                commands.add(cmdList.get(0));
            }
        }

        return new PipeCommand(this, command, commands);
    }

    public List<String> splitPipe(UniversalScriptAnalysis analysis, String script) {
        char delimiter = '|';
        List<String> list = new ArrayList<String>();
        if (script == null) {
            return list;
        }

        int begin = 0;
        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);

            // 忽略括号中的空白字符
            if (c == '(') {
                i = analysis.indexOfParenthes(script, i);
                if (i == -1) {
                    list.add(script.subSequence(begin, script.length()).toString());
                    return list;
                }
                continue;
            }

            if (c == '[') {
                i = analysis.indexOfBracket(script, i);
                if (i == -1) {
                    list.add(script.subSequence(begin, script.length()).toString());
                    return list;
                }
                continue;
            }

            if (c == '{') {
                i = analysis.indexOfBrace(script, i);
                if (i == -1) {
                    list.add(script.subSequence(begin, script.length()).toString());
                    return list;
                }
                continue;
            }

            // 忽略字符常量中的空白
            else if (c == '\'') {
                i = analysis.indexOfQuotation(script, i);
                if (i == -1) {
                    list.add(script.subSequence(begin, script.length()).toString());
                    return list;
                }
                continue;
            }

            // 忽略双引号中的字符串常量
            else if (c == '\"') {
                i = analysis.indexOfDoubleQuotation(script, i);
                if (i == -1) {
                    list.add(script.subSequence(begin, script.length()).toString());
                    return list;
                }
                continue;
            }

            // 二个重音符表示内部命令
            else if (c == '`') {
                i = analysis.indexOfAccent(script, i);
                if (i == -1) {
                    list.add(script.subSequence(begin, script.length()).toString());
                    return list;
                }
                continue;
            }

            // 忽略字符数组中的字符
            else if (c == delimiter) {
                list.add(script.subSequence(begin, i).toString());
                for (int j = i + 1; j < script.length(); j++) {
                    char nc = script.charAt(j);
                    if (nc == delimiter) {
                        i++;
                    } else {
                        break; // 表示字符串起始位置
                    }
                }
                begin = i + 1;
            }
        }

        if (begin < script.length()) {
            list.add(script.subSequence(begin, script.length()).toString());
        } else if (begin == script.length()) {
            list.add("");
        }

        return list;
    }

    /**
     * 判断是否存在管道符
     *
     * @param analysis
     * @param script   脚本语句
     * @return 返回true表示存在管道符，false表示脚本语句中没有管道符
     */
    private boolean containsPipe(UniversalScriptAnalysis analysis, String script) {
        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);

            if (c == '(') {
                int end = analysis.indexOfParenthes(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            if (c == '[') {
                int end = analysis.indexOfBracket(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            if (c == '{') {
                int end = analysis.indexOfBrace(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            // 忽略字符常量中的空白
            if (c == '\'') {
                int end = analysis.indexOfQuotation(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            // 忽略双引号中的字符串常量
            else if (c == '\"') {
                int end = analysis.indexOfDoubleQuotation(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            // 忽略命令替换中的管道符
            else if (c == '`') {
                int end = analysis.indexOfAccent(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            } else if (c == '|') {
                int index = StringUtils.indexOfNotBlank(script, i + 1, -1);
                if (index == -1) {
                    return true;
                } else if (script.charAt(index) == '|') { // 连续2个竖线 ||
                    i = index;
                } else {
                    return true;
                }
            }
        }

        return false;
    }
}
