package cn.org.expect.script.command;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.script.method.VariableMethodEntry;
import cn.org.expect.script.method.VariableMethodRepository;
import cn.org.expect.script.method.inernal.MethodParameters;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 变量方法命令
 */
public class VariableMethodCommand extends AbstractTraceCommand implements NohupCommandSupported {
    private final static Log log = LogFactory.getLog(VariableMethodCommand.class);

    /** 变量名 */
    private final String variableName;

    /** 变量方法名 */
    private final String methodExpression;

    /** 变量方法执行结果 */
    private Object value;

    /** true表示布尔值取反 */
    private final boolean reverse;

    /** 变量方法集合 */
    private final VariableMethodRepository repository;

    /** 参数集合 */
    private final MethodParameters parameters;

    /**
     * 初始化
     *
     * @param command          脚本命令
     * @param repository       变量方法仓库
     * @param variableName     变量名
     * @param methodExpression 变量方法表达式，如：<br>
     *                         substr(1, 2) <br>
     *                         substr(1, 2).length() <br>
     * @param reverse          true表示取反
     */
    public VariableMethodCommand(UniversalCommandCompiler compiler, String command, VariableMethodRepository repository, String variableName, String methodExpression, boolean reverse) {
        super(compiler, command);
        this.parameters = new MethodParameters();
        this.repository = repository;
        this.variableName = variableName;
        this.methodExpression = methodExpression;
        this.reverse = reverse;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        this.value = null;
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String variableName = analysis.replaceShellVariable(session, context, this.variableName, true, false);
        Object variable = context.getVariable(variableName);
        String methodExpression = analysis.trim(analysis.replaceShellVariable(session, context, this.methodExpression, true, false), 0, 0);
        int exitcode = this.executeMethod(session, context, stdout, stderr, analysis, variable, methodExpression); // 执行变量方法
        if (exitcode != 0) {
            return exitcode;
        }

        // 对结果取反
        if (this.reverse) {
            if (this.value instanceof Boolean) {
                boolean value = (Boolean) this.value;
                this.value = !value;
                return 0;
            } else {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message059", this.getScript()));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        }
        return 0;
    }

    /**
     * 执行下一个变量方法
     *
     * @param session          用户会话信息
     * @param context          脚本引擎上下文信息
     * @param stdout           标准信息输出流
     * @param stderr           错误信息输出流
     * @param analysis         语句分析器
     * @param variable         变量
     * @param methodExpression 变量方法表达式，如：<br>
     *                         substr(1, 2) <br>
     *                         substr(1, 2).length() <br>
     *                         [0] <br>
     * @return 返回值，0表示变量方法执行正确 非0表示发生错误
     * @throws Exception 发生错误
     */
    protected int executeMethod(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, String methodExpression) throws Exception {
        // 变量值
        if (variable == null) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message092", methodExpression));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        methodExpression = StringUtils.trimBlank(methodExpression);
        boolean isArray = methodExpression.startsWith("[");

        // 变量方法
        String methodName = isArray ? "[" : this.parseFirstMethodNamePrefix(methodExpression); // substr 或 [

        // 参数起始位置
        char startChar = isArray ? '[' : '(';
        String endChar = isArray ? "]" : ")";
        int paramExprBegin = isArray ? 0 : methodName.length(); // 方法名: ls, [
        if (methodExpression.charAt(paramExprBegin) != startChar) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message085", methodExpression, startChar));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        // 参数的起始位置
        int paramBegin = paramExprBegin + 1;
        int paramEnd = analysis.indexOf(methodExpression, endChar, paramBegin, 2, 2);  // 参数的结束位置
        if (paramEnd == -1) {
            stderr.println(ResourcesUtils.getMessage("script.stderr.message086", methodExpression, endChar));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        // 参数
        this.parameters.parse(session, context, analysis, methodExpression.substring(paramBegin, paramEnd));

        // 方法返回值
        Object value;

        // 打印日志
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message051", methodExpression, variable.getClass().getName(), methodName, this.parameters.toStandardString());
        }

        // 变量方法信息
        VariableMethodEntry entry = this.repository.get(variable, methodName, this.parameters);
        if (entry == null) { // 反射调用
            Class<?> variableClass = variable.getClass();
            Method method = this.getMethod(variableClass, methodName);
            if (method != null) {
                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message053", method.toGenericString());
                }

                value = this.execute(variable, method);
            } else {
                // 未知方法
                stderr.println(ResourcesUtils.getMessage("script.stderr.message091", variable.getClass().getName(), methodName + "(" + this.parameters.toStandardString() + ")"));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("script.stdout.message054", entry.getMethodInfo());
            }

            // 执行变量方法
            boolean needReturn = entry.canGetMethod();
            UniversalScriptVariableMethod method = needReturn ? entry.getVariableMethod() : (UniversalScriptVariableMethod) context.getContainer().newInstance(entry.getMethodClass());
            try {
                value = method.execute(session, context, stdout, stderr, analysis, variable, this.parameters);
            } finally {
                if (needReturn) {
                    entry.returnMethod(method);
                }
            }
        }

        // 下一个方法的起始位置
        int next = paramEnd + 1;

        // 下一个变量方法是点操作
        if (StringUtils.startsWith(methodExpression, '.', next, false, true)) {
            int nextMethodBegin = methodExpression.indexOf('.', next);
            if (nextMethodBegin == methodExpression.length() - 1) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message088", methodExpression, '.'));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }

            // 读取下一个变量方法
            String nextMethodExpression = methodExpression.substring(nextMethodBegin + 1);
            return this.executeMethod(session, context, stdout, stderr, analysis, value, nextMethodExpression);
        }

        // 下一个变量方法是索引操作
        if (StringUtils.startsWith(methodExpression, '[', next, false, true)) {
            int nextMethodBegin = methodExpression.indexOf('[', next);
            if (nextMethodBegin == methodExpression.length() - 1) {
                stderr.println(ResourcesUtils.getMessage("script.stderr.message088", methodExpression, '['));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }

            // 读取下一个变量方法
            String nextMethodExpression = methodExpression.substring(nextMethodBegin);
            return this.executeMethod(session, context, stdout, stderr, analysis, value, nextMethodExpression);
        }

        // 变量方法右侧没有其他内容，直接返回结果
        if (StringUtils.isBlank(methodExpression, next)) {
            this.value = value;
            return 0;
        }

        // 变量方法右侧存在未知内容
        stderr.println(ResourcesUtils.getMessage("script.stderr.message089", methodExpression, methodExpression.substring(next)));
        return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
    }

    /**
     * 返回匹配的方法
     *
     * @param type       类信息
     * @param methodName 方法名
     * @return 方法信息
     */
    public Method getMethod(Class<?> type, String methodName) {
        Method match = null;
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (methodName.equalsIgnoreCase(method.getName()) && this.parameters.match(method)) {
                if (match == null) {
                    match = method;
                } else {
                    boolean mv1 = method.isVarArgs();
                    boolean mv2 = match.isVarArgs();
                    if (!mv1 && !mv2) {
                        throw new UnsupportedOperationException(method.toGenericString() + " " + match.toGenericString());
                    }

                    if (!mv1) {
                        return method;
                    } else {
                        return match;
                    }
                }
            }
        }
        return match;
    }

    public Object execute(Object variable, Method method) throws IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] array = new Object[parameterTypes.length]; // 参数值：固定参数、可变参数数组
        if (method.isVarArgs()) {
            int size = parameterTypes.length - 1;

            // 固定参数
            for (int i = 0; i < size; i++) {
                array[i] = this.parameters.getValue(i, parameterTypes[i]);
            }

            // 可变参数
            int length = this.parameters.size() - size;
            Class<?> varargClass = ArrayUtils.last(parameterTypes).getComponentType();
            Object varargs = Array.newInstance(varargClass, length);
            for (int i = 0; i < length; i++) {
                Array.set(varargs, i, this.parameters.getValue(size + i, varargClass));
            }
            array[array.length - 1] = varargs;
        } else {
            for (int i = 0; i < parameterTypes.length; i++) {
                array[i] = this.parameters.getValue(i, parameterTypes[i]);
            }
        }

        try {
            return method.invoke(variable, array);
        } catch (Throwable e) {
            throw new UniversalScriptException(method.toGenericString(), e);
        }
    }

    public boolean enableNohup() {
        return true;
    }

    /**
     * 变量方法的返回值
     *
     * @return 变量方法的返回值
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * 返回第一个变量方法名
     *
     * @param method 变量方法表达式, 如: <br>
     *               substr() <br>
     *               substr(1, 2).length() <br>
     * @return 变量方法名
     */
    public String parseFirstMethodNamePrefix(String method) {
        int index = method.indexOf('(');
        if (index != -1) {
            return method.substring(0, index);
        } else {
            return method;
        }
    }
}
