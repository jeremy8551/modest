package cn.org.expect.script.internal;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.BreakCommand;
import cn.org.expect.script.command.ContinueCommand;
import cn.org.expect.script.command.ExitCommand;
import cn.org.expect.script.command.ReturnCommand;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;

/**
 * 脚本命令回调函数实现类
 *
 * @author jeremy8551@gmail.com
 */
public class Callback {

    /** 回调函数集合 */
    protected final List<CommandList> list;

    /** 正在运行的脚本命令 */
    protected UniversalScriptCommand command;

    public Callback() {
        super();
        this.list = new ArrayList<CommandList>();
    }

    /**
     * 执行用户自定义方法
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标注信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param args        自定义方法的参数, 第一个值是方法名, 从第二个值开始是方法参数
     * @return 0表示执行成功
     * @throws Exception 发生错误
     */
    public int executeCallback(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, String[] args) throws Exception {
        for (int i = 0; i < this.list.size(); i++) {
            CommandList cmd = this.list.get(i);
            int value = this.execute(session, context, stdout, stderr, forceStdout, cmd, args);
            if (value != 0) {
                return i + 1; // 如果钩子函数执行错误，则返回钩子函数的位置信息
            }
        }
        return 0;
    }

    protected int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, CommandList body, String[] args) throws Exception {
        try {
            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            }

            session.setFunctionParameter(args);
            for (int i = 0; !session.isTerminate() && i < body.size(); i++) {
                UniversalScriptCommand command = body.get(i);
                if (command == null) {
                    continue;
                } else {
                    this.command = command;
                }

                int exitcode = command.execute(session, context, stdout, stderr, forceStdout);
                if (exitcode != 0) {
                    return exitcode;
                }

                if (command instanceof LoopCommandKind) {
                    LoopCommandKind cmd = (LoopCommandKind) command;
                    int type = cmd.kind();
                    if (type == ExitCommand.KIND) { // Exit script
                        return exitcode;
                    } else if (type == ReturnCommand.KIND) { // Exit method
                        return exitcode;
                    } else if (type == BreakCommand.KIND) { // break
                        throw new UnsupportedOperationException(ResourcesUtils.getMessage("script.stderr.message028", this.command));
                    } else if (type == ContinueCommand.KIND) { // continue
                        throw new UnsupportedOperationException(ResourcesUtils.getMessage("script.stderr.message029", this.command));
                    }
                }
            }

            if (session.isTerminate()) {
                return UniversalScriptCommand.TERMINATE;
            } else {
                return 0;
            }
        } finally {
            session.removeFunctionParameter();
            this.command = null;
        }
    }

    /**
     * 添加一个回调函数
     *
     * @param callback 回调函数信息
     */
    public void add(CommandList callback) {
        this.list.add(Ensure.notNull(callback));
    }

    /**
     * 添加一个回调函数到指定位置上
     *
     * @param index    位置信息，从 0 开始
     * @param callback 回调函数信息
     */
    public void add(int index, CommandList callback) {
        this.list.add(Ensure.fromZero(index), Ensure.notNull(callback));
    }

    /**
     * 删除所有回调函数
     *
     * @return 命令集合
     */
    public List<CommandList> removeAll() {
        List<CommandList> list = new ArrayList<CommandList>(this.list);
        this.list.clear();
        return list;
    }

    public Callback clone() {
        Callback obj = new Callback();
        for (CommandList cl : this.list) {
            obj.list.add(cl.clone());
        }
        return obj;
    }
}
