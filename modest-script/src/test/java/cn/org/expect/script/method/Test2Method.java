package cn.org.expect.script.method;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;

@EasyVariableMethod(name = "test", variable = CharSequence.class, parameters = {int.class, int.class, Double[].class}, varargs = true)
public class Test2Method implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        return "test2";
    }
}
