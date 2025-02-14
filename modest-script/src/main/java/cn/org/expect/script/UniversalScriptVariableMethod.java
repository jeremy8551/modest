package cn.org.expect.script;

/**
 * 脚本引擎变量方法
 *
 * @author jeremy8551@gmail.com
 */
public interface UniversalScriptVariableMethod {

    /**
     * 执行方法
     *
     * @param session    脚本引擎会话信息
     * @param context    脚本引擎上下文信息
     * @param stdout     标准输出
     * @param stderr     标准错误输出
     * @param analysis   语句分析器
     * @param variable   变量
     * @param parameters 参数集合
     * @return 方法的返回值
     * @throws Exception 运行方法发生错误
     */
    Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception;
}
