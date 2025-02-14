package cn.org.expect.script.method.array;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.util.StringUtils;

/**
 * 在数组中搜索
 *
 * @author jeremy8551@gmail.com
 */
@EasyVariableMethod(name = "indexOf", variable = Object[].class, parameters = {String.class})
public class IndexOfMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        String parameter = parameters.get(0);
        if (parameter.length() == 0) {
            throw new IllegalArgumentException(parameters.get(0));
        }

        //
        Object[] array = (Object[]) variable;
        Object dest;
        if (parameters.isString(0)) {
            dest = parameters.getString(0);
        } else if (context.containsVariable(parameter)) {
            dest = context.getVariable(parameter);
        } else {
            dest = StringUtils.parsePrimitive(array.getClass().getComponentType(), parameters.get(0));
        }

        if (dest == null) {
            throw new NullPointerException();
        }

        for (int i = 0; i < array.length; i++) {
            if (dest.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
}
