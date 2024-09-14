package cn.org.expect.script.method;

import java.util.Date;

import cn.org.expect.annotation.ScriptFunction;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.Dates;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

@ScriptFunction(name = "format")
public class FormatMethod extends AbstractMethod {

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, String variableName, String methodHandle) throws Exception {
        if (methodHandle.charAt("format".length()) != '(') {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr111", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        int begin = "format(".length();
        int end = analysis.indexOf(methodHandle, ")", begin, 2, 2);
        if (end == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr112", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String parameters = methodHandle.substring(begin, end);
        if (StringUtils.isBlank(parameters)) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr097", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String pattern = analysis.unQuotation(analysis.trim(parameters, 0, 0));
        if (StringUtils.isBlank(pattern)) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr097", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        Object value = session.getMethodVariable(variableName);
        if (value == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr127", variableName));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        Date date = Dates.testParse(value);
        if (date == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr124", value.getClass().getName(), methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        } else {
            this.value = Dates.format(date, pattern);
            int next = end + 1;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, this.value, next);
        }
    }

}
