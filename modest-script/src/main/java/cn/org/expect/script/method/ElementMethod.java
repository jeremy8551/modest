package cn.org.expect.script.method;

import cn.org.expect.annotation.ScriptFunction;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * variableName[index]
 *
 * @author jeremy8551@qq.com
 */
@ScriptFunction(name = "[")
public class ElementMethod extends AbstractMethod {

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, String variableName, String methodHandle) throws Exception {
        int end = analysis.indexOf(methodHandle, "]", 1, 2, 2);
        if (end == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr115", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        int index = -1;
        if ((index = StringUtils.parseInt(analysis.replaceShellVariable(session, context, methodHandle.substring(1, end), true, true, false, false), -1)) == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr116", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        Object object = session.getMethodVariable(variableName);
        if (object == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr127", variableName));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        } else if (object instanceof String) {
            String value = (String) object;

            int next = end + 1;
            if (index >= 0 && index < value.length()) {
                this.value = String.valueOf(value.charAt(index));
                return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, this.value, next);
            } else {
                this.value = null;
                stderr.println(ResourcesUtils.getMessage("script.message.stderr117", methodHandle, index));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;

            int next = end + 1;
            if (index >= 0 && index < array.length) {
                this.value = array[index];
                return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, this.value, next);
            } else {
                this.value = null;
                stderr.println(ResourcesUtils.getMessage("script.message.stderr117", methodHandle, index));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        } else {
            this.value = null;
            stderr.println(ResourcesUtils.getMessage("script.message.stderr124", object.getClass().getName(), methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }
    }

}
