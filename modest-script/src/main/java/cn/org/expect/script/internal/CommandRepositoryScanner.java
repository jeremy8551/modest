package cn.org.expect.script.internal;

import java.util.Collections;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanEntryCollection;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandRepository;
import cn.org.expect.script.UniversalScriptCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptContextAware;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 脚本引擎命令类扫描器 <br>
 * 用于扫描当前JVM 中所有可用的脚本引擎命令类信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-09
 */
public class CommandRepositoryScanner {
    private final static Log log = LogFactory.getLog(CommandRepositoryScanner.class);

    /** 脚本引擎命令仓库 */
    private final UniversalCommandRepository repository;

    /**
     * 初始化
     *
     * @param repository 命令仓库
     */
    public CommandRepositoryScanner(UniversalCommandRepository repository) {
        this.repository = Ensure.notNull(repository);
    }

    /**
     * 加载脚本引擎命令
     *
     * @param context 脚本引擎上下文信息
     */
    public void load(UniversalScriptContext context) {
        EasyContext ioc = context.getContainer();
        EasyBeanEntryCollection collection = ioc.getBeanEntryCollection(UniversalCommandCompiler.class);
        for (EasyBeanEntry entry : collection.values()) {
            Class<? extends UniversalCommandCompiler> type = entry.getType();
            try {
                if (this.load(context, type)) {
                    if (log.isDebugEnabled()) {
                        log.debug("script.stdout.message045", type.getName());
                    }
                }
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("script.stderr.message139", type.getName(), e);
                }
            }
        }
    }

    /**
     * 加载脚本命令
     *
     * @param type 脚本引擎命令的Class信息
     * @return 返回true表示加载成功，false表示加载失败
     */
    public boolean load(UniversalScriptContext context, Class<? extends UniversalCommandCompiler> type) {
        if (this.repository.contains(type)) {
            return false;
        }

        if (type.isAnnotationPresent(EasyCommandCompiler.class)) {
            EasyCommandCompiler anno = type.getAnnotation(EasyCommandCompiler.class);
            String[] names = StringUtils.trimBlank(anno.name());
            String[] words = StringUtils.trimBlank(anno.keywords());

            if (StringUtils.isBlank(names)) {
                if (log.isWarnEnabled()) {
                    log.warn("script.stderr.message043", type.getName(), EasyCommandCompiler.class.getName(), "name()");
                }
                return false;
            }

            UniversalCommandCompiler compiler;
            try {
                compiler = context.getContainer().newInstance(type);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("class.stdout.message003", type.getName(), e);
                }
                return false;
            }

            // 向编译器中注入脚本引擎上下文信息
            if (compiler instanceof UniversalScriptContextAware) {
                ((UniversalScriptContextAware) compiler).setContext(context);
            }

            this.repository.add(names, compiler);

            // 关键字
            Collections.addAll(context.getEngine().getFactory().getKeywords(), words);
            return true;
        } else {
            if (log.isWarnEnabled()) {
                log.warn("script.stderr.message046", type.getName(), UniversalScriptCommand.class.getName(), EasyCommandCompiler.class.getName());
            }
            return false;
        }
    }
}
