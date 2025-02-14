package cn.org.expect.script.method.clazz;

import java.lang.reflect.Constructor;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;

/**
 * 创建类的实例对象
 */
@EasyVariableMethod(name = "newInstance", variable = Class.class, parameters = {Object[].class}, varargs = true)
public class NewInstanceMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        EasyContext ioc = context.getContainer();
        Class<?> type = (Class<?>) variable;
        if (parameters.size() == 0) {
            return ioc.newInstance(type);
        } else {
            Constructor<?>[] constructors = type.getConstructors(); // 类与父类中公共（public）构造方法
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() > 0) {
                    Object object;
                    try {
                        object = constructor.newInstance(parameters.toArray(constructor.getParameterTypes()));
                    } catch (Throwable e) {
                        continue;
                    }

                    ioc.autowire(object);
                    return object;
                }
            }

            throw new UniversalScriptException("script.stderr.message036", type.getName(), parameters.toStandardString());
        }
    }
}
