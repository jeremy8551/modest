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

@ScriptFunction(name = "substr")
public class SubstrMethod extends AbstractMethod {

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, String variableName, String methodHandle) throws Exception {
        if (methodHandle.charAt("substr".length()) != '(') {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr111", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        int funcStart = "substr(".length();
        int funcEnd = analysis.indexOf(methodHandle, ")", funcStart, 2, 2);
        if (funcEnd == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr112", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        // parse function parameters
        String params = methodHandle.substring(funcStart, funcEnd);
        List<String> parameters = analysis.split(params, analysis.getSegment());

        if (parameters.size() != 1 && parameters.size() != 2) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr118", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        Object value = session.getMethodVariable(variableName);
        if (value == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr127", variableName));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        } else if (value instanceof String) {
            String str = (String) value;

            int begin = -1;
            if ((begin = StringUtils.parseInt(analysis.replaceShellVariable(session, context, parameters.get(0), true, true, false, false), -1)) == -1 || begin > str.length()) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr123", methodHandle));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }

            int end = -1;
            if (parameters.size() == 2 //
                    && ((end = StringUtils.parseInt(analysis.replaceShellVariable(session, context, parameters.get(1), true, true, false, false), -1)) == -1 || end < begin || end > str.length()) //
            ) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr123", methodHandle));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }

            if (end == -1) {
                this.value = str.substring(begin);
            } else {
                this.value = str.substring(begin, end);
            }

            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, this.value, funcEnd + 1);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;

            int begin = -1;
            if ((begin = StringUtils.parseInt(parameters.get(0), -1)) == -1 || begin > array.length) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr123", methodHandle));
                return 4;
            }

            int end = array.length;
            if (parameters.size() == 2 //
                    && ((end = StringUtils.parseInt(parameters.get(1), -1)) == -1 || end < begin || end > array.length) //
            ) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr123", methodHandle));
                return 5;
            }

            int length = end - begin; // calc new array length
            Object[] newarray = new Object[length];
            System.arraycopy(array, begin, newarray, 0, length);
            this.value = newarray;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, newarray, funcEnd + 1);
        } else {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr124", value.getClass().getName(), methodHandle));
            return 9;
        }
    }

}
