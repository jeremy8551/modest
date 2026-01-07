package cn.org.expect.script.method.clazz;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.util.Settings;

/**
 * Settings.getProperty(key)
 */
@EasyVariableMethod(name = "getProperty", variable = UniversalScriptEngine.class, parameters = {String.class})
public class GetPropertyStrMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        String key = parameters.getString(0);
        return Settings.getProperty(key);
    }
}
