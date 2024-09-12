package cn.org.expect.script.method;

import java.util.List;

import cn.org.expect.annotation.ScriptFunction;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

@ScriptFunction(name = "split")
public class SplitMethod extends AbstractMethod {

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, String variableName, String methodHandle) throws Exception {
        if (methodHandle.charAt("split".length()) != '(') {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr111", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        int funcStart = "split(".length();
        int funcEnd = analysis.indexOf(methodHandle, ")", funcStart, 2, 2);
        if (funcEnd == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr112", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String params = methodHandle.substring(funcStart, funcEnd); // 'string', 'delimiter', 'escape'
        List<String> parameters = analysis.split(params, analysis.getSegment());

        if (parameters.size() != 0 && parameters.size() != 1 && parameters.size() != 2) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr119", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String delimiter = null, escape = null;

        if (StringUtils.isBlank(params)) {
            parameters.clear();
        } else if (parameters.size() == 1) {
            delimiter = analysis.replaceShellVariable(session, context, parameters.get(0), true, true, true, false);
            if (StringUtils.isBlank(delimiter)) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr120", methodHandle));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        } else if (parameters.size() == 2) {
            delimiter = analysis.replaceShellVariable(session, context, parameters.get(0), true, true, true, false);
            escape = analysis.unQuotation(analysis.replaceShellVariable(session, context, parameters.get(1), false, true, true, false));
            escape = analysis.unescapeString(escape); // 需要对字符串进行反向转义

            if (StringUtils.isBlank(escape) || escape.length() != 1) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr121", methodHandle));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        }

        Object object = session.getMethodVariable(variableName);
        if (object == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr127", variableName));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        if (object instanceof String) {
            String value = (String) object;
            String[] array = null; // string array
            if (parameters.size() == 0) {
                array = StringUtils.splitByBlank(value);
            } else if (escape == null) {
                array = StringUtils.split(value, delimiter);
            } else {
                array = StringUtils.split(value, delimiter, escape.charAt(0));
            }

            this.value = array;
            int next = funcEnd + 1;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, array, next);
        } else {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr124", object.getClass().getName(), methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }
    }

}
