package cn.org.expect.script.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandCompilerResult;
import cn.org.expect.script.UniversalCommandRepository;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptConfiguration;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.util.StringUtils;

/**
 * 脚本命令仓库
 *
 * @author jeremy8551@gmail.com
 */
public class CommandRepository implements UniversalCommandRepository {
    protected final static Log log = LogFactory.getLog(CommandRepository.class);

    /** 所有命令编译器 */
    private final List<CommandCompilerContext> all;

    /** 优先级为 1 的命令编译器 */
    private final List<CommandCompilerContext> rule1;

    /** 优先级为 2 的命令编译器，集合中是命令名与脚本命令配置信息的映射关系 */
    private final Map<String, CommandCompilerContext> rule2;

    /** 优先级为 3 的命令编译器，集合中是命令名与脚本命令集合的映射关系，即一个命令名对应多个命令的情况 */
    private final Map<String, List<CommandCompilerContext>> rule3;

    /** 优先级为 4 的命令编译器 */
    private final List<CommandCompilerContext> rule4;

    /** 优先级为 5 的命令编译器，脚本引擎默认命令编译器 */
    private UniversalCommandCompiler defaultCompiler;

    /** 识别脚本语句时使用的缓冲区 */
    private final List<UniversalCommandCompiler> cache;

    /**
     * 初始化
     */
    public CommandRepository() {
        this.all = new ArrayList<CommandCompilerContext>();
        this.rule1 = new ArrayList<CommandCompilerContext>();
        this.rule2 = new HashMap<String, CommandCompilerContext>();
        this.rule3 = new HashMap<String, List<CommandCompilerContext>>();
        this.rule4 = new ArrayList<CommandCompilerContext>();
        this.cache = new ArrayList<UniversalCommandCompiler>();
    }

    public void load(UniversalScriptContext context) {
        this.clear();
        new CommandRepositoryScanner(this).load(context);

        // 设置脚本引擎默认命令
        if (this.getDefault() == null) {
            EasyContext ioc = context.getContainer();
            String script = ioc.getBean(UniversalScriptConfiguration.class).getDefaultCommand(); // 默认脚本语句
            if (StringUtils.isNotBlank(script)) {
                UniversalScriptAnalysis analysis = ioc.getBean(UniversalScriptAnalysis.class);
                UniversalCommandCompiler compiler = this.get(analysis, script);
                this.setDefault(compiler);
            }
        }
    }

    public void setDefault(UniversalCommandCompiler compiler) {
        this.defaultCompiler = compiler;
    }

    public UniversalCommandCompiler getDefault() {
        return this.defaultCompiler;
    }

    public boolean contains(Class<? extends UniversalCommandCompiler> type) {
        if (type != null) {
            for (CommandCompilerContext context : this.all) {
                if (context.equalsClass(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clear() {
        this.all.clear();
        this.rule1.clear();
        this.rule2.clear();
        this.rule3.clear();
        this.rule4.clear();
    }

    public void add(String[] names, UniversalCommandCompiler compiler) {
        Class<? extends UniversalCommandCompiler> type = compiler.getClass();
        if (this.contains(type)) { // 校验是否重复添加
            throw new UniversalScriptException("script.stderr.message048", type.getName());
        }

        CommandCompilerContext context = new CommandCompilerContext(compiler);
        for (String name : names) {
            this.add(name, context);
        }
        this.all.add(context);
    }

    /**
     * 添加脚本命令的注解配置信息
     *
     * @param name    脚本命令前缀
     * @param context 脚本命令配置信息
     */
    private void add(String name, CommandCompilerContext context) {
        String key = name.toUpperCase();

        // 添加 1 级匹配规则
        if ("^".equals(key)) {
            if (!this.rule1.contains(context)) {
                this.rule1.add(context);
            }
            return;
        }

        // 添加 4 级匹配规则
        if ("*".equals(key)) {
            if (!this.rule4.contains(context)) {
                this.rule4.add(context);
            }
            return;
        }

        if (this.rule2.containsKey(key)) {
            List<CommandCompilerContext> list = this.rule3.get(key);
            if (list == null) {
                list = new ArrayList<CommandCompilerContext>();
            }

            CommandCompilerContext old = this.rule2.remove(key);
            if (old != null) {
                list.add(old);
            }
            list.add(context);
            this.rule3.put(key, list);
            return;
        }

        if (this.rule3.containsKey(key)) {
            List<CommandCompilerContext> list = this.rule3.get(key);
            if (list == null) {
                list = new ArrayList<CommandCompilerContext>();
            }

            list.add(context);
            this.rule3.put(key, list);
            return;
        }

        this.rule2.put(key, context);
    }

    @SuppressWarnings("unchecked")
    public <E extends UniversalCommandCompiler> E get(Class<E> type) {
        for (CommandCompilerContext context : this.all) {
            if (context.equalsClass(type)) {
                return (E) context.getCompiler();
            }
        }
        return null;
    }

    public synchronized UniversalCommandCompiler get(UniversalScriptAnalysis analysis, String script) {
        this.cache.clear();

        // 截取命令前缀
        String commandPrefix = analysis.getPrefix(script);

        // 尝试匹配 ^ 命令
        for (CommandCompilerContext context : this.rule1) {
            UniversalCommandCompilerResult code = context.getCompiler().match(analysis, commandPrefix, script);
            switch (code) {
                case NEUTRAL:
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message022", commandPrefix, context.getCompiler().getClass().getName(), script);
                    }
                    this.cache.add(context.getCompiler());
                    break;

                case ACCEPT:
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message023", commandPrefix, context.getCompiler().getClass().getName(), script);
                    }
                    return context.getCompiler();

                case IGNORE:
                    continue;

                case DENY:
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message024", commandPrefix, context.getCompiler().getClass().getName(), script, this.defaultCompiler.getClass().getName());
                    }
                    return this.defaultCompiler;

                default:
                    throw new UnsupportedOperationException(String.valueOf(code));
            }
        }

        // 匹配到一个命令
        if (this.cache.size() == 1) {
            return this.cache.get(0);
        }

        // 匹配到多个命令
        if (this.cache.size() > 1) {
            throw new UniversalScriptException("script.stderr.message045", script, this.cache.size(), StringUtils.join(this.cache, ", "));
        }

        // 优先根据命令的名称查找对应的编译器
        String key = commandPrefix.toUpperCase();
        CommandCompilerContext cxt = this.rule2.get(key);
        if (cxt != null) {
            return cxt.getCompiler();
        }

        // 查找相同名字的命令
        List<CommandCompilerContext> list = this.rule3.get(key);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                CommandCompilerContext context = list.get(i);
                UniversalCommandCompilerResult code = context.getCompiler().match(analysis, commandPrefix, script);
                switch (code) {
                    case NEUTRAL:
                        if (log.isDebugEnabled()) {
                            log.debug("script.stdout.message022", commandPrefix, context.getCompiler().getClass().getName(), script);
                        }
                        this.cache.add(context.getCompiler());
                        break;

                    case ACCEPT:
                        if (log.isDebugEnabled()) {
                            log.debug("script.stdout.message023", commandPrefix, context.getCompiler().getClass().getName(), script);
                        }
                        return context.getCompiler();

                    case IGNORE:
                        continue;

                    case DENY:
                        if (log.isDebugEnabled()) {
                            log.debug("script.stdout.message024", commandPrefix, context.getCompiler().getClass().getName(), script, this.defaultCompiler.getClass().getName());
                        }
                        return this.defaultCompiler;

                    default:
                        throw new UnsupportedOperationException(String.valueOf(code));
                }
            }
        }

        // 匹配到一个命令
        if (this.cache.size() == 1) {
            return this.cache.get(0);
        }

        // 匹配到多个命令
        if (this.cache.size() > 1) {
            throw new UniversalScriptException("script.stderr.message045", script, this.cache.size(), StringUtils.join(this.cache, ", "));
        }

        // 尝试匹配 * 命令
        for (CommandCompilerContext context : this.rule4) {
            UniversalCommandCompilerResult code = context.getCompiler().match(analysis, commandPrefix, script);
            switch (code) {
                case NEUTRAL:
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message022", commandPrefix, context.getCompiler().getClass().getName(), script);
                    }
                    this.cache.add(context.getCompiler());
                    break;

                case ACCEPT:
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message023", commandPrefix, context.getCompiler().getClass().getName(), script);
                    }
                    return context.getCompiler();

                case IGNORE:
                    continue;

                case DENY:
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message024", commandPrefix, context.getCompiler().getClass().getName(), script, this.defaultCompiler.getClass().getName());
                    }
                    return this.defaultCompiler;

                default:
                    throw new UnsupportedOperationException(String.valueOf(code));
            }
        }

        // 匹配到一个命令
        if (this.cache.size() == 1) {
            return this.cache.get(0);
        }

        // 匹配到多个命令
        if (this.cache.size() > 1) {
            throw new UniversalScriptException("script.stderr.message045", script, this.cache.size(), StringUtils.join(this.cache, ", "));
        }

        // 匹配到零个命令
        if (log.isDebugEnabled()) {
            log.debug("script.stdout.message025", commandPrefix, script, this.defaultCompiler.getClass().getName());
        }
        return this.defaultCompiler; // 默认命令
    }

    public boolean isEmpty() {
        return this.all.isEmpty();
    }
}
