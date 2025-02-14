package cn.org.expect.script;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.Map;

import cn.org.expect.script.io.ScriptFileExpression;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.script.session.ScriptSubProcess;
import cn.org.expect.util.Terminate;

/**
 * 用户会话信息 <br>
 * <p>
 * 用户或程序在执行 {@linkplain UniversalScriptEngine#evaluate(Reader, UniversalScriptContext)} 方法视作一次会话
 *
 * @author jeremy8551@gmail.com
 */
public interface UniversalScriptSession extends Terminate {

    /**
     * 设置会话的返回值（即 {@linkplain UniversalScriptEngine#evaluate(Reader, UniversalScriptContext)} 方法的返回值）
     *
     * @param value 值
     */
    void putValue(Object value);

    /**
     * 会话的返回值（即 {@linkplain UniversalScriptEngine#evaluate(Reader, UniversalScriptContext)} 方法的返回值）
     *
     * @return 返回值
     */
    <E> E getValue();

    /**
     * 返回用户会话编号
     *
     * @return 用户会话编号
     */
    String getId();

    /**
     * 返回父会话编号
     *
     * @return 父会话编号
     */
    String getParentID();

    /**
     * 返回脚本引擎名称
     *
     * @return 脚本引擎名称
     */
    String getScriptName();

    /**
     * 设置脚本文件
     *
     * @param file 脚本文件表达式
     * @throws IOException 访问文件错误
     */
    void setScriptFile(ScriptFileExpression file) throws IOException;

    /**
     * 判断是不是脚本文件
     *
     * @return 判断 true 表示是脚本文件
     */
    boolean isScriptFile();

    /**
     * 返回主线程
     *
     * @return 主线程
     */
    ScriptMainProcess getMainProcess();

    /**
     * 返回（并行运行的）后台任务
     *
     * @return 后台运行的进程
     */
    ScriptSubProcess getSubProcess();

    /**
     * 返回用户会话的工厂
     *
     * @return 用户会话的工厂
     */
    UniversalScriptSessionFactory getSessionFactory();

    /**
     * 添加变量方法的临时变量
     *
     * @param name  变量名
     * @param value 变量值
     */
    void addMethodVariable(String name, Object value);

    /**
     * 返回变量方法的临时变量值
     *
     * @param name 变量名
     * @return 临时变量值
     */
    Object getMethodVariable(String name);

    /**
     * 删除变量方法的临时变量
     *
     * @param name 变量名
     * @return 被删除的临时变量
     */
    Object removeMethodVariable(String name);

    /**
     * 保存自定义方法的参数值数组
     *
     * @param parameter 参数数组
     */
    void setFunctionParameter(String[] parameter);

    /**
     * 返回自定义方法的参数值数组
     *
     * @return 参数数组
     */
    String[] getFunctionParameter();

    /**
     * 删除自定义方法的参数值数组
     */
    void removeFunctionParameter();

    /**
     * 返回脚本参数
     *
     * @return 参数数组
     */
    String[] getScriptParameters();

    /**
     * 保存脚本参数
     *
     * @param parameter 参数数组
     */
    void setScriptParameters(String[] parameter);

    /**
     * 删除脚本参数
     */
    void removeScriptParameter();

    /**
     * 判断当前会话信息是否关闭
     *
     * @return 返回true表示会话未关闭
     */
    boolean isAlive();

    /**
     * 设置echo命令是否可用
     *
     * @param enable true表示已设置 echo on，false表示已执行 echo off
     */
    void setEchoEnabled(boolean enable);

    /**
     * 返回echo命令是否可用
     *
     * @return true表示已设置 echo on，false表示已执行 echo off
     */
    boolean isEchoEnable();

    /**
     * 保存当前用户会话使用的编译器
     *
     * @param compiler 编译器
     */
    void setCompiler(UniversalScriptCompiler compiler);

    /**
     * 返回当前用户会话使用的编译器
     *
     * @return 编译器
     */
    UniversalScriptCompiler getCompiler();

    /**
     * 返回语句分析器
     *
     * @return 语句分析器
     */
    UniversalScriptAnalysis getAnalysis();

    /**
     * 返回变量值
     *
     * @return 变量值
     */
    <E> E getVariable(String key);

    /**
     * 保存变量
     *
     * @param key   变量名
     * @param value 变量值
     */
    void addVariable(String key, Object value);

    /**
     * 返回变量集合
     *
     * @return 变量集合
     */
    Map<String, Object> getVariables();

    /**
     * 判断是否存在变量
     *
     * @param name 变量名
     * @return 返回true表示存在变量
     */
    boolean containsVariable(String name);

    /**
     * 移除变量
     *
     * @param key 变量名
     * @return 变量值
     */
    Object removeVariable(String key);

    /**
     * 设置脚本引擎默认的目录
     *
     * @param dir 目录
     */
    void setDirectory(File dir);

    /**
     * 返回脚本引擎默认的目录
     *
     * @return 目录
     */
    String getDirectory();

    /**
     * 是否检查 {@linkplain UniversalScriptEngine#evaluate(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, Reader)} 方法的返回值
     *
     * @return 返回true表示校验返回值是否为0 false表示不校验返回值
     */
    boolean isVerifyExitcode();

    /**
     * 设置是否检查 {@linkplain UniversalScriptEngine#evaluate(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean, Reader)} 方法的返回值
     *
     * @param checkExitcode true表示校验返回值是否为0 false表示不校验返回值
     */
    void setVerifyExitcode(boolean checkExitcode);

    /**
     * 创建一个子会话
     *
     * @return 子会话
     */
    UniversalScriptSession subsession();

    /**
     * 清空所有信息
     */
    void close();

    /**
     * 返回用户会话创建时间
     *
     * @return 创建时间
     */
    Date getCreateTime();

    /**
     * 返回用户会话结束时间
     *
     * @return 结束时间
     */
    Date getEndTime();

    /**
     * 返回临时文件目录
     *
     * @return 目录
     */
    File getTempDir();
}
