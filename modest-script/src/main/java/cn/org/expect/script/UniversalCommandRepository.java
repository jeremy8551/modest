package cn.org.expect.script;

/**
 * 脚本命令编译器集合
 *
 * @author jeremy8551@gmail.com
 */
public interface UniversalCommandRepository {

    /**
     * 加载所有脚本命令的编译器
     *
     * @param context 脚本引擎上下文信息
     */
    void load(UniversalScriptContext context);

    /**
     * 设置默认的编译器
     *
     * @param compiler 编译器
     */
    void setDefault(UniversalCommandCompiler compiler);

    /**
     * 返回默认的编译器
     *
     * @return 编译器
     */
    UniversalCommandCompiler getDefault();

    /**
     * 判断是否已添加脚本命令
     *
     * @param compiler 脚本命令的编译器类信息
     * @return 返回true表示已添加脚本命令 false表示未添加脚本命令
     */
    boolean contains(Class<? extends UniversalCommandCompiler> compiler);

    /**
     * 添加脚本命令编译器
     *
     * @param names    脚本命令前缀
     * @param compiler 命令的编译器
     */
    void add(String[] names, UniversalCommandCompiler compiler);

    /**
     * 返回编译器
     *
     * @param cls 类信息
     * @return 编译器
     */
    <E extends UniversalCommandCompiler> E get(Class<E> cls);

    /**
     * 返回脚本语句对应的脚本命令编译器
     *
     * @param analysis 脚本语句分析器
     * @param script   脚本语句
     * @return 编译器
     */
    UniversalCommandCompiler get(UniversalScriptAnalysis analysis, String script);

    /**
     * 清空所有信息
     */
    void clear();

    /**
     * 判断是否加载了脚本引擎变量方法
     *
     * @return 返回true表示未加载任何脚本引擎变量方法
     */
    boolean isEmpty();
}
