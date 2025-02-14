package cn.org.expect.script.method.inernal;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.method.VariableMethodEntry;

/**
 * 变量方法的实际类：通过反射调用类的静态方法
 */
public class MethodReflection implements UniversalScriptVariableMethod {

    /** 变量方法 对应的 类中的静态方法 */
    private final Method method;

    /** 方法信息 */
    private final VariableMethodEntry entry;

    public MethodReflection(Method method, VariableMethodEntry entry) {
        this.method = method;
        this.entry = entry;
    }

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        Class<?>[] parameterTypes = this.entry.getParameters();
        if (this.entry.isVarArgs()) {
            Object[] array = new Object[parameterTypes.length + 2]; // 参数值：变量、固定参数、可变参数数组
            array[0] = variable; // 变量

            // 固定参数
            for (int i = 0; i < parameterTypes.length; i++) {
                array[i + 1] = parameters.getValue(i, parameterTypes[i]);
            }

            // 可变参数
            int length = parameters.size() - parameterTypes.length;
            Class<?> varargClass = this.entry.getVarargClass();
            Object varargs = Array.newInstance(varargClass, length);
            for (int i = 0; i < length; i++) {
                Array.set(varargs, i, parameters.getValue(parameterTypes.length + i, varargClass));
            }
            array[array.length - 1] = varargs;
            return this.method.invoke(null, array);
        } else {
            Object[] array = new Object[parameterTypes.length + 1];
            array[0] = variable;

            for (int i = 0; i < parameterTypes.length; i++) {
                array[i + 1] = parameters.getValue(i, parameterTypes[i]);
            }
            return this.method.invoke(null, array);
        }
    }
}
