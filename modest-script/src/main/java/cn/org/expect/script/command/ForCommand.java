package cn.org.expect.script.command;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.org.expect.expression.Expression;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.internal.CommandList;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.util.StringUtils;

/**
 * for语句: <br>
 * <br>
 * for i in `ls` <br>
 * loop <br>
 * .. <br>
 * end loop <br>
 * <br>
 * <br>
 * for i in ${var} <br>
 * loop <br>
 * .. <br>
 * end loop <br>
 * <br>
 * <br>
 * for i in (1,2,3,4) <br>
 * loop <br>
 * .. <br>
 * end loop <br>
 *
 * @author jeremy8551@gmail.com
 */
public class ForCommand extends AbstractCommand implements WithBodyCommandSupported, LoopCommandKind {

    /** 变量名 */
    protected String name;

    /** 变量集合 */
    protected String collection;

    /** for循环体 */
    protected CommandList body;

    /** 正在运行的脚本命令 */
    protected volatile UniversalScriptCommand command;

    /** 种类编号 */
    protected int type;

    public ForCommand(UniversalCommandCompiler compiler, String command, String name, String collection, CommandList body) {
        super(compiler, command);
        this.name = name;
        this.collection = collection;
        this.body = body;
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        String variableName = this.name; // 变量名
        List<Object> list = this.toCollection(session, context, this.collection);  // 集合

        boolean exists = context.containsVariable(variableName);
        Object oldValue = context.getVariable(variableName);
        int scope = context.getVariableScope(variableName);
        try {
            ScriptMainProcess process = session.getMainProcess();
            boolean isbreak = false, iscontinue;
            for (Iterator<Object> it = list.iterator(); !session.isTerminate() && it.hasNext(); ) {
                iscontinue = false;
                Object element = it.next();
                context.addLocalVariable(variableName, element);

                // 遍历所有命令
                for (int i = 0; !session.isTerminate() && i < this.body.size(); i++) {
                    UniversalScriptCommand command = this.body.get(i);
                    this.command = command;
                    if (command == null) {
                        continue;
                    }

                    UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, forceStdout, command);
                    int value = result.getExitcode();
                    if (value != 0) {
                        return value;
                    }

                    if (command instanceof LoopCommandKind) {
                        LoopCommandKind cmd = (LoopCommandKind) command;
                        int type = cmd.kind();
                        this.type = cmd.kind();
                        if (type == LoopCommandKind.BREAK_COMMAND) { // break
                            isbreak = true;
                            break;
                        } else if (type == LoopCommandKind.CONTINUE_COMMAND) { // continue
                            iscontinue = true;
                            break;
                        } else if (type == LoopCommandKind.EXIT_COMMAND) { // Exit script
                            return value;
                        } else if (type == LoopCommandKind.RETURN_COMMAND) { // Exit the result set loop
                            return value;
                        }
                    }
                }

                if (isbreak) {
                    break;
                }

                if (iscontinue) {
                    continue;
                }
            }

            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            } else {
                return 0;
            }
        } finally {
            this.command = null;
            if (exists) {
                context.addVariable(variableName, oldValue, scope);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<Object> toCollection(UniversalScriptSession session, UniversalScriptContext context, String collection) {
        List<Object> list = new ArrayList<Object>();

        // 变量的名字
        String variableName;
        List<String> variableNames = StringUtils.splitVariable(collection, new ArrayList<String>());
        if (variableNames.size() == 1) {
            variableName = variableNames.get(0); // ${name}
        } else {
            variableName = collection; // name
        }

        // 变量
        if (context.containsVariable(variableName)) {
            Object variable = context.getVariable(variableName);

            // 数组
            if (variable.getClass().isArray()) {
                int length = Array.getLength(variable);
                for (int i = 0; i < length; i++) {
                    list.add(Array.get(variable, i));
                }
                return list;
            }

            // 遍历接口
            if (variable instanceof Iterable) {
                for (Iterator<Object> it = ((Iterable<Object>) variable).iterator(); it.hasNext(); ) {
                    list.add(it.next());
                }
                return list;
            }

            // 集合
            if (variable instanceof Map) {
                list.addAll(((Map) variable).values());
                return list;
            }

            throw new UniversalScriptException(variable.getClass().getName() + " not supported for loop");
        }

        // 字符串常量: (1,2,3,4)
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String value = analysis.replaceShellVariable(session, context, collection, true, true);
        String str = analysis.trim(analysis.removeSide(value, '(', ')'), 0, 0);
        List<String> strList = new ArrayList<String>();
        analysis.split(str, strList, analysis.getSegment());
        for (String element : strList) {
            list.add(new Expression(element).value());
        }
        return list;
    }

    public void terminate() throws Exception {
        super.terminate();
        if (this.command != null) {
            this.command.terminate();
        }
    }

    public int kind() {
        return this.type;
    }
}
