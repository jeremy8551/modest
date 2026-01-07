package cn.org.expect.script.command;

import java.io.IOException;

import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "wait")
public class WaitCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws IOException {
        WordIterator it = analysis.parse(command);
        it.assertNext("wait");
        String pidExpression = it.next();
        String waitTime = analysis.trim(it.readOther(), 0, 1);

        String[] array = StringUtils.splitPropertyForce(pidExpression);
        Ensure.equals("pid", array[0]);
        return new WaitCommand(this, orginalScript, array[1], waitTime);
    }
}
