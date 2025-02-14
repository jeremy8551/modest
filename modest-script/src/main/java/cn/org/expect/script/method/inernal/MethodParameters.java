package cn.org.expect.script.method.inernal;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;

public class MethodParameters implements UniversalScriptVariableMethodParameters {

    private UniversalScriptContext context;

    private UniversalScriptAnalysis analysis;

    private final List<String> list;

    public MethodParameters() {
        this.list = new ArrayList<String>();
    }

    public void parse(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptAnalysis analysis, String expression) {
        this.context = context;
        this.analysis = analysis;
        this.list.clear();
        if (StringUtils.isNotBlank(expression)) {
            analysis.split(expression, this.list, analysis.getSegment());
            for (int i = 0; i < this.list.size(); i++) {
                String str = this.list.get(i);
                String value = analysis.replaceShellVariable(session, context, str, true, false);
                this.list.set(i, value);
            }
        }
    }

    public String get(int index) {
        return this.list.get(index);
    }

    public Object getValue(int index, Class<?> type) {
        String value = this.get(index);

        // null
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }

        // 对象
        if (Object.class.equals(type) && this.analysis.containsQuotation(value)) {
            return this.analysis.unQuotation(value);
        }

        // 字符串
        if (CharSequence.class.isAssignableFrom(type)) {
            return this.getString(index);
        }

        // 基础类型
        Object primitive = StringUtils.parsePrimitive(type, value);
        if (primitive != null) {
            return primitive;
        }

        // 引用类型
        if (this.context.containsVariable(value)) {
            return this.context.getVariable(value);
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, type.getName());
    }

    public String getString(int index) {
        String value = this.get(index);

        // null
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }

        // 字符串两端有引号
        if (this.analysis.containsQuotation(value)) {
            return this.analysis.unQuotation(value);
        }

        // 字符串变量
        CharSequence strValue = this.getVariable(CharSequence.class, value);
        if (strValue != null) {
            return strValue.toString();
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, String.class.getName());
    }

    public int getInt(int index) {
        String value = this.get(index);

        // 字符串
        Integer variable = StringUtils.parseInt(value);
        if (variable != null) {
            return variable;
        }

        // 变量值
        variable = this.getVariable(Integer.class, value);
        if (variable != null) {
            return variable;
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, int.class.getName());
    }

    public long getLong(int index) {
        String value = this.get(index);

        // 字符串
        Long variable = StringUtils.parseLong(value);
        if (variable != null) {
            return variable;
        }

        // 变量值
        variable = this.getVariable(Long.class, value);
        if (variable != null) {
            return variable;
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, long.class.getName());
    }

    public boolean getBoolean(int index) {
        String value = this.get(index);

        // 字符串
        Boolean variable = StringUtils.parseBoolean(value);
        if (variable != null) {
            return variable;
        }

        // 变量值
        variable = this.getVariable(Boolean.class, value);
        if (variable != null) {
            return variable;
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, Boolean.class.getName());
    }

    public BigDecimal getDecimal(int index) {
        String value = this.get(index);

        // null
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }

        // 字符串
        BigDecimal variable = StringUtils.parseDecimal(value);
        if (variable != null) {
            return variable;
        }

        // 变量值
        variable = this.getVariable(BigDecimal.class, value);
        if (variable != null) {
            return variable;
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, BigDecimal.class.getName());
    }

    public Date getDate(int index) {
        String value = this.get(index);

        // null
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }

        // 字符串两端有引号
        if (this.analysis.containsQuotation(value)) {
            return Dates.parse(this.analysis.unQuotation(value));
        }

        // 字符串变量
        if (this.context.containsVariable(value)) {
            return Dates.parse(this.context.getVariable(value));
        }

        throw new UniversalScriptException("script.stderr.message113", index, value, Date.class.getName());
    }

    public boolean isString(int index) {
        String value = this.get(index);

        // null
        if ("null".equalsIgnoreCase(value)) {
            return true;
        }

        // 字符串两端有引号
        if (this.analysis.containsQuotation(value)) {
            return true;
        }

        // 字符串变量
        return this.getVariable(CharSequence.class, value) != null;
    }

    @SuppressWarnings("unchecked")
    public <E> E getVariable(Class<E> type, String variableName) {
        // 变量
        if (this.context.containsVariable(variableName)) {
            Object variable = this.context.getVariable(variableName);
            if (variable != null && type.isAssignableFrom(variable.getClass())) {
                return (E) variable;
            }
        }
        return null;
    }

    public int size() {
        return this.list.size();
    }

    public boolean startsWith(Class<?>[] array) {
        if (this.list.size() < array.length) {
            return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (!this.match(i, array[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWith(Class<?> type, int from) {
        for (int i = from; i < this.size(); i++) {
            if (!this.match(i, type)) {
                return false;
            }
        }
        return true;
    }

    public boolean match(int index, Class<?> type) {
        if (Object.class.equals(type)) {
            return true;
        }

        // 字符串
        if (CharSequence.class.isAssignableFrom(type) && this.isString(index)) {
            return true;
        }

        String value = this.get(index);

        // null
        if ("null".equalsIgnoreCase(value)) {
            return true;
        }

        // 变量
        if (this.context.containsVariable(value)) {
            Object variable = this.context.getVariable(value);
            return ClassUtils.isAssignableFrom(type, variable.getClass());
        }

        // 基础类型
        return StringUtils.parsePrimitive(type, value) != null;
    }

    public boolean match(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes(); // 方法定义的参数类型
        if (method.isVarArgs()) { // 可变参数
            if (parameterTypes.length >= 1) {
                int size = parameterTypes.length - 1; // 固定参数个数
                if (size <= this.size()) {
                    for (int i = 0; i < size; i++) {
                        if (!this.match(i, parameterTypes[i])) {
                            return false;
                        }
                    }

                    // 非固定参数
                    return this.startsWith(parameterTypes[size].getComponentType(), size);
                }
            }
            return false;
        } else {
            return parameterTypes.length == this.size() && this.startsWith(parameterTypes);
        }
    }

    public Object[] toArray(Class<?>[] types) {
        if (this.list.size() != types.length) {
            throw new IllegalArgumentException(this.list.size() + " != " + types.length);
        }

        Object[] array = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            array[i] = this.getValue(i, types[i]);
        }
        return array;
    }

    public String toStandardString() {
        return StringUtils.join(this.list, ", ");
    }
}
