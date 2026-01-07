package cn.org.expect.script.session;

import java.io.File;
import java.io.Reader;
import java.util.Date;
import java.util.Map;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptSessionFactory;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.UniversalScriptVariable;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;

/**
 * 接口实现类
 *
 * @author jeremy8551@gmail.com
 */
public class ScriptSession extends Terminator implements UniversalScriptSession {

    /** 脚本引擎工厂 */
    private final UniversalScriptEngineFactory engineFactory;

    /** 会话编号 */
    private String sessionId;

    /** 上级会话编号 */
    private String parentSessionId;

    /** 用户会话所属的脚本引擎编号 */
    private final String scriptEngineId;

    /** 表示用户会话归属的集合 */
    private SessionFactory factory;

    /** 会话创建时间 */
    private final Date startTime;

    /** 会话结束时间 */
    private Date endTime;

    /** 主线程 */
    private final ScriptMainProcess main;

    /** 子线程 */
    private ScriptSubProcess subs;

    /** 用户自定义方法的参数 */
    private String[] functionParameters;

    /** 脚本参数，第一个元素是脚本名，第二个元素是第一个参数 */
    private String[] scriptParameters;

    /** true表示会话正在使用 */
    private boolean isAlive;

    /** echo命令是否可用，true表示可用 false表示不可用 */
    private boolean echoEnabled;

    /** 语句分析器 */
    private UniversalScriptAnalysis anlaysis;

    /** 编译器 */
    private UniversalScriptCompiler compiler;

    /** 变量集合 */
    private final Map<String, Object> variable;

    /** 内部使用的变量集合 */
    private final Map<String, Object> systemVariable;

    /** 变量方法的缓存变量 */
    private final Map<String, Object> methodVariable;

    /** 脚本引擎名（可以不唯一） */
    private String name;

    /** 临时文件存储目录 */
    private final File tempDir;

    /** 会话的返回值 */
    private Object value;

    /** 是否检查 {@linkplain UniversalScriptEngine#evaluate(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, Reader)} 方法的返回值 */
    private volatile boolean checkExitcode;

    /**
     * 初始化
     *
     * @param engineFactory  脚本引擎工厂
     * @param scriptEngineId 脚本引擎编号
     */
    private ScriptSession(UniversalScriptEngineFactory engineFactory, String scriptEngineId) {
        this.engineFactory = Ensure.notNull(engineFactory);
        this.scriptEngineId = Ensure.notBlank(scriptEngineId);
        this.sessionId = "M" + StringUtils.toRandomUUID();
        this.name = ResourcesUtils.getMessage("script.stdout.message018"); // 脚本引擎
        this.isAlive = true;
        this.startTime = new Date();
        this.endTime = null;
        this.variable = this.engineFactory.buildVariable();
        this.systemVariable = this.engineFactory.buildVariable();
        this.methodVariable = this.engineFactory.buildVariable();
        this.main = new ScriptMainProcess();
        this.subs = new ScriptSubProcess();
        this.echoEnabled = true;
        this.terminate = false;
        this.tempDir = FileUtils.getTempDir(UniversalScriptEngine.class.getSimpleName(), this.scriptEngineId);
        this.checkExitcode = true;

        this.setDirectory(Settings.getUserHome()); // 会话当前目录
        this.addVariable(UniversalScriptVariable.SESSION_VARNAME_HOME, Settings.getUserHome()); // 临时文件目录
        this.addVariable(UniversalScriptVariable.SESSION_VARNAME_TEMP, this.tempDir.getAbsolutePath()); // 临时文件目录
    }

    /**
     * 初始化
     *
     * @param engineFactory  脚本引擎工厂
     * @param scriptEngineId 脚本引擎编号
     * @param factory        脚本引擎会话工厂
     */
    public ScriptSession(UniversalScriptEngineFactory engineFactory, String scriptEngineId, SessionFactory factory) {
        this(engineFactory, scriptEngineId);
        this.factory = Ensure.notNull(factory);
    }

    public void setScriptFilepath(String filepath) {
        this.addVariable(UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE, filepath);

        // TODO
        StringBuilder buf = new StringBuilder();
        if (StringUtils.isNotBlank(this.parentSessionId) && this.factory.get(this.parentSessionId).isScriptFile()) {
            buf.append(ResourcesUtils.getMessage("script.stdout.message020")); // 子脚本文件
        } else {
            buf.append(ResourcesUtils.getMessage("script.stdout.message021")); // 脚本文件
        }
        buf.append(' ');
        buf.append(filepath);
        this.name = buf.toString();
    }

    public boolean isScriptFile() {
        return this.containsVariable(UniversalScriptVariable.SESSION_VARNAME_SCRIPTFILE);
    }

    public String getScriptName() {
        return this.name;
    }

    public ScriptMainProcess getMainProcess() {
        return this.main;
    }

    public ScriptSubProcess getSubProcess() {
        return this.subs;
    }

    public UniversalScriptSessionFactory getSessionFactory() {
        return this.factory;
    }

    public void addMethodVariable(String name, Object value) {
        this.methodVariable.put(name, value);
    }

    public Object getMethodVariable(String name) {
        return this.methodVariable.get(name);
    }

    public Object removeMethodVariable(String name) {
        return this.methodVariable.remove(name);
    }

    public void setFunctionParameter(String[] parameter) {
        this.functionParameters = parameter;
    }

    public String[] getFunctionParameter() {
        return this.functionParameters;
    }

    public void removeFunctionParameter() {
        this.functionParameters = null;
    }

    public String[] getScriptParameters() {
        return scriptParameters;
    }

    public void setScriptParameters(String[] scriptParameters) {
        this.scriptParameters = scriptParameters;
    }

    public void removeScriptParameter() {
        this.scriptParameters = null;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public String getId() {
        return this.sessionId;
    }

    public String getParentID() {
        return this.parentSessionId;
    }

    public void setEchoEnabled(boolean enable) {
        this.echoEnabled = enable;
    }

    public boolean isEchoEnable() {
        return this.echoEnabled;
    }

    public void setDirectory(File dir) {
        FileUtils.assertDirectory(dir);
        File oldPwd = this.addVariable(UniversalScriptVariable.SESSION_VARNAME_PWD, dir);
        if (oldPwd != null) {
            this.addVariable(UniversalScriptVariable.SESSION_VARNAME_OLDPWD, oldPwd);
        }
    }

    public File getDirectory() {
        return this.getVariable(UniversalScriptVariable.SESSION_VARNAME_PWD);
    }

    public void terminate() throws Exception {
        if (!this.terminate) {
            this.terminate = true;
            if (this.compiler != null) {
                this.compiler.terminate();
            }
            this.main.terminate();
            this.subs.terminate();
        }
    }

    public void setCompiler(UniversalScriptCompiler compiler) {
        this.compiler = Ensure.notNull(compiler);
        this.anlaysis = compiler.getAnalysis();
    }

    public UniversalScriptCompiler getCompiler() {
        return this.compiler;
    }

    public UniversalScriptAnalysis getAnalysis() {
        return this.anlaysis;
    }

    @SuppressWarnings("unchecked")
    public <E> E getVariable(String key) {
        return (E) this.variable.get(key);
    }

    public <E> E addVariable(String key, Object value) {
        return (E) this.variable.put(key, value);
    }

    public <E> E getSystemVariable(String key) {
        return (E) this.systemVariable.get(key);
    }

    public <E> E addSystemVariable(String key, Object value) {
        return (E) this.systemVariable.put(key, value);
    }

    public Object removeVariable(String key) {
        return this.variable.remove(key);
    }

    public Map<String, Object> getVariables() {
        return this.variable;
    }

    public boolean containsVariable(String name) {
        return this.variable.containsKey(name);
    }

    public File getTempDir() {
        return this.tempDir;
    }

    public Date getCreateTime() {
        return this.startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    @SuppressWarnings("unchecked")
    public <E> E getValue() {
        return (E) this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isVerifyExitcode() {
        return checkExitcode;
    }

    public void setVerifyExitcode(boolean checkExitcode) {
        this.checkExitcode = checkExitcode;
    }

    public UniversalScriptSession subsession() {
        ScriptSession session = new ScriptSession(this.engineFactory, this.scriptEngineId);
        session.factory = this.factory;
        session.parentSessionId = this.sessionId;
        session.sessionId = "S" + StringUtils.toRandomUUID();
        session.echoEnabled = this.echoEnabled;
        session.anlaysis = this.anlaysis;
        session.compiler = null;
        session.variable.putAll(this.variable);
        session.subs = this.subs; // 同步子线程
        this.factory.add(session);
        return session;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof UniversalScriptSession) {
            UniversalScriptSession session = (UniversalScriptSession) obj;
            return session.getId().equals(this.sessionId);
        } else {
            return false;
        }
    }

    public void close() {
        this.isAlive = false;
        this.factory.remove(this.sessionId);
        this.endTime = new Date();
    }
}
