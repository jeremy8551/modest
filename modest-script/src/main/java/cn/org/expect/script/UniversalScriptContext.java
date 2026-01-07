package cn.org.expect.script;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.internal.ScriptCatalog;
import cn.org.expect.script.internal.ScriptListenerList;
import cn.org.expect.script.internal.ScriptProgram;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 脚本引擎上下文信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2018-06-01
 */
public class UniversalScriptContext {

    /** 局部变量域（只能在当前脚本引擎内访问） */
    public final static int ENGINE_SCOPE = 100;

    /** 全局变量域（可以在当前脚本及其子脚本中访问） */
    public final static int GLOBAL_SCOPE = 200;

    /** 环境变量（可以在当前脚本及其子脚本中访问） */
    public final static int ENVIRONMENT_SCOPE = 300;

    /** 容器上下文信息 */
    protected final EasyContext ioc;

    /** 父脚本引擎的上下文信息 */
    protected UniversalScriptContext parent;

    /** 归属的脚本引擎 */
    protected final UniversalScriptEngine engine;

    /** 命令监听器集合 */
    protected final UniversalScriptListenerList listeners;

    /** 全局变量集合 */
    protected final UniversalScriptVariable globalVariable;

    /** 局部变量集合 */
    protected final UniversalScriptVariable localVariable;

    /** 外部的环境变量集合（不可修改内容） */
    protected UniversalScriptVariable environmentVariable;

    /** 全局数据库编目集合（可以在当前脚本引擎及其子脚本引擎中访问） */
    protected final ScriptCatalog globalCatalog;

    /** 局部数据库编目集合（只能在当前脚本引擎中使用） */
    protected final ScriptCatalog localCatalog;

    /** 用于保存用户自定义数据或程序 */
    protected final ScriptProgram globalPrograms;

    /** 用于保存用户自定义数据或程序 */
    protected final ScriptProgram localPrograms;

    /**
     * 初始化
     *
     * @param engine 当前脚本引擎上下文信息归属的脚本引擎
     */
    public UniversalScriptContext(UniversalScriptEngine engine) {
        this.engine = Ensure.notNull(engine);
        this.listeners = new ScriptListenerList();
        this.ioc = engine.getFactory().getContainer();
        this.globalVariable = engine.getFactory().buildVariable();
        this.localVariable = engine.getFactory().buildVariable();
        this.environmentVariable = engine.getFactory().buildVariable();
        this.environmentVariable.put(UniversalScriptVariable.SESSION_VARNAME_THIS, engine);
        this.globalCatalog = new ScriptCatalog();
        this.localCatalog = new ScriptCatalog();
        this.globalPrograms = new ScriptProgram();
        this.localPrograms = new ScriptProgram();
    }

    /**
     * 保存父脚本引擎上下文信息
     *
     * @param context 父脚本引擎上下文信息
     */
    public void setParent(UniversalScriptContext context) {
        this.parent = Ensure.notNull(context);

        // 复制监听器
        if (context.listeners != null) {
            this.listeners.addAll(context.listeners);
        }

        // 复制程序
        if (context.globalPrograms != null) {
            this.globalPrograms.addAll(context.globalPrograms);
        }

        // 复制全局变量
        if (context.globalVariable != null) {
            this.globalVariable.putAll(context.globalVariable);
        }

        // 复制全局数据库编目信息
        if (context.globalCatalog != null) {
            this.globalCatalog.putAll(context.globalCatalog);
        }

        // 复制环境变量的引用
        if (context.environmentVariable != null) {
            this.environmentVariable = context.environmentVariable;
        }
    }

    /**
     * 返回当前脚本引擎对象的父脚本引擎对象
     *
     * @return 脚本引擎上下文信息
     */
    public UniversalScriptContext getParent() {
        return this.parent;
    }

    /**
     * 返回脚本引擎上下文信息对应的脚本引擎对象
     *
     * @return 脚本引擎
     */
    public UniversalScriptEngine getEngine() {
        return this.engine;
    }

    /**
     * 返回容器上下文信息
     *
     * @return 容器上下文信息
     */
    public EasyContext getContainer() {
        return this.ioc;
    }

    /**
     * 返回用户设置的全局和局部变量中的字符集名
     *
     * @return 字符集名
     */
    public String getCharsetName() {
        Object value = this.getVariable(UniversalScriptVariable.VARNAME_CHARSET);
        if (value instanceof String) {
            return (String) value;
        } else {
            return CharsetUtils.get();
        }
    }

    /**
     * 返回执行命令的监听器
     *
     * @return 监听器
     */
    public UniversalScriptListenerList getListenerList() {
        return listeners;
    }

    /**
     * 添加一个程序
     *
     * @param name   关键字
     * @param value  值
     * @param global 是否是全局变量
     */
    public void addProgram(String name, Object value, boolean global) {
        if (global) {
            if (this.globalPrograms.containsKey(name)) {
                throw new UnsupportedOperationException(name);
            } else {
                this.globalPrograms.put(name, value);
            }
        } else {
            if (this.localPrograms.containsKey(name)) {
                throw new UnsupportedOperationException(name);
            } else {
                this.localPrograms.put(name, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <E> E getProgram(String name, boolean global) {
        if (global) {
            return (E) this.globalPrograms.get(name);
        } else {
            return (E) this.localPrograms.get(name);
        }
    }

    /**
     * 返回局部程序集合
     *
     * @return 局部程序集合
     */
    protected ScriptProgram getLocalPrograms() {
        return this.localPrograms;
    }

    /**
     * 返回全局程序集合
     *
     * @return 全局程序集合
     */
    protected ScriptProgram getGlobalPrograms() {
        return this.globalPrograms;
    }

    /**
     * 判断是否已配置局部数据库编目信息
     *
     * @param name 数据库编目名
     * @return 返回true表示存在局部变量 false表示不存在局部变量
     */
    public boolean containLocalCatalog(String name) {
        return this.localCatalog.containsKey(name.toUpperCase());
    }

    /**
     * 添加局部数据库编目信息
     *
     * @param name  数据库编目名
     * @param value 数据库编目所在文件或数据库编目所在 Properties 对象
     * @throws IOException 加载资源文件错误
     */
    public Properties addLocalCatalog(String name, Object value) throws IOException {
        if (StringUtils.isBlank(name) || value == null) {
            throw new IllegalArgumentException();
        }

        if (value instanceof Properties) {
            return this.localCatalog.put(name.toUpperCase(), (Properties) value);
        }

        if (value instanceof String) {
            Properties catalog = FileUtils.loadProperties((String) value);
            return this.localCatalog.put(name.toUpperCase(), catalog);
        }

        throw new UnsupportedOperationException(value.getClass().getName());
    }

    /**
     * 返回指定数据库编目信息
     *
     * @param name 数据库编目名
     * @return 数据库编目信息
     */
    public Properties getLocalCatalog(String name) {
        return this.localCatalog.get(name.toUpperCase());
    }

    /**
     * 返回局部数据库编目集合
     *
     * @return 局部数据库编目
     */
    protected ScriptCatalog getLocalCatalog() {
        return this.localCatalog;
    }

    /**
     * 删除局部数据库编目信息
     *
     * @param name 数据库编目名
     */
    public Properties removeLocalCatalog(String name) {
        Ensure.notBlank(name);
        return this.localCatalog.remove(name.toUpperCase());
    }

    /**
     * 返回数据库编目信息，优先使用全局数据库编目信息。
     *
     * @param name 数据库编目名
     * @return 数据库编目信息
     */
    public Properties getCatalog(String name) {
        String key = name.toUpperCase();
        Properties obj = this.globalCatalog.get(key);
        if (obj == null) {
            return this.localCatalog.get(key);
        } else {
            return obj;
        }
    }

    /**
     * 添加全局数据库编目信息
     *
     * @param name  数据库编目名
     * @param value 数据库编目信息，可以是 Properties 或 filepath
     * @throws IOException 加载资源文件错误
     */
    public Properties addGlobalCatalog(String name, Object value) throws IOException {
        if (StringUtils.isBlank(name) || value == null) {
            throw new IllegalArgumentException();
        }

        if (value instanceof Properties) {
            return this.globalCatalog.put(name.toUpperCase(), (Properties) value);
        }

        if (value instanceof String) {
            Properties catalog = FileUtils.loadProperties((String) value);
            return this.globalCatalog.put(name.toUpperCase(), catalog);
        }

        throw new UnsupportedOperationException(value.getClass().getName());
    }

    /**
     * 返回指定数据库编目信息
     *
     * @param name 数据库编目名
     * @return 数据库编目信息
     */
    public Properties getGlobalCatalog(String name) {
        return this.globalCatalog.get(name.toUpperCase());
    }

    /**
     * 返回全局数据库编目集合
     *
     * @return 数据库编目集合
     */
    protected ScriptCatalog getGlobalCatalog() {
        return this.globalCatalog;
    }

    /**
     * 删除指定数据库编目信息
     *
     * @param name 数据库编目名
     */
    public Properties removeGlobalCatalog(String name) {
        return this.globalCatalog.remove(name.toUpperCase());
    }

    /**
     * 返回局部变量集合
     *
     * @return 局部变量
     */
    public UniversalScriptVariable getLocalVariable() {
        return this.localVariable;
    }

    /**
     * 判断是否存在局部变量
     *
     * @param name 变量名
     * @return 返回true表示存在变量
     */
    public boolean containsLocalVariable(String name) {
        return this.localVariable.containsKey(name);
    }

    /**
     * 添加局部变量
     *
     * @param name  变量名
     * @param value 变量值
     */
    public void addLocalVariable(String name, Object value) {
        this.localVariable.put(name, value == null ? "" : value);
    }

    /**
     * 返回局部变量值
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getLocalVariable(String name) {
        return this.localVariable.get(name);
    }

    /**
     * 返回全局变量集合
     *
     * @return 变量集合
     */
    public UniversalScriptVariable getGlobalVariable() {
        return this.globalVariable;
    }

    /**
     * 判断是否存在全局变量
     *
     * @param name 变量名
     * @return 返回true表示存在变量
     */
    public boolean containsGlobalVariable(String name) {
        return this.globalVariable.containsKey(name);
    }

    /**
     * 添加全局变量
     *
     * @param name  变量名
     * @param value 变量值
     */
    public void addGlobalVariable(String name, Object value) {
        if (value == null) {
            value = "";
        }

        this.globalVariable.put(name, value);
        if (this.localVariable.containsKey(name)) {
            this.localVariable.put(name, value);
        }
    }

    /**
     * 返回全局变量值
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getGlobalVariable(String name) {
        return this.globalVariable.get(name);
    }

    /**
     * 返回环境变量
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getEnvironmentVariable(String name) {
        return this.environmentVariable.get(name);
    }

    /**
     * 返回环境变量集合
     *
     * @return 环境变量集合
     */
    public UniversalScriptVariable getEnvironmentVariable() {
        return environmentVariable;
    }

    /**
     * 判断是否存在环境变量
     *
     * @param name 变量名
     * @return 返回true表示存在
     */
    public boolean containsEnvironmentVariable(String name) {
        return this.environmentVariable.containsKey(name);
    }

    /**
     * 添加环境变量
     *
     * @param array 环境变量，第一个元素是变量名，第二个元素是变量值，第三个元素是变量名 ..
     */
    public void addEnvironmentVariable(Object... array) {
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            int next = i + 1;
            if (next < array.length && object instanceof CharSequence) {
                String name = object.toString();
                Object value = array[next];
                this.environmentVariable.put(name, value);
                i = next;
            } else {
                throw new IllegalArgumentException(StringUtils.toString(array));
            }
        }
    }

    /**
     * 将参数集合中的所有参数添加到指定域中
     *
     * @param variables 参数集合
     * @param scope     域的编号 <br>
     *                  {@link UniversalScriptContext#ENGINE_SCOPE} <br>
     *                  {@link UniversalScriptContext#GLOBAL_SCOPE} <br>
     *                  {@link UniversalScriptContext#ENVIRONMENT_SCOPE} <br>
     */
    public void addVariable(UniversalScriptVariable variables, int scope) {
        switch (scope) {
            case UniversalScriptContext.ENGINE_SCOPE:
                if (variables != null) {
                    this.localVariable.putAll(variables);
                }
                break;

            case UniversalScriptContext.GLOBAL_SCOPE:
                if (variables != null) {
                    this.globalVariable.putAll(variables);
                }
                break;

            case UniversalScriptContext.ENVIRONMENT_SCOPE:
                if (variables != null) {
                    this.environmentVariable = variables;
                }
                break;

            default:
                throw new UniversalScriptException("script.stderr.message053", scope);
        }
    }

    /**
     * 判断是否存在变量
     *
     * @param name 变量名
     * @return 返回true表示存在变量
     */
    public boolean containsVariable(String name) {
        return this.localVariable.containsKey(name) || this.globalVariable.containsKey(name) || this.ioc.hasProperty(name) || this.environmentVariable.containsKey(name) || Settings.containsVariable(name);
    }

    /**
     * 返回变量值 <br>
     * 如果在不同的域中存在同名的变量名时，按域的优先级从高到低返回变量值，域的优先级如下：<br>
     * {@literal 局部变量域 > 全局变量域 > 环境变量域 }
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getVariable(String name) {
        if (this.localVariable.containsKey(name)) {
            return this.getLocalVariable(name);
        }

        if (this.globalVariable.containsKey(name)) {
            return this.getGlobalVariable(name);
        }

        if (this.ioc.hasProperty(name)) {
            return this.ioc.getProperty(name);
        }

        if (this.environmentVariable.containsKey(name)) {
            return this.environmentVariable.get(name);
        }

        return Settings.getVariable(name);
    }

    /**
     * 返回变量值 <br>
     * 如果在不同的域中存在同名的变量名时，按域的优先级从高到低返回变量值，域的优先级如下：<br>
     * {@literal session > 局部变量域 > 全局变量域 > 环境变量域 }
     *
     * @param session 会话信息
     * @param name    变量名
     * @return 变量值
     */
    public Object getVariable(UniversalScriptSession session, String name) {
        if (session.containsVariable(name)) {
            return session.getVariable(name);
        } else {
            return this.getVariable(name);
        }
    }

    /**
     * 返回变量值
     *
     * @param name  变量名
     * @param scope 域的编号 <br>
     *              {@link UniversalScriptContext#ENGINE_SCOPE} <br>
     *              {@link UniversalScriptContext#GLOBAL_SCOPE} <br>
     *              {@link UniversalScriptContext#ENVIRONMENT_SCOPE} <br>
     */
    public Object getVariable(String name, int scope) {
        switch (scope) {
            case UniversalScriptContext.ENGINE_SCOPE:
                return this.getLocalVariable(name);

            case UniversalScriptContext.GLOBAL_SCOPE:
                return this.getGlobalVariable(name);

            case UniversalScriptContext.ENVIRONMENT_SCOPE:
                return getEnvironmentVariable(name);

            default:
                throw new UniversalScriptException("script.stderr.message053", scope);
        }
    }

    /**
     * 删除指定域中的变量
     *
     * @param name  变量名
     * @param scope 域的编号 <br>
     *              {@link UniversalScriptContext#ENGINE_SCOPE} <br>
     *              {@link UniversalScriptContext#GLOBAL_SCOPE} <br>
     */
    public Object removeVariable(String name, int scope) {
        switch (scope) {
            case UniversalScriptContext.ENGINE_SCOPE:
                return this.localVariable.remove(name);

            case UniversalScriptContext.GLOBAL_SCOPE:
                return this.globalVariable.remove(name);

            default:
                throw new UniversalScriptException("script.stderr.message053", scope);
        }
    }

    /**
     * 添加变量到指定域中
     *
     * @param name  变量名
     * @param value 变量值
     * @param scope 域的编号 <br>
     *              {@link UniversalScriptContext#ENGINE_SCOPE} <br>
     *              {@link UniversalScriptContext#GLOBAL_SCOPE} <br>
     */
    public void addVariable(String name, Object value, int scope) {
        switch (scope) {
            case UniversalScriptContext.ENGINE_SCOPE:
                this.addLocalVariable(name, value);
                return;

            case UniversalScriptContext.GLOBAL_SCOPE:
                this.addGlobalVariable(name, value);
                return;
        }

        throw new UniversalScriptException("script.stderr.message053", scope);
    }

    /**
     * 返回变量所在的域编号 <br>
     * 如果在不同的域中存在同名的变量名时，按域的优先级从高到低返回，域的优先级如下：<br>
     * {@literal 局部变量域 > 全局变量域 > 全局数据库编目域 > 内置变量域 }
     *
     * @param name 变量名
     * @return 域的编号 <br>
     * {@link UniversalScriptContext#ENGINE_SCOPE} <br>
     * {@link UniversalScriptContext#GLOBAL_SCOPE} <br>
     * {@link UniversalScriptContext#ENVIRONMENT_SCOPE} <br>
     */
    public int getVariableScope(String name) {
        if (this.localVariable.containsKey(name)) {
            return UniversalScriptContext.ENGINE_SCOPE;
        }

        if (this.globalVariable.containsKey(name)) {
            return UniversalScriptContext.GLOBAL_SCOPE;
        }

        if (this.environmentVariable.containsKey(name)) {
            return UniversalScriptContext.ENVIRONMENT_SCOPE;
        }

        return -1;
    }

    /**
     * 返回指定域的变量集合
     *
     * @param scope 域的编号 <br>
     *              {@link UniversalScriptContext#ENGINE_SCOPE} <br>
     *              {@link UniversalScriptContext#GLOBAL_SCOPE} <br>
     *              {@link UniversalScriptContext#ENVIRONMENT_SCOPE} <br>
     */
    public UniversalScriptVariable getVariables(int scope) {
        switch (scope) {
            case UniversalScriptContext.ENGINE_SCOPE:
                return this.localVariable;

            case UniversalScriptContext.GLOBAL_SCOPE:
                return this.globalVariable;

            case UniversalScriptContext.ENVIRONMENT_SCOPE:
                return this.environmentVariable;

            default:
                throw new UniversalScriptException("script.stderr.message053", scope);
        }
    }

    /**
     * 返回变量名
     *
     * @return 变量名集合
     */
    public Set<String> getVariableNames(UniversalScriptSession session) {
        Set<String> sks = (session == null) ? new HashSet<String>() : session.getVariables().keySet();
        Set<String> lks = this.getLocalVariable().keySet(); // 局部变量名
        Set<String> gks = this.getGlobalVariable().keySet(); // 全局变量名
        Set<String> eks = this.getEnvironmentVariable().keySet(); // 环境变量名

        int size = sks.size() + lks.size() + gks.size() + eks.size();
        HashSet<String> names = new LinkedHashSet<String>(size);
        names.addAll(eks);
        names.addAll(gks);
        names.addAll(lks);
        names.addAll(sks);
        return names;
    }

    /**
     * 返回脚本引擎支持的所有域
     */
    public List<Integer> getScopes() {
        return ArrayUtils.asList(UniversalScriptContext.ENGINE_SCOPE, UniversalScriptContext.GLOBAL_SCOPE);
    }

    /**
     * 释放所有资源
     */
    protected void close() {
        this.getGlobalVariable().clear(); // 清空全局变量
        this.getLocalVariable().clear(); // 清空局部变量
        this.getGlobalCatalog().clear(); // 清空全局数据库编目
        this.getLocalCatalog().clear(); // 清空局部数据库编目
        this.getGlobalPrograms().close(); // 关闭全局程序
        this.getLocalPrograms().close(); // 关闭局部程序
    }
}
