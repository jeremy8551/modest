package cn.org.expect.script.method;

import java.util.Date;

import cn.org.expect.script.annotation.ScriptFunction;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.Dates;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

@ScriptFunction(name = "getMillis")
public class GetMillisMethod extends AbstractMethod {

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, String variableName, String methodHandle) throws Exception {
        if (methodHandle.charAt("getMillis".length()) != '(') {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr111", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        int funcStart = "getMillis(".length();
        int funcEnd = analysis.indexOf(methodHandle, ")", funcStart, 2, 2);
        if (funcEnd == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr112", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String parameters = methodHandle.substring(funcStart, funcEnd);
        if (StringUtils.isNotBlank(parameters)) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr113", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        Object value = session.getMethodVariable(variableName);
        if (value == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr127", variableName));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        } else if (value instanceof String) {
            String str = (String) value;
            Date date = Dates.parse(str);
            int millis = Dates.getMillisecond(date);
            this.value = millis;
            int next = funcEnd + 1;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, this.value, next);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            int millis = Dates.getMillisecond(date);
            this.value = millis;
            int next = funcEnd + 1;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, this.value, next);
        } else {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr124", value.getClass().getName(), methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }
    }

}
