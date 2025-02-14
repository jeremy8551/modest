package cn.org.expect.script.method.string;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;

/**
 * 返回字符串中指定位置上的字符
 *
 * @author jeremy8551@gmail.com
 */
@EasyVariableMethod(name = "[", variable = CharSequence.class, parameters = {int.class})
public class CharAtMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        CharSequence str = (CharSequence) variable;
        int index = parameters.getInt(0);
        return String.valueOf(str.charAt(index));
    }
}
