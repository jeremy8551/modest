package cn.org.expect.script.method;

import java.lang.reflect.Method;
import java.util.List;

import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.script.internal.MethodNote;
import cn.org.expect.script.method.inernal.MethodReflection;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;

/**
 * 变量方法
 */
public class VariableMethodEntry {

    /** 方法的类信息 */
    private final Class<?> methodClass;

    /** 变量的类信息 */
    private final Class<?> variableClass;

    /** 方法名 */
    private final String name;

    /** 参数类型 */
    private final Class<?>[] parameters;

    /** true表示最后右侧参数是一个（Varargs Method）可变参数：String... array */
    private final boolean varargs;

    /** 可变参数类型 */
    private final Class<?> varargClass;

    /** true表示单例模式 */
    private final boolean singleton;

    /** true表示可以使用实例对象 {@linkplain #variableMethod}，false表示不可以使用 */
    private volatile boolean canUse;

    /** 方法的实例对象 */
    private final UniversalScriptVariableMethod variableMethod;

    /** 方法的实现信息 */
    private final String methodInfo;

    /** 静态方法信息 */
    private Method staticMethod;

    /**
     * 实现了 {@linkplain UniversalScriptVariableMethod} 接口的变量方法
     *
     * @param method 变量方法
     */
    public VariableMethodEntry(UniversalScriptVariableMethod method) {
        EasyVariableMethod annotation = method.getClass().getAnnotation(EasyVariableMethod.class);
        Class<?>[] array = annotation.parameters();

        this.variableClass = annotation.variable();
        this.name = StringUtils.trimBlank(annotation.name());

        this.varargs = annotation.varargs();
        if (this.varargs) {
            this.parameters = ClassUtils.subarray(array, 0, array.length - 1);
            Class<?> lastElement = ArrayUtils.last(array);
            if (!lastElement.isArray()) {
                throw new UniversalScriptException("script.stderr.message090", method.getClass().getName(), annotation);
            }
            this.varargClass = lastElement.getComponentType();
        } else {
            this.parameters = array;
            this.varargClass = null;
        }

        this.methodInfo = method.getClass().getName();
        this.singleton = annotation.singleton();
        this.canUse = true;

        this.variableMethod = method;
        this.methodClass = method.getClass();
    }

    /**
     * 类中定义的静态方法
     *
     * @param method 静态方法
     * @param type   静态方法所在的类
     */
    public VariableMethodEntry(Method method, Class<?> type) {
        this.staticMethod = method;
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?>[] array = ClassUtils.subarray(parameterTypes, 1, parameterTypes.length);

        this.variableClass = parameterTypes[0];
        this.name = method.getName();

        this.varargs = method.isVarArgs();
        if (this.varargs) {
            this.parameters = ClassUtils.subarray(array, 0, array.length - 1);
            this.varargClass = ArrayUtils.last(array).getComponentType();
        } else {
            this.parameters = array;
            this.varargClass = null;
        }

        String genericString = method.toGenericString();
        int index = genericString.indexOf(type.getName());
        if (index != -1) {
            this.methodInfo = genericString.substring(index);
        } else {
            this.methodInfo = genericString;
        }

        this.singleton = true;
        this.canUse = true;

        this.methodClass = type;
        this.variableMethod = new MethodReflection(method, this);
    }

    /**
     * 方法名
     *
     * @return 方法名
     */
    public String getName() {
        return this.name;
    }

    /**
     * 固定参数
     *
     * @return 数组
     */
    public Class<?>[] getParameters() {
        return this.parameters;
    }

    /**
     * 是否是可变参数
     */
    public boolean isVarArgs() {
        return varargs;
    }

    /**
     * 方法的类信息
     *
     * @return 类信息
     */
    public Class<?> getMethodClass() {
        return methodClass;
    }

    /**
     * 定义方法的位置信息
     *
     * @return 字符串
     */
    public String getMethodInfo() {
        return methodInfo;
    }

    /**
     * 变量的类信息
     *
     * @return 类信息
     */
    public Class<?> getVariableClass() {
        return variableClass;
    }

    /**
     * 变量的类名（不包括包名）
     *
     * @return 类名
     */
    public String getVariableClassName() {
        return this.variableClass.isArray() ? this.variableClass.getComponentType().getSimpleName() : this.variableClass.getSimpleName();
    }

    /**
     * 可变参数类型
     *
     * @return 类信息
     */
    public Class<?> getVarargClass() {
        return varargClass;
    }

    public boolean canGetMethod() {
        return canUse;
    }

    /**
     * 方法
     *
     * @return 方法
     */
    public UniversalScriptVariableMethod getVariableMethod() {
        if (this.singleton) {
            return variableMethod;
        } else {
            this.canUse = false;
            return variableMethod;
        }
    }

    public void returnMethod(UniversalScriptVariableMethod method) {
        if (method != null && method.equals(this.variableMethod)) {
            this.canUse = true;
        }
    }

    public Method getStaticMethod() {
        return this.staticMethod;
    }

    public boolean equals(Object obj) {
        if (obj instanceof VariableMethodEntry) {
            VariableMethodEntry entry = (VariableMethodEntry) obj;

            return ClassUtils.equals(entry.getVariableClass(), this.getVariableClass()) //
                && entry.getName().equalsIgnoreCase(this.getName()) //
                && ClassUtils.equals(entry.getParameters(), this.getParameters()) //
                && ClassUtils.equals(entry.getVarargClass(), this.getVarargClass()) //
                && entry.isVarArgs() == this.isVarArgs() //
                ;
        }
        return false;
    }

    public String toStandardString() {
        StringBuilder buf = new StringBuilder();

        Class<?> variableClass = this.getVariableClass();
        buf.append(variableClass.isArray() ? variableClass.getComponentType().getSimpleName() : variableClass.getSimpleName());

        if (this.getName().equals("[")) {
            buf.append(this.getName());
        } else {
            buf.append('.');
            buf.append(this.getName());
            buf.append("(");
        }

        Class<?>[] parameters = this.getParameters();
        for (int i = 0; i < parameters.length; ) {
            Class<?> parameter = parameters[i];
            buf.append(parameter.getSimpleName());
            if (++i < parameters.length) {
                buf.append(", ");
            }
        }

        if (this.isVarArgs()) {
            if (parameters.length > 0) {
                buf.append(", ");
            }
            buf.append(this.getVarargClass().getSimpleName());
            buf.append("...");
        }

        if (this.getName().equals("[")) {
            buf.append("]");
        } else {
            buf.append(")");
        }

        return buf.toString();
    }

    /**
     * functionName(Type name1, Type name2)
     *
     * @param note 注释信息
     * @return 方法信息
     */
    public String toStandardString(MethodNote note) {
        StringBuilder buf = new StringBuilder();
        if (this.getName().equals("[")) {
            buf.append(this.getVariableClassName());
            buf.append("[int index]");
        } else {
            buf.append(this.getName());
            buf.append("(");
            List<MethodNote.Property> parameterList = note.getParameterList();
            Class<?>[] parameters = this.getParameters();
            for (int i = 0; i < parameters.length; ) {
                Class<?> parameter = parameters[i];

                buf.append(parameter.getSimpleName());
                if (i < parameterList.size()) {
                    buf.append(' ');
                    buf.append(parameterList.get(i).getName());
                }

                if (++i < parameters.length) {
                    buf.append(", ");
                }
            }

            if (this.isVarArgs()) {
                if (parameters.length > 0) {
                    buf.append(", ");
                }
                buf.append(this.getVarargClass().getSimpleName());
                buf.append("...");

                if (parameters.length < parameterList.size()) {
                    buf.append(' ');
                    buf.append(parameterList.get(parameters.length).getName());
                }
            }

            buf.append(")");
        }
        return buf.toString();
    }

    /**
     * functionName(Type, Type)
     *
     * @return 方法信息
     */
    public String toTitle() {
        StringBuilder buf = new StringBuilder(50);
        if (this.getName().equals("[")) {
            buf.append(this.getVariableClassName());
            buf.append("[int]");
        } else {
            buf.append(this.getName());
            buf.append("(");
            Class<?>[] parameters = this.getParameters();
            for (int i = 0; i < parameters.length; ) {
                Class<?> parameter = parameters[i];
                buf.append(parameter.getSimpleName());
                if (++i < parameters.length) {
                    buf.append(", ");
                }
            }

            if (this.isVarArgs()) {
                if (parameters.length > 0) {
                    buf.append(", ");
                }
                buf.append(this.getVarargClass().getSimpleName());
                buf.append("...");
            }

            buf.append(")");
        }
        return buf.toString();
    }
}
