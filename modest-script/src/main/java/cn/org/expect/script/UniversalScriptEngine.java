package cn.org.expect.script;

import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.org.expect.io.AliveReader;
import cn.org.expect.script.io.ScriptStderr;
import cn.org.expect.script.io.ScriptStdout;
import cn.org.expect.script.io.ScriptSteper;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.UniqueSequenceGenerator;

/**
 * 脚本引擎 <br>
 * <br>
 * 脚本引擎使用说明详见 help 命令: <br>
 * <b> ScriptEngineManager manager = new ScriptEngineManager(); </b> <br>
 * <b> ScriptEngine engine = manager.getEngineByExtension("usl"); </b> <br>
 * <b> engine.eval("help"); </b> <br>
 * <b> engine.eval("exit 0"); </b> <br>
 *
 * @author jeremy8551@gmail.com
 * @createtime 2018-06-01
 */
public class UniversalScriptEngine implements Closeable {

    /** 脚本引擎的序号生成器 */
    protected final static UniqueSequenceGenerator UNIQUE = new UniqueSequenceGenerator("{timestamp}{}", 1);

    /** 唯一编号 */
    private final String id;

    /** 脚本引擎正在执行的语句输入流 */
    private Reader reader;

    /** true表示脚本引擎已关闭 */
    private final AtomicBoolean close;

    /** 脚本引擎上下文信息 */
    protected UniversalScriptContext context;

    /** 脚本引擎的工厂类 */
    protected UniversalScriptEngineFactory factory;

    /** 用户会话信息工厂 */
    protected UniversalScriptSessionFactory sessionFactory;

    /** 内部对象转换器 */
    protected UniversalScriptFormatter format;

    /** 校验规则 */
    protected UniversalScriptChecker checker;

    /** 标准信息输出接口 */
    protected final UniversalScriptStdout stdout;

    /** 错误信息输出接口 */
    protected final UniversalScriptStderr stderr;

    /** 步骤信息输出接口 */
    protected final UniversalScriptSteper steper;

    /**
     * 初始化
     *
     * @param factory 脚本引擎工厂类
     */
    public UniversalScriptEngine(UniversalScriptEngineFactory factory) {
        this.factory = Ensure.notNull(factory);
        this.id = UNIQUE.nextString();
        this.sessionFactory = factory.buildSessionFactory();
        this.format = factory.buildFormatter();
        this.checker = factory.buildChecker();
        this.close = new AtomicBoolean(false);

        UniversalScriptFormatter formatter = this.getFactory().buildFormatter();
        this.stdout = new ScriptStdout(null, formatter);
        this.stderr = new ScriptStderr(null, formatter);
        this.steper = new ScriptSteper(null, formatter);

        // 最后初始化上下文信息
        this.context = new UniversalScriptContext(this);
    }

    /**
     * 返回脚本引擎编号
     *
     * @return 脚本引擎编号
     */
    public String getId() {
        return id;
    }

    /**
     * 返回脚本引擎上下文信息
     *
     * @return 脚本引擎上下文信息
     */
    public UniversalScriptContext getContext() {
        return this.context;
    }

    /**
     * 返回脚本引擎工厂
     *
     * @return 脚本引擎工厂
     */
    public UniversalScriptEngineFactory getFactory() {
        return factory;
    }

    /**
     * 返回读取脚本语句的 Reader
     */
    public Reader getReader() {
        return this.reader;
    }

    /**
     * 设置读取脚本语句的 Reader
     */
    public void setReader(Reader reader) {
        this.reader = new AliveReader(reader);
    }

    /**
     * 返回脚本引擎内部对象转换器
     *
     * @return 对象转换器
     */
    public UniversalScriptFormatter getFormatter() {
        return this.format;
    }

    /**
     * 返回校验规则
     *
     * @return 校验规则
     */
    public UniversalScriptChecker getChecker() {
        return this.checker;
    }

    /**
     * 设置脚本引擎上下文信息
     *
     * @param context 脚本引擎上下文信息
     */
    public void setContext(UniversalScriptContext context) {
        this.context = Ensure.notNull(context);
    }

    /**
     * 执行脚本文件
     *
     * @param type  脚本文件所在的包
     * @param array 环境变量，第一个元素是变量名，第二个元素是变量值，第三个元素是变量名 ..
     * @param <E>   计算结果的类信息
     * @return 计算结果
     */
    public <E> E executeScriptFile(Class<?> type, Object... array) {
        String scriptFilename = StringUtils.firstCharToLower(type.getSimpleName());
        return this.executeScriptFile(scriptFilename, type, array);
    }

    /**
     * 执行脚本文件
     *
     * @param scriptFilename 脚本文件名
     * @param type           脚本文件所在的包
     * @param array          环境变量，第一个元素是变量名，第二个元素是变量值，第三个元素是变量名 ..
     * @param <E>            计算结果的类信息
     * @return 计算结果
     */
    public <E> E executeScriptFile(String scriptFilename, Class<?> type, Object... array) {
        String parent = type.getPackage().getName().replace('.', '/');
        String executeScriptFile = StringUtils.replaceEmptyHolder(". classpath:/{}/{}.usl", parent, scriptFilename);
        return this.evaluate(executeScriptFile, array);
    }

    /**
     * 执行脚本语句
     *
     * @param script 脚本语句
     * @param array  环境变量，第一个元素是变量名，第二个元素是变量值，第三个元素是变量名 ..
     * @param <E>    计算结果的类信息
     * @return 计算结果
     */
    public <E> E evaluate(String script, Object... array) {
        Ensure.notNull(script);
        UniversalScriptContext context = this.getContext();
        context.addEnvironmentVariable(array); // 环境变量
        CharArrayReader in = new CharArrayReader(script.toCharArray());
        return this.evaluate(in, context); // 执行脚本
    }

    /**
     * 执行脚本语句
     *
     * @param in      脚本语句输入流
     * @param context 脚本引擎上下文信息
     * @param <E>     计算结果的类信息
     * @return 计算结果
     */
    public <E> E evaluate(Reader in, UniversalScriptContext context) {
        this.setReader(in);

        int value;
        UniversalScriptSession session = this.sessionFactory.build(this);
        try {
            value = this.evaluate(session, context, context.getEngine().getStdout(), context.getEngine().getStderr(), false, in);
        } catch (UniversalScriptException use) {
            throw use;
        } catch (Throwable e) {
            throw new UniversalScriptException("script.stderr.message108", session.getMainProcess().getErrorScript(), e);
        } finally {
            session.close();
        }

        // 校验返回值是否正确
        if (session.isVerifyExitcode() && value != 0) {
            throw new UniversalScriptException("script.stderr.message135", value);
        } else {
            return session.getValue(); // 会话的返回值
        }
    }

    /**
     * 执行脚本语句
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param forceStdout true 表示使用标准信息输出接口输出标准信息（忽略 {@linkplain UniversalScriptSession#isEchoEnable()} 返回值）
     * @param in          语句输入流
     * @return 返回0表示正确, 返回非0表示不正确
     * @throws Exception 文件访问错误
     */
    public int evaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, Reader in) throws Exception {
        context.getListenerList().startEvaluate(session, context, stdout, stderr, forceStdout, in);
        UniversalScriptCompiler oldCompiler = session.getCompiler(); // 保存当前使用的编译器
        UniversalScriptCommand command = null;
        UniversalCommandResultSet resultSet = null;
        ScriptMainProcess process = session.getMainProcess();
        UniversalScriptCompiler compiler = (oldCompiler == null) ? this.getFactory().buildCompiler() : oldCompiler.buildCompiler();
        try {
            compiler.compile(session, context, in); // 执行编译
            session.setCompiler(compiler); // 在读取命令之前一定要保存编译器到用户会话信息中

            while (!session.isTerminate() && compiler.hasNext()) {
                command = compiler.next();
                resultSet = process.execute(session, context, stdout, stderr, forceStdout, command);
                if (resultSet.isExitSession()) {
                    break;
                }
            }

            context.getListenerList().exitEvaluate(session, context, stdout, stderr, forceStdout, command, resultSet);
            assert resultSet != null;
            return resultSet.getExitcode();
        } catch (Exception e) {
            context.getListenerList().catchEvaluate(session, context, stdout, stderr, forceStdout, command, resultSet, e);
            return UniversalScriptCommand.ERROR;
        } finally {
            compiler.close(); // 关闭当前编译器

            if (oldCompiler != null) { // 恢复历史编译器
                session.setCompiler(oldCompiler);
            }
        }
    }

    /**
     * 执行命令代换
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param stdout  标准信息输出接口
     * @param stderr  错误信息输出接口
     * @param str     字符串
     * @return 脚本命令的返回值
     */
    public int evaluate(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String str) {
        UniversalScriptCompiler oldCompiler = session.getCompiler(); // 保存当前使用的编译器

        int exitcode = 0;
        ScriptMainProcess process = session.getMainProcess();
        UniversalScriptCompiler compiler = (oldCompiler == null) ? this.getFactory().buildCompiler() : oldCompiler.buildCompiler();
        try {
            compiler.compile(session, context, new CharArrayReader(str.toCharArray())); // 执行编译
            session.setCompiler(compiler); // 在读取命令之前一定要保存编译器到用户会话信息中

            // 继续向下执行
            while (!session.isTerminate() && compiler.hasNext()) {
                UniversalScriptCommand command = compiler.next();
                UniversalCommandResultSet result = process.execute(session, context, stdout, stderr, false, command);
                exitcode = result.getExitcode();
                if (result.isExitSession()) {
                    break;
                }
            }

            if (session.isTerminate()) { // 会话已被终止
                stderr.println(ResourcesUtils.getMessage("script.stderr.message041", session.getScriptName()));
                return UniversalScriptCommand.TERMINATE;
            } else {
                return exitcode;
            }
        } catch (Throwable e) {
            throw new UniversalScriptException("script.stderr.message052", str, e);
        } finally {
            compiler.close(); // 关闭当前编译器

            if (oldCompiler != null) { // 恢复历史编译器
                session.setCompiler(oldCompiler);
            }
        }
    }

    /**
     * 设置输出标准信息使用的 Writer
     */
    public void setWriter(Writer writer) {
        this.stdout.setWriter(writer);
    }

    /**
     * 返回输出标准信息使用的 Writer
     */
    public Writer getWriter() {
        return this.stdout.getWriter();
    }

    /**
     * 设置用于显示错误输出的 Writer
     */
    public void setErrorWriter(Writer writer) {
        this.stderr.setWriter(writer);
    }

    /**
     * 返回用于显示错误输出的 Writer
     */
    public Writer getErrorWriter() {
        return this.stderr.getWriter();
    }

    /**
     * 设置用于输出步骤信息的 Writer
     *
     * @param writer 步骤信息输出流
     */
    public void setStepWriter(Writer writer) {
        this.steper.setWriter(writer);
    }

    /**
     * 返回用于输出步骤信息的 Writer
     *
     * @return 步骤信息输出流
     */
    public Writer getStepWriter() {
        return this.steper.getWriter();
    }

    /**
     * 返回脚本引擎的标准输出对象
     *
     * @return 标准输出对象
     */
    public UniversalScriptStdout getStdout() {
        return this.stdout;
    }

    /**
     * 返回脚本引擎的错误输出对象
     *
     * @return 错误输出对象
     */
    public UniversalScriptStderr getStderr() {
        return this.stderr;
    }

    /**
     * 返回脚本引擎的步骤输出对象
     *
     * @return 步骤输出对象
     */
    public UniversalScriptSteper getSteper() {
        return this.steper;
    }

    /**
     * 判断脚本引擎是否已关闭
     *
     * @return 返回true表示脚本引擎已关闭 false表示未关闭
     */
    public boolean isClose() {
        return this.close.get();
    }

    public synchronized void close() {
        if (!this.close.compareAndSet(false, true)) {
            return;
        }

        IO.flushQuietly(this.getWriter(), this.getErrorWriter(), this.getStepWriter());
        this.setWriter(null);
        this.setErrorWriter(null);
        this.setStepWriter(null);
        this.setReader(null);

        if (this.context != null) {
            this.context.close();
        }
    }
}
