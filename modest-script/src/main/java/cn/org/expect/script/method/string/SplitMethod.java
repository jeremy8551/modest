package cn.org.expect.script.method.string;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.util.StringUtils;

/**
 * 使用分隔符、转义字符提取字符串中的字段
 */
@EasyVariableMethod(name = "split", variable = CharSequence.class, parameters = {String.class, String.class}, parameterNote = {"字符串"})
public class SplitMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        String escape = analysis.unescapeString(parameters.getString(1));
        if (StringUtils.isBlank(escape) || escape.length() != 1) {
            throw new IllegalArgumentException("escape: " + parameters.getString(1));
        } else {
            return StringUtils.split((CharSequence) variable, parameters.getString(0), escape.charAt(0));
        }
    }
}
