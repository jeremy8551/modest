package cn.org.expect.script.internal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.expression.ExpressionException;
import cn.org.expect.expression.Formula;
import cn.org.expect.expression.Parser;
import cn.org.expect.expression.operation.Operator;
import cn.org.expect.expression.parameter.ExpressionParameter;
import cn.org.expect.expression.parameter.Parameter;
import cn.org.expect.script.UniversalCommandRepository;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.VariableMethodCommand;
import cn.org.expect.script.command.VariableMethodCommandCompiler;
import cn.org.expect.script.io.ScriptStdbuf;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 脚本引擎表达式解析器
 *
 * @author jeremy8551@qq.com
 * @createtime 2020-12-08
 */
public class ScriptExpressionParser extends Parser {

    private UniversalScriptSession session;
    private UniversalScriptContext context;
    private UniversalScriptStdout stdout;
    private UniversalScriptStderr stderr;
    private UniversalScriptAnalysis analysis;

    /**
     * 初始化
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     */
    public ScriptExpressionParser(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr) {
        super(session.getAnalysis());
        this.analysis = session.getAnalysis();
        this.session = session;
        this.context = context;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    /**
     * 对字符串变量中的子命令和变量进行替换, 再执行表达式运算
     *
     * @param datas      数值
     * @param operations 操作符
     */
    protected Formula calc(ArrayList<Parameter> datas, ArrayList<Operator> operations) {
        for (Parameter parameter : datas) {
            if (parameter.getType() == Parameter.STRING) {
                String str = parameter.stringValue();
                String value = this.analysis.replaceShellVariable(this.session, this.context, str, false, false, true, true);
                parameter.setValue(value);
            }

            if (parameter.getType() == Parameter.UNKNOWN && parameter.value() instanceof String) {
                parameter.setType(Parameter.STRING);
            }
        }

        return super.calc(datas, operations);
    }

    public int parse(String str, int start, boolean isData, List<Parameter> datas, List<Operator> operation) throws ExpressionException {
        boolean reverse = false; // true 表示布尔值取反
        int reverseIndex = -1;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);

            // 执行命令替换操作
            if (c == '`') {
                if (isData) {
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg034", str, i + 1), i + 1);
                }
                if (reverse) { // 不支持在命令替换符前面使用取反符!
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg061", str, reverseIndex), reverseIndex);
                }

                int index = this.analysis.indexOfAccent(str, i);
                if (index == -1) {
                    throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr108", str));
                }

                String command = str.substring(i + 1, index);
                ScriptStdbuf cache = new ScriptStdbuf(this.stdout);
                int exitcode = this.context.getEngine().evaluate(this.session, this.context, cache, this.stderr, command);
                if (exitcode == 0) {
                    datas.add(ExpressionParameter.parse(StringUtils.rtrim(cache.toString(), '\r', '\n')));
                    return index;
                } else {
                    throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr058", command));
                }
            }

            // 布尔表达式取反操作： !variableName.isfile()
            else if (c == '!') {
                reverse = true;
                reverseIndex = i;
                continue;
            }

            // 替换变量 $? $# $0 $1 ... $name ${name}
            else if (c == '$') {
                if (isData) {
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg034", str, i + 1), i + 1);
                }
                if (reverse) { // 不支持取反操作符!
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg061", str, reverseIndex), reverseIndex);
                }

                // exists next character
                int next = i + 1;
                if (next >= str.length()) {
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg047", str, i + 1), i + 1);
                }

                // variable name
                char nc = str.charAt(next);
                if (nc == '?') { // $?
                    datas.add(new ExpressionParameter(Parameter.LONG, this.session.getMainProcess().getExitcode()));
                    return next;
                } else if (nc == '#') { // $#
                    String[] args = this.session.getFunctionParameter();
                    datas.add(new ExpressionParameter(Parameter.LONG, args.length >= 1 ? args.length - 1 : 0));
                    return next;
                } else if (StringUtils.isNumber(nc)) { // $0 $1 ...
                    String[] args = this.session.getFunctionParameter();
                    int end = this.analysis.indexOfInteger(str, next);
                    int index = Integer.parseInt(str.substring(next, end));
                    if (index < args.length) {
                        datas.add(ExpressionParameter.parse(args[index]));
                    } else {
                        throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg060", str, i + 1, "$" + index), i + 1);
                    }
                    return end - 1;
                } else if (nc == '{') { // 变量名 ${name}
                    int end = this.analysis.indexOfBrace(str, next);
                    if (end == -1) {
                        throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg046", str, i + 1), i + 1);
                    }

                    String variableName = str.substring(next + 1, end);
                    if (this.containsVariable(variableName)) {
                        Object value = this.getVariable(variableName);
                        datas.add(ExpressionParameter.toParameter(value));
                        return end;
                    } else {
                        throw new ExpressionException(ResourcesUtils.getMessage("script.message.stderr107", variableName), i + 1);
                    }
                } else if (nc == '_' || StringUtils.isLetter(nc)) { // 变量名 $name
                    int end = this.analysis.indexOfVariableName(str, next);
                    String variableName = str.substring(next, end);

                    if (this.containsVariable(variableName)) {
                        Object value = this.getVariable(variableName);
                        datas.add(ExpressionParameter.toParameter(value));
                        return end - 1;
                    } else {
                        throw new ExpressionException(ResourcesUtils.getMessage("script.message.stderr107", variableName), i + 1);
                    }
                } else {
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg047", i + 1), i + 1);
                }
            }

            // 执行变量方法
            else if (StringUtils.isLetter(c) || c == '_') {
                int next = this.analysis.indexOfVariableName(str, i);
                String variableName = str.substring(i, next);

                if (!this.containsVariable(variableName)) {
                    throw new UniversalScriptException(ResourcesUtils.getMessage("script.message.stderr107", str, variableName));
                }
                if (isData) {
                    throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg034", str, i + 1), i + 1);
                }

                // 打印变量值
                if (next >= str.length()) {
                    if (reverse) {
                        throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg061", str, reverseIndex), reverseIndex);
                    }

                    Object value = this.getVariable(variableName);
                    datas.add(ExpressionParameter.toParameter(value));
                    return next - 1;
                }

                char nc = str.charAt(next);

                // .function()
                if (nc == '.') {
                    int end = this.analysis.indexOfVariableMethod(str, next);
                    String methodName = str.substring(next + 1, end); // substr(1, 2).length()
                    Object value = this.executeMethod(this.session, this.analysis, variableName, methodName, reverse);
                    datas.add(ExpressionParameter.toParameter(value));
                    return end - 1;
                }

                // [index]
                else if (nc == '[') {
                    int end = this.analysis.indexOfVariableMethod(str, i);
                    String methodName = str.substring(next, end); // [index]
                    Object value = this.executeMethod(this.session, this.analysis, variableName, methodName, reverse);
                    datas.add(ExpressionParameter.toParameter(value));
                    return end - 1;
                }

                // 打印变量值
                else {
                    if (reverse) { // 不支持取反操作符!
                        throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg061", str, reverseIndex), reverseIndex);
                    }

                    Object value = this.getVariable(variableName);
                    datas.add(ExpressionParameter.toParameter(value));
                    return next - 1;
                }
            } else if (reverse) {
                throw new ExpressionException(ResourcesUtils.getMessage("expression.standard.output.msg061", str, reverseIndex), reverseIndex);
            }
        }
        return start;
    }

    /**
     * 判断是否存在变量
     *
     * @param name 变量名
     * @return 返回true表示存在变量
     */
    public boolean containsVariable(String name) {
        return this.context.containVariable(name) || this.session.containsVariable(name);
    }

    /**
     * 返回变量值
     *
     * @param name 变量名
     * @return 变量值6
     */
    public Object getVariable(String name) {
        if (this.context.containVariable(name)) {
            return this.context.getVariable(name);
        }

        if (this.session.containsVariable(name)) {
            return this.session.getVariable(name);
        }

        return null;
    }

    /**
     * 执行变量方法
     *
     * @param session      用户会话信息
     * @param analysis     语句分析器
     * @param variableName 变量名
     * @param methodName   变量方法名, 如: ls, [0]
     * @param reverse      true表示布尔值取反
     * @return 变量方法的返回值
     */
    protected Object executeMethod(UniversalScriptSession session, UniversalScriptAnalysis analysis, String variableName, String methodName, boolean reverse) {
        UniversalScriptCompiler compiler = session.getCompiler();
        UniversalCommandRepository repository = compiler.getRepository(); // 命令编译器集合
        VariableMethodCommandCompiler c = repository.get(VariableMethodCommandCompiler.class);

        int value;
        VariableMethodCommand command = c.compile(analysis, variableName, methodName, reverse);
        try {
            value = command.execute(this.session, this.context, this.stdout, this.stderr, false, null, null);
        } catch (Throwable e) {
            throw new UniversalScriptException(command.getScript(), e);
        }

        if (value == 0) {
            return command.getValue();
        } else {
            throw new UniversalScriptException(command.getScript());
        }
    }

}
