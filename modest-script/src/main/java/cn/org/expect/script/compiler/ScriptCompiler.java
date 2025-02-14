package cn.org.expect.script.compiler;

import java.io.Reader;
import java.util.Comparator;
import java.util.PriorityQueue;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.script.UniversalCommandRepository;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.internal.CommandRepository;
import cn.org.expect.util.Terminator;

/**
 * 编译器
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean(value = "default", description = "即时编译器")
public class ScriptCompiler extends Terminator implements UniversalScriptCompiler {

    /** 脚本命令编译器集合 */
    protected CommandRepository commandRepository;

    /** 缓冲区 */
    protected PriorityQueue<UniversalScriptCommand> cache;

    /** 语句分析器 */
    protected UniversalScriptAnalysis analysis;

    /** 词法分析器 */
    protected ScriptReader reader;

    /** 语法分析器 */
    protected ScriptParser parser;

    /** 起始行数 */
    protected long startLineNumber;

    /** 容器上下文信息 */
    protected EasyContext context;

    /** 读取命令的时间戳 */
    protected long readMillis;

    /**
     * 初始化
     */
    public ScriptCompiler(EasyContext context) {
        this.context = context;
        this.cache = new PriorityQueue<UniversalScriptCommand>(10, new Comparator<UniversalScriptCommand>() {
            public int compare(UniversalScriptCommand o1, UniversalScriptCommand o2) {
                return 0;
            }
        });

        this.analysis = this.context.getBean(UniversalScriptAnalysis.class);
        this.commandRepository = new CommandRepository();
        this.terminate = false;
        this.startLineNumber = 0;
    }

    public UniversalScriptCompiler buildCompiler() {
        ScriptCompiler child = new ScriptCompiler(this.context);
        child.getRepository().setDefault(this.commandRepository.getDefault()); // 设置子编译器的默认命令
        if (this.reader != null) { // 设置起始行数
            child.startLineNumber = this.reader.getLineNumber() - 1; // 设置子编译器其实行数
        }
        return child;
    }

    public void compile(UniversalScriptSession session, UniversalScriptContext context, Reader in) throws Exception {
        this.commandRepository.load(context);
        this.reader = new ScriptReader(in, this.startLineNumber);
        this.parser = new ScriptParser(session, context, this.commandRepository, this.reader);
    }

    public boolean hasNext() throws Exception {
        this.readMillis = System.currentTimeMillis();
        if (this.terminate) {
            this.cache.clear();
            return false;
        } else if (this.cache.isEmpty()) {
            UniversalScriptCommand command = this.parser.read();
            return command != null && this.cache.add(command);
        } else {
            return true;
        }
    }

    public UniversalScriptCommand next() {
        return this.cache.poll();
    }

    public UniversalScriptAnalysis getAnalysis() {
        return this.analysis;
    }

    public UniversalScriptParser getParser() {
        return parser;
    }

    public UniversalCommandRepository getRepository() {
        return this.commandRepository;
    }

    public long getLineNumber() {
        return this.reader == null ? 0 : this.reader.getStartLineNumber();
    }

    public long getCompileMillis() {
        return readMillis;
    }

    public void close() {
        if (this.reader != null) {
            this.reader.close();
        }
        this.cache.clear();
        this.terminate = false;
    }
}
