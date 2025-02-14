package cn.org.expect.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.script.internal.UniversalScriptVariableImpl;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 脚本引擎工厂类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2018-06-01
 */
@EasyBean(singleton = true)
public class UniversalScriptEngineFactory {

    /** 配置信息 */
    protected volatile UniversalScriptConfiguration configuration;

    /** 容器的上下文信息 */
    protected volatile EasyContext context;

    /**
     * 初始化 <br>
     * 因为脚本引擎使用 SPI 机制读取并创建脚本引擎工厂实例，所以本方法中只做简单操作。
     */
    public UniversalScriptEngineFactory() {
        this.context = DefaultEasyContext.getRoot();
    }

    /**
     * 初始化
     *
     * @param context 容器上下文信息
     */
    public UniversalScriptEngineFactory(EasyContext context) {
        this.context = Ensure.notNull(context);
    }

    /**
     * 返回容器上下文信息
     *
     * @return 容器上下文信息
     */
    public EasyContext getContainer() {
        if (this.context == null) {
            synchronized (this) {
                if (this.context == null) {
                    this.context = new DefaultEasyContext();
                }
            }
        }
        return this.context;
    }

    /**
     * 返回脚本引擎配置信息
     *
     * @return 配置信息
     */
    public UniversalScriptConfiguration getConfiguration() {
        if (this.configuration == null) {
            synchronized (this) {
                if (this.configuration == null) {
                    this.configuration = this.getContainer().getBean(UniversalScriptConfiguration.class);
                }
            }
        }
        return this.configuration;
    }

    public String getEngineName() {
        return this.getConfiguration().getEngineName();
    }

    public String getEngineVersion() {
        return this.getConfiguration().getEngineVersion();
    }

    public String getLanguageName() {
        return this.getConfiguration().getLanguageName();
    }

    public String getLanguageVersion() {
        return this.getConfiguration().getLanguageVersion();
    }

    public String getMethodCallSyntax(String obj, String method, String... args) {
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException();
        }

        StringBuilder buf = new StringBuilder();
        if (StringUtils.isBlank(obj)) {
            for (String str : args) {
                buf.append(' ');
                if (StringUtils.indexOfBlank(str, 0, -1) != -1) {
                    buf.append("\"");
                    buf.append(StringUtils.coalesce(str, ""));
                    buf.append("\"");
                } else {
                    buf.append(StringUtils.coalesce(str, ""));
                }
            }
        } else {
            buf.append(obj).append('.').append(method).append('(');
            for (int i = 0; i < args.length; i++) {
                String str = args[i];

                if (StringUtils.indexOfBlank(str, 0, -1) != -1) {
                    buf.append('\'');
                    buf.append(StringUtils.coalesce(str, ""));
                    buf.append('\'');
                } else {
                    buf.append(StringUtils.coalesce(str, ""));
                }

                if (++i < args.length) {
                    buf.append(", ");
                }
            }
            buf.append(')');
        }
        return buf.toString();
    }

    public List<String> getMimeTypes() {
        List<String> list = new ArrayList<String>();
        StringUtils.split(this.getConfiguration().getMimeTypes(), ',', list);
        return Collections.unmodifiableList(StringUtils.trimBlank(list));
    }

    public List<String> getExtensions() {
        List<String> list = new ArrayList<String>();
        StringUtils.split(this.getConfiguration().getExtensions(), ',', list);
        return Collections.unmodifiableList(StringUtils.trimBlank(list));
    }

    public List<String> getNames() {
        List<String> list = new ArrayList<String>();
        StringUtils.split(this.getConfiguration().getNames(), ',', list);
        return Collections.unmodifiableList(StringUtils.trimBlank(list));
    }

    public String getOutputStatement(String message) {
        return "echo " + StringUtils.ltrimBlank(message);
    }

    public Object getProperty(String key) {
        return this.getConfiguration().getProperty(key);
    }

    public String getProgram(String... statements) {
        char token = this.buildCompiler().getAnalysis().getToken();
        StringBuilder buf = new StringBuilder();
        for (String str : statements) {
            buf.append(str).append(token).append(' ');
        }
        return buf.toString();
    }

    public UniversalScriptEngine getScriptEngine() {
        return new UniversalScriptEngine(this);
    }

    /**
     * 关键字集合
     *
     * @return 关键字集合
     */
    public Set<String> getKeywords() {
        return this.getConfiguration().getKeywords();
    }

    /**
     * 返回用户会话工厂
     *
     * @return 用户会话工厂
     */
    public UniversalScriptSessionFactory buildSessionFactory() {
        String flag = StringUtils.coalesce(this.getConfiguration().getSessionFactory(), "default");
        return this.getContainer().getBean(UniversalScriptSessionFactory.class, flag);
    }

    /**
     * 返回编译器
     *
     * @return 编译器
     */
    public UniversalScriptCompiler buildCompiler() {
        String flag = StringUtils.coalesce(this.getConfiguration().getCompiler(), "default");
        return this.getContainer().getBean(UniversalScriptCompiler.class, flag);
    }

    /**
     * 返回类型转换器
     *
     * @return 类型转换器
     */
    public UniversalScriptFormatter buildFormatter() {
        String flag = StringUtils.coalesce(this.getConfiguration().getConverter(), "default");
        return this.getContainer().getBean(UniversalScriptFormatter.class, flag);
    }

    /**
     * 创建校验器
     *
     * @return 校验器
     */
    public UniversalScriptChecker buildChecker() {
        String flag = StringUtils.coalesce(this.getConfiguration().getChecker(), "default");
        UniversalScriptChecker obj = this.getContainer().getBean(UniversalScriptChecker.class, flag);
        obj.setScriptEngineKeywords(this.getKeywords());
        return obj;
    }

    /**
     * 创建变量域
     */
    public UniversalScriptVariable buildVariable() {
        return new UniversalScriptVariableImpl();
    }
}
