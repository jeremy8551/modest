package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptContextAware;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.script.method.VariableMethodRepository;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 解析单行是变量方法的脚本，如：var.print()
 */
@EasyCommandCompiler(name = "*")
public class VariableMethodCommandCompiler extends AbstractTraceCommandCompiler implements UniversalScriptContextAware {

    private VariableMethodRepository methodRepository;

    public void setContext(UniversalScriptContext context) {
        this.methodRepository = context.getContainer().getBean(VariableMethodRepository.class);
    }

    public UniversalCommandCompilerResult match(UniversalScriptAnalysis analysis, String name, String script) {
        if (script.length() > name.length()) {
            if (script.charAt(0) == '!') { // 第一个字符是取反操作
                int size = name.length() + 1;
                if (size < script.length() && StringUtils.inArray(script.charAt(size), '.', '[')) {
                    return UniversalCommandCompilerResult.ACCEPT;
                } else {
                    return UniversalCommandCompilerResult.IGNORE;
                }
            }

            // variable.print()
            if (StringUtils.inArray(script.charAt(name.length()), '.', '[')) {
                return UniversalCommandCompilerResult.ACCEPT;
            }
        }
        return UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public VariableMethodCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) {
        int variableNameEnd = 0; // 变量名结束的位置（的下一个位置）
        int length = command.length();

        boolean reverse = false;
        if (command.charAt(0) == '!') {
            reverse = true;
            variableNameEnd = 1;
        }

        for (; variableNameEnd < length; variableNameEnd++) {
            char c = command.charAt(variableNameEnd);
            if (c == '[' || c == '.') {
                break;
            }
        }

        Ensure.isTrue(variableNameEnd < length, command);
        String variableName = command.substring(reverse ? 1 : 0, variableNameEnd); // 变量名
        String methodName = this.readMethodName(command, variableNameEnd); // 变量方法名
        return new VariableMethodCommand(this, orginalScript, this.methodRepository, variableName, methodName);
    }

    /**
     * 解析变量方法
     *
     * @param analysis     语句分析器
     * @param variableName 变量名
     * @param methodName   变量方法名, 如: substr(1, 2).length(), [0]
     */
    public VariableMethodCommand compile(UniversalScriptAnalysis analysis, String variableName, String methodName) {
        String command = methodName.length() > 0 && methodName.charAt(0) == '[' ? (variableName + methodName) : (variableName + "." + methodName);
        return new VariableMethodCommand(this, command, this.methodRepository, variableName, methodName);
    }

    /**
     * 返回变量方法的仓库
     *
     * @return 变量方法的仓库
     */
    public VariableMethodRepository getRepository() {
        return this.methodRepository;
    }

    /**
     * 读取变量方法的名字，删除一次字符串左端的句号
     *
     * @param str   字符串 <br>
     *              test.substr(1, 2) <br>
     *              test.substr(1, 2).length()
     * @param start 变量名结束位置的下一个位置
     * @return 变量方法名，substr(1, 2)，substr(1, 2).length()
     */
    private String readMethodName(String str, int start) {
        int sp = start, len = str.length(), ep = len - 1;
        if (sp < len && str.charAt(sp) == '.') { // 删除一次左边的半角句号
            sp++;
        }

        while (sp <= ep && ep >= 0 && Character.isWhitespace(str.charAt(ep))) { // 删除右侧的空白字符
            ep--;
        }
        return str.substring(sp, ep + 1);
    }
}
