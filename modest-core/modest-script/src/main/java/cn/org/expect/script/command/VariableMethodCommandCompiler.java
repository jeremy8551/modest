package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.annotation.ScriptCommand;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptContextAware;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptUsage;
import cn.org.expect.script.method.VariableMethodRepository;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@ScriptCommand(name = "*", keywords = {})
public class VariableMethodCommandCompiler extends AbstractTraceCommandCompiler implements UniversalScriptContextAware {

    private UniversalScriptContext context;

    private VariableMethodRepository methods;

    /**
     * 自动注入脚本引擎上下文信息
     *
     * @param context 脚本引擎上下文信息
     */
    public void setContext(UniversalScriptContext context) {
        this.context = context;
        this.methods = new VariableMethodRepository(context);
    }

    public UniversalCommandCompilerResult match(String name, String script) {
        return this.context.containVariable(name) ? UniversalCommandCompilerResult.NEUTRAL : UniversalCommandCompilerResult.IGNORE;
    }

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public VariableMethodCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) {
        int index = 0, length = command.length();

        boolean reverse = false;
        if (command.charAt(0) == '!') {
            reverse = true;
            index = 1;
        }

        for (; index < length; index++) {
            char c = command.charAt(index);
            if (c == '[' || c == '.') {
                break;
            }
        }

        Ensure.isTrue(index < length, command);
        String variableName = command.substring(reverse ? 1 : 0, index); // 变量名
        String methodName = this.readMethodName(command, index); // 变量方法名
        return new VariableMethodCommand(this, orginalScript, this.methods, variableName, methodName, reverse);
    }

    /**
     * 解析变量方法
     *
     * @param analysis     语句分析器
     * @param variableName 变量名
     * @param methodName   变量方法名, 如: ls, [0]
     * @param reverse      true表示布尔值取反
     */
    public VariableMethodCommand compile(UniversalScriptAnalysis analysis, String variableName, String methodName, boolean reverse) {
        String command = methodName.length() > 0 && methodName.charAt(0) == '[' ? (variableName + methodName) : (variableName + "." + methodName);
        return new VariableMethodCommand(this, command, this.methods, variableName, methodName, reverse);
    }

    /**
     * 返回变量方法的仓库
     *
     * @return 变量方法的仓库
     */
    public VariableMethodRepository getRepository() {
        return this.methods;
    }

    /**
     * 从指定位置 start 开始删除字符串右端的空白字符与 token 字符，删除一次字符串左端的半角句号字符
     *
     * @param str   字符串
     * @param start 截取字符串左侧开始位置，从0开始
     * @return 方法名
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

    public void usage(UniversalScriptContext context, UniversalScriptStdout out) {
        out.println(new ScriptUsage(this.getClass(), this.methods.toString(StringUtils.CHARSET)));
    }

}
