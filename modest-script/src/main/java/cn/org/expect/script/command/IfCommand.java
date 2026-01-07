package cn.org.expect.script.command;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandResultSet;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptExpression;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.LoopCommandKind;
import cn.org.expect.script.command.feature.WithBodyCommandSupported;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * if .. then .. elseif .. then .. else .. fi
 */
public class IfCommand extends AbstractCommand implements LoopCommandKind, WithBodyCommandSupported {
    protected final static Log log = LogFactory.getLog(IfCommand.class);

    /** if 语句的执行条件 */
    protected String ifCondition;

    /** if then ... 语句的执行代码块 */
    protected ArrayList<UniversalScriptCommand> ifCmds;

    /** elseif 语句的执行条件 */
    protected ArrayList<String> elseIfCondition;

    /** elseif then ... 语句的执行代码块 */
    protected ArrayList<List<UniversalScriptCommand>> elseIfCmds;

    /** true表示存在 else ... fi 之间的代码块 */
    protected boolean hasElseCmds;

    /** else ... fi 之间的代码块 */
    protected ArrayList<UniversalScriptCommand> elseCmds;

    /** 正在运行的命令 */
    protected volatile UniversalScriptCommand command;

    /** 种类编号 */
    protected int type;

    public IfCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
        this.ifCmds = new ArrayList<UniversalScriptCommand>();
        this.elseCmds = new ArrayList<UniversalScriptCommand>();
        this.elseIfCondition = new ArrayList<String>();
        this.elseIfCmds = new ArrayList<List<UniversalScriptCommand>>();
        this.hasElseCmds = false;
    }

    /**
     * 添加 if ... then 语句的条件和 then 之后的逻辑代码块
     *
     * @param condition if语句执行条件
     * @param list      if语句执行代码块
     */
    public void setIf(String condition, List<UniversalScriptCommand> list) {
        if (!this.elseIfCondition.isEmpty() || this.hasElseCmds) {
            throw new UniversalScriptException("script.stderr.message079", this.getScript());
        }
        if (StringUtils.isBlank(condition)) {
            throw new UniversalScriptException("script.stderr.message080", this.getScript());
        }

        this.ifCondition = condition;
        this.ifCmds.addAll(list);
        this.setOwner(this.ifCmds);
    }

    /**
     * 添加 elseif 关键字右侧的条件与 then 关键字右侧的逻辑代码块
     *
     * @param condition elseif语句的执行条件
     * @param list      elseif语句的执行代码块
     */
    public void addElseIf(String condition, List<UniversalScriptCommand> list) {
        if (this.ifCondition == null || this.hasElseCmds) {
            throw new UniversalScriptException("script.stderr.message079", this.getScript());
        }
        if (StringUtils.isBlank(condition)) {
            throw new UniversalScriptException("script.stderr.message081", this.getScript());
        }

        this.elseIfCondition.add(condition);
        this.elseIfCmds.add(list == null ? new ArrayList<UniversalScriptCommand>(0) : new ArrayList<UniversalScriptCommand>(list));
        for (int i = 0; i < this.elseIfCmds.size(); i++) {
            List<UniversalScriptCommand> elseifbody = this.elseIfCmds.get(i);
            if (elseifbody != null) {
                this.setOwner(elseifbody);
            }
        }
    }

    /**
     * 返回 else ... fi 语句的逻辑代码块
     *
     * @param list 逻辑代码块
     */
    public void setElse(List<UniversalScriptCommand> list) {
        this.hasElseCmds = true;
        this.elseCmds.addAll(list);
        this.setOwner(this.elseCmds);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout) throws Exception {
        ScriptMainProcess process = session.getMainProcess();
        this.type = 0;

        UniversalScriptExpression expression = new UniversalScriptExpression(session, context, stdout, stderr, this.ifCondition);
        boolean booleanValue = expression.booleanValue();
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message031", "if", this.ifCondition, booleanValue);
        }

        if (booleanValue) {
            for (UniversalScriptCommand command : this.ifCmds) {
                if (session.isTerminate()) {
                    return UniversalScriptCommand.TERMINATE;
                }

                this.command = command;

                UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, forceStdout, command);
                int exitcode = result.getExitcode();
                if (exitcode != 0) {
                    return exitcode;
                }

                if (command instanceof LoopCommandKind) {
                    LoopCommandKind cmd = (LoopCommandKind) command;
                    this.type = cmd.kind();
                    // return exitcode;
                }
            }
            return 0;
        } else {
            // elseif ... then
            List<String> elseIfCondition = this.elseIfCondition;
            for (int i = 0; i < elseIfCondition.size(); i++) {
                 String elseifCondition = elseIfCondition.get(i);
                Boolean booleanValue1 = new UniversalScriptExpression(session, context, stdout, stderr, elseifCondition).booleanValue();

                if (log.isDebugEnabled()) {
                    log.debug("script.stdout.message031", "elseif", elseifCondition, booleanValue1);
                }

                if (booleanValue1) {
                    List<UniversalScriptCommand> list = this.elseIfCmds.get(i);
                    for (UniversalScriptCommand command : list) {
                        if (session.isTerminate()) {
                            return UniversalScriptCommand.TERMINATE;
                        }

                        this.command = command;

                        UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, forceStdout, command);
                        int exitcode = result.getExitcode();
                        if (exitcode != 0) {
                            return exitcode;
                        }

                        if (command instanceof LoopCommandKind) {
                            LoopCommandKind cmd = (LoopCommandKind) command;
                            this.type = cmd.kind();
                            // return exitcode;
                        }
                    }
                    return 0;
                }
            }

            // else ... fi
            for (UniversalScriptCommand command : this.elseCmds) {
                if (session.isTerminate()) {
                    return UniversalScriptCommand.TERMINATE;
                }

                this.command = command;

                UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, forceStdout, command);
                int exitcode = result.getExitcode();
                if (exitcode != 0) {
                    return exitcode;
                }

                if (command instanceof LoopCommandKind) {
                    LoopCommandKind cmd = (LoopCommandKind) command;
                    this.type = cmd.kind();
                    // return exitcode;
                }
            }

            return 0;
        }
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

    /**
     * 校验代码块是否正确
     *
     * @param cmdlist 执行代码块
     */
    private void setOwner(List<UniversalScriptCommand> cmdlist) {
        if (cmdlist != null) {
            for (int i = 0; i < cmdlist.size(); i++) {
                UniversalScriptCommand command = cmdlist.get(i);

                if (command instanceof ReturnCommand) {
                    ReturnCommand rc = (ReturnCommand) command;
                    rc.setOwner(this);
                } else if (command instanceof BreakCommand) {
                    BreakCommand bc = (BreakCommand) command;
                    bc.setOwner(this);
                } else if (command instanceof ContinueCommand) {
                    ContinueCommand cc = (ContinueCommand) command;
                    cc.setOwner(this);
                }
            }
        }
    }

    public String toString() {
        return this.toString(1);
    }

    /**
     * 返回 if 语句表达式
     *
     * @param level 指定语句块中代码锁进宽度
     * @return 语句
     */
    public String toString(int level) {
        String tab = StringUtils.left("", level * 2, ' '); // tab 字符的长度
        String str = "";

        // if ... then
        str += tab + "if " + StringUtils.trimBlank(this.ifCondition) + " then" + Settings.getLineSeparator();
        for (Object obj : this.ifCmds) {
            if (obj instanceof IfCommand) {
                str += ((IfCommand) obj).toString(level + 1) + Settings.getLineSeparator();
            } else {
                str += tab + tab + StringUtils.ltrimBlank(obj.toString()) + Settings.getLineSeparator();
            }
        }

        // elseif
        for (int i = 0; i < this.elseIfCondition.size(); i++) {
            Object condition = this.elseIfCondition.get(i);
            str += tab + "elseif " + StringUtils.trimBlank(condition.toString()) + " then" + Settings.getLineSeparator();
            List<UniversalScriptCommand> cmds = this.elseIfCmds.get(i);
            for (UniversalScriptCommand obj : cmds) {
                if (obj instanceof IfCommand) {
                    str += ((IfCommand) obj).toString(level + 1) + Settings.getLineSeparator();
                } else {
                    str += tab + tab + StringUtils.ltrimBlank(obj.toString()) + Settings.getLineSeparator();
                }
            }
        }

        // else
        if (!this.elseCmds.isEmpty()) {
            str += tab + "else" + Settings.getLineSeparator();
            for (Object obj : this.elseCmds) {
                if (obj instanceof IfCommand) {
                    str += ((IfCommand) obj).toString(level + 1) + Settings.getLineSeparator();
                } else {
                    str += tab + tab + StringUtils.ltrimBlank(obj.toString()) + Settings.getLineSeparator();
                }
            }
        }
        str += tab + "fi";
        return str;
    }
}
