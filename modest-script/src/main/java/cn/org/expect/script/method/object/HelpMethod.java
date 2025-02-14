package cn.org.expect.script.method.object;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariableMethod;
import cn.org.expect.script.UniversalScriptVariableMethodParameters;
import cn.org.expect.script.annotation.EasyVariableMethod;
import cn.org.expect.script.method.VariableMethodRepository;

/**
 * 打印所有变量方法
 */
@EasyVariableMethod(name = "help", variable = Object.class)
public class HelpMethod implements UniversalScriptVariableMethod {

    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        VariableMethodRepository repository = context.getContainer().getBean(VariableMethodRepository.class);
        if (session.isEchoEnable()) {
            stdout.println(repository.toStandardString());
        }
        return repository;
    }
}
