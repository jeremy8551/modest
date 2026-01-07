package cn.org.expect.script;

import java.util.Map;
import java.util.Properties;

/**
 * 脚本引擎变量接口
 *
 * @author jeremy8551@gmail.com
 */
public interface UniversalScriptVariable extends Map<String, Object> {

    /** 内置变量: 当前脚本引擎 */
    String SESSION_VARNAME_THIS = "this";

    /** 内置变量: 脚本引擎默认目录 */
    String SESSION_VARNAME_HOME = "HOME";

    /** 内置变量: 当前工作目录（自动维护） */
    String SESSION_VARNAME_PWD = "PWD";

    /** 内置变量: 上一次工作目录（自动维护） */
    String SESSION_VARNAME_OLDPWD = "OLDPWD";

    /** 内置变量: 临时文件存储目录 */
    String SESSION_VARNAME_TEMP = "TMPDIR";

    /** 内置变量: 脚本文件的绝对路径 */
    String SESSION_VARNAME_SCRIPTFILE = "scriptFile";

    /** 用户会话最后一次发生的异常信息 */
    String SYSTEM_LASTEXCEPTION = "LAST_EXCEPTION";

    /** 内置变量: 脚本语句的字符集 */
    String VARNAME_CHARSET = "charset";

    /** 内置变量: 异常信息 */
    String VARNAME_EXCEPTION = "exception";

    /** 内置变量: sqlstate 值 */
    String VARNAME_SQLSTATE = "sqlstate";

    /** 内置变量: errorcode 值 */
    String VARNAME_ERRORCODE = "errorcode";

    /** 内置变量: exitcode 值 */
    String VARNAME_EXITCODE = "exitcode";

    /** 内置变量: 发生错误的脚本命令 */
    String VARNAME_ERRORSCRIPT = "errorscript";

    /**
     * 将属性集合添加到变量集合中
     *
     * @param properties 属性集合
     */
    void putAll(Properties properties);
}
