package cn.org.expect.script.method.array;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptFormatter;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;

/**
 * 打印数组
 */
@EasyVariableMethod(name = "print", variable = Object[].class)
public class PrintMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        UniversalScriptFormatter formatter = context.getEngine().getFormatter();
        Object[] array = (Object[]) variable;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < array.length; ) {
            buf.append(formatter.format(array[i]));
            if (++i < array.length) {
                buf.append(' ');
            }
        }

        if (session.isEchoEnable()) {
            stdout.println(buf);
        }
        return buf.toString();
    }
}
