package cn.org.expect.script.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptJob;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.StringUtils;

@EasyCommandCompiler(name = "container")
public class ContainerCommandCompiler extends AbstractCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readPieceofScript("begin", "end");
    }

    public UniversalScriptCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("container");
        it.assertNext("to");
        it.assertNext("execute");
        it.assertNext("tasks");
        it.assertNext("in");
        it.assertNext("parallel");

        Map<String, String> attributes = new CaseSensitivMap<String>();
        if (it.isNext("using")) {
            it.assertNext("using");
            String parameter = it.readUntil("begin");
            List<String> list = new ArrayList<String>();
            analysis.split(parameter, list); // 解析属性信息
            for (String property : list) {
                String[] array = StringUtils.splitProperty(property);
                if (array == null) {
                    attributes.put(property, ""); // 无值参数
                } else {
                    attributes.put(array[0], array[1]);
                }
            }
        } else {
            it.assertNext("begin");
        }

        it.assertLast("end");
        String body = it.readOther();
        List<UniversalScriptCommand> cmdlist = parser.read(body);
        for (UniversalScriptCommand cmd : cmdlist) {
            if (!(cmd instanceof UniversalScriptJob)) {
                throw new UniversalScriptException("script.stderr.message027", command, cmd.getScript());
            }
        }
        return new ContainerCommand(this, command, attributes, cmdlist);
    }
}
