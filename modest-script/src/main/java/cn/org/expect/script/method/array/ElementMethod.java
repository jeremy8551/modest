package cn.org.expect.script.method.array;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;

/**
 * 返回数组中指定位置上的元素
 *
 * @author jeremy8551@gmail.com
 */
@EasyVariableMethod(name = "[", variable = Object[].class, parameters = {int.class})
public class ElementMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        Object[] array = (Object[]) variable;
        int index = parameters.getInt(0);
        return array[index];
    }
}
