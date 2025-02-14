package cn.org.expect.script.method.object;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;

/**
 * 将对象转为整数
 */
@EasyVariableMethod(name = "int", variable = Object.class)
public class IntMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        return Integer.parseInt(context.getEngine().getFormatter().format(variable));
    }
}
