package cn.org.expect.script.method;

import java.util.List;

import cn.org.expect.script.annotation.ScriptFunction;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * indexOf('str', from) <br>
 * indexOf('str')
 *
 * @author jeremy8551@qq.com
 */
@ScriptFunction(name = "indexOf")
public class IndexOfMethod extends AbstractMethod {

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, String variableName, String methodHandle) throws Exception {
        if (methodHandle.charAt("indexOf".length()) != '(') {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr111", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        int funcStart = "indexOf(".length();
        int funcEnd = analysis.indexOf(methodHandle, ")", funcStart, 2, 2);
        if (funcEnd == -1) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr112", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String params = methodHandle.substring(funcStart, funcEnd); // 'string', 'delimiter', 'escape'
        List<String> parameters = analysis.split(params, analysis.getSegment());

        if (parameters.size() != 1 && parameters.size() != 2) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr110", methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }

        String str = null;
        int from = -1;

        if (parameters.size() == 1) {
            str = analysis.replaceShellVariable(session, context, parameters.get(0), true, true, true, false);
            if (str.length() == 0) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr129", methodHandle));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        } else if (parameters.size() == 2) {
            str = analysis.replaceShellVariable(session, context, parameters.get(0), true, true, true, false);
            from = StringUtils.parseInt(analysis.replaceShellVariable(session, context, parameters.get(1), true, true, true, false), -1);
            if (from < 0) {
                stderr.println(ResourcesUtils.getMessage("script.message.stderr123", methodHandle));
                return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
            }
        }

        Object object = session.getMethodVariable(variableName);
        if (object == null) {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr127", variableName));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        } else if (object instanceof String) {
            String value = (String) object;

            int index = -1;
            if (from >= value.length()) {
                index = -1;
            } else if (from == -1) {
                index = value.indexOf(str);
            } else {
                index = value.indexOf(str, from);
            }

            this.value = index;
            int next = funcEnd + 1;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, index, next);
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;

            int index = -1;
            if (from >= array.length) {
                index = -1;
            } else {
                for (int i = (from == -1 ? 0 : from); i < array.length; i++) {
                    Object obj = array[i];
                    if (str.equals(context.getEngine().getFormatter().format(obj))) {
                        index = i;
                        break;
                    }
                }
            }

            this.value = index;
            int next = funcEnd + 1;
            return this.executeNextMethod(session, context, stdout, stderr, analysis, variableName, methodHandle, index, next);
        } else {
            stderr.println(ResourcesUtils.getMessage("script.message.stderr124", object.getClass().getName(), methodHandle));
            return UniversalScriptCommand.VARIABLE_METHOD_ERROR;
        }
    }

}
