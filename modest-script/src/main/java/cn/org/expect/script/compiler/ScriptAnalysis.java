package cn.org.expect.script.compiler;

import java.text.Format;
import java.util.Arrays;
import java.util.Map;

import cn.org.expect.expression.DefaultAnalysis;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.script.UniversalScriptFormatter;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.internal.ScriptVariableMap;
import cn.org.expect.script.io.ScriptStdbuf;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 语句分析器的实现类
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean
public class ScriptAnalysis extends DefaultAnalysis implements UniversalScriptAnalysis {
    private final static Log log = LogFactory.getLog(ScriptAnalysis.class);

    public final static String ESCAPE_STRING = "&ads;";

    public ScriptAnalysis() {
        super();
    }

    public WordIterator parse(String script) {
        return new WordIterator(this, script);
    }

    public String unescapeString(CharSequence str) {
        if (!this.escape) {
            return str.toString();
        }

        int length = str.length();
        if (length <= 1) {
            return str.toString();
        }

        char[] array = new char[length];
        int index = 0;
        boolean escape = false;
        for (int i = 0; i < length; ) {
            char c = str.charAt(i);

            if (escape) {
                if (c == this.escapeChar) {
                    array[index++] = this.escapeChar;
                } else if (c == '\'') {
                    array[index++] = '\'';
                } else if (c == '"') {
                    array[index++] = '"';
                } else if (c == '$') {
                    array[index++] = '$';
                } else if (c == 'r') {
                    array[index++] = '\r';
                } else if (c == 'n') {
                    array[index++] = '\n';
                } else if (c == 'b') {
                    array[index++] = '\b';
                } else if (c == 'f') {
                    array[index++] = '\f';
                } else if (c == 't') {
                    array[index++] = '\t';
                } else if (c == 'u') {
                    int ep = i + 5;
                    if (ep <= length) {
                        String number = str.subSequence(i + 1, ep).toString();
                        try {
                            array[index++] = (char) Integer.parseInt(number, 16);
                        } catch (Throwable e) {
                            throw new NumberFormatException("Unicode=[\\u" + number + "] parse to int error!");
                        }
                        escape = false;
                        i += 5;
                        continue;
                    }
                } else {
                    array[index++] = this.escapeChar;
                    array[index++] = c;
                }

                escape = false;
                i++;
                continue;
            }

            if (++i != length && c == this.escapeChar) {
                escape = true;
            } else {
                array[index++] = c;
            }
        }

        return new String(array, 0, index);
    }

    public String getPrefix(String script) {
        int length = script.length();
        StringBuilder buf = new StringBuilder(length);

        // 忽略取反符号 !
        for (int i = script.charAt(0) == '!' ? 1 : 0; i < length; i++) {
            char c = script.charAt(i);

            if (Character.isWhitespace(c) // 空白字符
                || c == this.token // 语句分隔符
                || c == this.comment // 注释符
                || c == this.segdel // 段落分隔符
                || c == '|' // 管道符
                || c == '`' // 命令替换符
                || c == '[' // 变量方法分隔符
                || (c == '.' && i != 0) // 变量方法分隔符， 第一个字符不能是 .
            ) {
                break;
            } else {
                buf.append(c);
            }
        }

        return buf.toString(); // 命令前缀 或 自定义方法名 或 变量名
    }

    public int indexOfVariableName(String str, int from) {
        if (str == null) {
            return -1;
        }

        // 在脚本语句中搜索变量名结束位置 <br>
        // 变量名中只能有英文字母, 数字, 下划线, 首字母不能是数字
        if (StringUtils.isNumber(str.charAt(from))) {
            throw new UniversalScriptException("script.stderr.message110", str, from);
        }

        for (int i = from; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!StringUtils.isLetter(c) && !StringUtils.isNumber(c) && c != '_') {
                return i;
            }
        }
        return str.length();
    }

    public int indexOf(CharSequence script, char[] dest, int from) {
        if (script == null) {
            return -1;
        }
        if (dest == null || dest.length == 0 || from < 0) {
            throw new IllegalArgumentException(script + ", " + Arrays.toString(dest) + ", " + from);
        }

        for (int i = from; i < script.length(); i++) {
            char c = script.charAt(i);

            // 忽略字符串常量
            if (c == '\'') {
                int end = this.indexOfQuotation(script, i);
                if (end == -1) {
                    return -1;
                } else {
                    i = end;
                    continue;
                }
            }

            // 忽略字符串变量
            else if (c == '\"') {
                int end = this.indexOfDoubleQuotation(script, i);
                if (end == -1) {
                    return -1;
                } else {
                    i = end;
                    continue;
                }
            } else {
                for (char ac : dest) {
                    if (c == ac) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    public int indexOfVariableMethod(String str, int from) {
        if (str == null) {
            return -1;
        }
        if (from < 0) {
            throw new IllegalArgumentException(str + ", " + from);
        }

        for (int i = from + 1; i < str.length(); i++) {
            char c = str.charAt(i);

            // 忽略括号中的空白字符与逗号
            if (c == '(') {
                int end = this.indexOfParenthes(str, i);
                if (end == -1) {
                    throw new IllegalArgumentException(ResourcesUtils.getMessage("script.stderr.message086", str, ")"));
                }
                i = end;
                continue;
            }

            // 忽略中括号中的字符串信息
            else if (c == '[') {
                int end = this.indexOfBracket(str, i);
                if (end == -1) {
                    throw new IllegalArgumentException(ResourcesUtils.getMessage("script.stderr.message087", str));
                }
                i = end;
                continue;
            } else if (!StringUtils.isLetter(c) && !StringUtils.isNumber(c) && c != '_' && c != '.') {
                return i;
            }
        }
        return str.length();
    }

    public int indexOfInteger(CharSequence script, int from) {
        if (script == null || from < 0 || from >= script.length()) {
            return -1;
        }

        char c = script.charAt(from);
        if (c == '0') {
            return from + 1;
        }

        for (int i = from + 1; i < script.length(); i++) {
            if (!StringUtils.isNumber(script.charAt(i))) {
                return i;
            }
        }
        return script.length();
    }

    public String unescapeSQL(String str) {
        return StringUtils.replaceAll(str, ESCAPE_STRING, "$");
    }

    public String removeSide(CharSequence str, char left, char right) {
        if (str == null) {
            return null;
        }

        if (this.containsSide(str, left, right)) {
            int sp = 0, len = str.length(), ep = len - 1;
            while (sp < len && Character.isWhitespace(str.charAt(sp))) {
                sp++;
            }
            while (sp <= ep && Character.isWhitespace(str.charAt(ep))) {
                ep--;
            }
            return str.subSequence(sp + 1, ep).toString();
        } else {
            return str.toString();
        }
    }

    public boolean containsSide(CharSequence str, char lc, char rc) {
        if (Character.isWhitespace(lc) || Character.isWhitespace(rc)) {
            throw new IllegalArgumentException();
        }
        if (str == null || str.length() <= 1) {
            return false;
        }

        char first = ' ', last = ' ';
        int left = 0, len = str.length(), right = len - 1;
        while (left < len && (Character.isWhitespace((first = str.charAt(left))))) {
            left++;
        }

        if (first != lc) { // 第一个非空白字符不是字符参数lc
            return false;
        }

        while (left <= right && Character.isWhitespace(last = str.charAt(right))) {
            right--;
        }

        if (last != rc) { // 最后一个字符不是双引号
            return false;
        } else if (first == '\'') { // 引号需要成对出现
            return this.indexOfQuotation(str, left) == right;
        } else if (first == '"') { // 引号需要成对出现
            return this.indexOfDoubleQuotation(str, left) == right;
        } else {
            return true;
        }
    }

    public boolean isBlank(CharSequence str) {
        if (str != null && str.length() > 0) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                if (!Character.isWhitespace(c) && c != this.token && c != this.comment) {
                    return false;
                }
            }
        }
        return true;
    }

    public String trim(CharSequence str, int left, int right, char... array) {
        if (str == null) {
            return null;
        }

        char c;
        int sp = 0, len = str.length(), ep = len - 1;

        if (left == 0) { // 删除左侧空白字符
            while (sp < len && Character.isWhitespace((c = str.charAt(sp)))) {
                sp++;
            }
        } else if (left == 1) { // 删除左侧空白字符与语句分隔符
            while (sp < len && (Character.isWhitespace((c = str.charAt(sp))) || c == this.token)) {
                sp++;
            }
        } else if (left == 2) { // 删除左侧空白字符与语句分隔符与字符数组中的字符
            while (sp < len && (Character.isWhitespace((c = str.charAt(sp))) || c == this.token || StringUtils.inArray(c, array))) {
                sp++;
            }
        }

        if (right == 0) { // 删除右侧空白字符
            while (sp <= ep && Character.isWhitespace(c = str.charAt(ep))) {
                ep--;
            }
        } else if (right == 1) { // 删除右侧空白字符与语句分隔符
            while (sp <= ep && (Character.isWhitespace(c = str.charAt(ep)) || c == this.token)) {
                ep--;
            }
        } else if (right == 2) { // 删除右侧空白字符与语句分隔符与字符数组中的字符
            while (sp <= ep && (Character.isWhitespace(c = str.charAt(ep)) || c == this.token || StringUtils.inArray(c, array))) {
                ep--;
            }
        }

        return str.subSequence(sp, ep + 1).toString();
    }

    /**
     * 对字符串参数 str 执行命令替换和变量替换 <br>
     * 命令替换: 替换 `` 中的命令 <br>
     * 变量替换: 替换（会替换单引号中的变量占位符） $name ${name} $? $# $0 $1 格式的变量占位符 <br>
     * 转义字符: 替换 &ads; 为 $ <br>
     *
     * @param session 用户会话信息
     * @param sql     SQL语句
     * @return 替换后的字符串
     */
    public String replaceSQLVariable(UniversalScriptSession session, UniversalScriptContext context, String sql) {
        if (sql == null) {
            return null;
        }

//        UniversalScriptStdout stdout = context.getEngine().getStdout();
//        UniversalScriptStderr stderr = context.getEngine().getStderr();
        UniversalScriptFormatter format = context.getEngine().getFormatter();

//        sql = this.replaceSubCommand(session, context, stdout, stderr, sql, false);
        sql = this.replaceShellSpecialVariable(session, sql, false);
        sql = this.replaceShellVariable(sql, new ScriptVariableMap(session, context), format, false, true);
        return this.unescapeSQL(sql);
    }

    /**
     * 对字符串参数 str 执行命令替换和变量替换 <br>
     * 命令替换: 替换 `` 中的命令 <br>
     * 变量替换: 替换（会保留单引号中的变量占位符） $name ${name} $? $# $0 $1 格式的变量占位符 <br>
     *
     * @param session      用户会话信息
     * @param str          字符串
     * @param keepVariable true表示保留变量值是null的变量占位符 false表示删除变量值是null的变量占位符
     * @param evalInnerCmd true表示执行命令替换 false表示不执行命令替换
     * @return 替换后的字符串
     */
    public String replaceShellVariable(UniversalScriptSession session, UniversalScriptContext context, String str, boolean keepVariable, boolean evalInnerCmd) {
        if (str == null) {
            return null;
        }

        UniversalScriptStdout stdout = context.getEngine().getStdout();
        UniversalScriptStderr stderr = context.getEngine().getStderr();
        UniversalScriptFormatter format = context.getEngine().getFormatter();

        if (evalInnerCmd) {
            str = this.replaceSubCommand(session, context, stdout, stderr, str, true);
        }

        str = this.replaceShellSpecialVariable(session, str, true);
        str = this.replaceShellVariable(str, new ScriptVariableMap(session, context), format, true, keepVariable);
        return str;
    }

    /**
     * 执行命令替换 <br>
     * 脚本引擎执行字符串str中 `` 中的内容，并将运行输出的标准输出信息替换到字符串str中
     *
     * @param session     用户会话信息
     * @param context     脚本引擎上下文信息
     * @param stdout      标准信息输出接口
     * @param stderr      错误信息输出接口
     * @param script      脚本语句
     * @param ignoreQuote true表示不会替换单引号中的命令
     * @return 替换后的字符串
     */
    public String replaceSubCommand(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, String script, boolean ignoreQuote) {
        if (script == null) {
            return null;
        }

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);

            // 转义字符
            if (this.escape && c == this.escapeChar) {
                i++;
                continue;
            }

            // 忽略单引号
            if (ignoreQuote && c == '\'') {
                int end = this.indexOfQuotation(script, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            if (c == '`') {
                int end = this.indexOfAccent(script, i);
                if (end == -1) {
                    continue;
                } else {
                    int begin = i + 1;
                    String command = script.substring(begin, end);
                    ScriptStdbuf cache = new ScriptStdbuf(stdout);
                    int exitcode = context.getEngine().evaluate(session, context, cache, stderr, command);
                    if (exitcode != 0) {
                        throw new UniversalScriptException("script.stderr.message052", command);
                    }

                    String message = cache.rtrimBlank();
                    int length = command.length() + 2;
                    script = StringUtils.replace(script, i, length, message);
                    i--;
                    continue;
                }
            }
        }
        return script;
    }

    /**
     * 替换字符串参数str中的占位符, 如: $? $# $1 $2 <br>
     * <br>
     * $$ 表示用户会话编号 <br>
     * $? 表示上一个命令执行返回值 <br>
     * $# 表示命令输入参数的个数 <br>
     * $0 表示命令名 <br>
     * $1 表示第一个参数值 <br>
     * $2 表示第二个参数值 <br>
     * $3 表示第三个参数值 <br>
     *
     * @param session     用户会话信息
     * @param str         字符串
     * @param ignoreQuote true表示不会替换单引号中的内置命令
     * @return 替换后的字符串
     */
    public String replaceShellSpecialVariable(UniversalScriptSession session, String str, boolean ignoreQuote) {
        if (str == null) {
            return null;
        }

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // 转义字符
            if (this.escape && c == this.escapeChar) {
                i++;
                continue;
            }

            // 忽略单引号
            if (ignoreQuote && c == '\'') {
                int end = this.indexOfQuotation(str, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            if (c == '$') {
                int next = i + 1;
                if (next >= str.length()) {
                    break;
                }

                char nc = str.charAt(next);
                if (nc == '{') { // 变量
                    int end = this.indexOfBrace(str, next);
                    if (end != -1) {
                        i = end;
                    }
                    continue;
                }

                if (nc == '?') { // 上一个命令的返回值
                    Integer exitcode = session.getMainProcess().getExitcode();
                    str = str.substring(0, i) + (exitcode == null ? "" : exitcode) + str.substring(next + 1);
                    i--;
                    continue;
                }

                if (nc == '$') { // 当前会话的编号
                    str = str.substring(0, i) + session.getId() + str.substring(next + 1);
                    i--;
                    continue;
                }

                // 参数数组
                String[] args = session.getFunctionParameter(); // 方法参数
                if (args == null) {
                    args = session.getScriptParameters(); // 脚本参数
                }

                if (nc == '#') { // 参数个数
                    str = str.substring(0, i) + ((args != null && args.length >= 1) ? args.length - 1 : 0) + str.substring(next + 1);
                    i--;
                    continue;
                }

                if (StringUtils.isNumber(nc)) { // $0 表示方法名 $1 表示第一个参数
                    int end = this.indexOfInteger(str, next);
                    if (end != -1) {
                        String number = str.substring(next, end);
                        int index = Integer.parseInt(number);
                        if (args != null && index < args.length) {
                            str = str.substring(0, i) + args[index] + str.substring(end);
                            i--;
                        } else {
                            i = end - 1;
                        }
                    }
                    continue;
                }
            }
        }
        return str;
    }

    /**
     * 替换shell文本中的变量 <br>
     * 忽略引号中的字符串常量 <br>
     * 变量格式: ${name} $name
     *
     * @param str            字符串
     * @param map            变量名与变量值集合
     * @param convert        字符串转换接口, 可以为null
     * @param ignoreQuote    true表示不会替换单引号中的占位符
     * @param keepBlankValue true表示变量值是null时,保留变量名的占位符
     * @return 替换后的字符串
     */
    public String replaceShellVariable(String str, Map<?, ?> map, Format convert, boolean ignoreQuote, boolean keepBlankValue) {
        if (str == null || map == null) {
            return null;
        }

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // 转义字符
            if (this.escape && c == this.escapeChar) {
                i++;
                continue;
            }

            // 忽略单引号
            if (ignoreQuote && c == '\'') {
                int end = this.indexOfQuotation(str, i);
                if (end != -1) {
                    i = end;
                }
                continue;
            }

            if (c == '$') { // variable
                int next = i + 1;
                if (next >= str.length()) {
                    continue;
                }

                char nc = str.charAt(next);
                if (nc == '{') { // ${name}
                    int end = this.indexOfBrace(str, next);
                    if (end != -1) {
                        String name = str.substring(next + 1, end); // variable name
                        if (map.containsKey(name)) {
                            Object value = map.get(name);
                            String valStr = convert == null ? StringUtils.toString(value) : convert.format(value);

                            if (log.isTraceEnabled()) {
                                String newStr = str.substring(0, i) + valStr + str.substring(end + 1);
                                log.trace("script.stderr.message112", "replaceShellVariable", name, value, valStr, str, newStr);
                            }

                            str = str.substring(0, i) + valStr + str.substring(end + 1);
                            i--; // 替换字符串之后继续从原 $ 字符所在位置开始搜索
                            continue;
                        }

                        if (log.isTraceEnabled()) {
                            log.trace("script.stderr.message111", str, "replaceShellVariable", str, name);
                        }

                        if (!keepBlankValue) {
                            str = str.substring(0, i) + str.substring(end + 1);
                            i--; // 替换字符串之后继续从原 $ 字符所在位置开始搜索
                        }
                    }
                    continue;
                }

                // $name
                if (StringUtils.isLetter(nc) || nc == '_') { // 变量名只能是英文与下划线开头
                    int end = this.indexOfVariableName(str, next);
                    String name = str.substring(next, end);
                    if (map.containsKey(name)) {
                        Object value = map.get(name);
                        String valStr = (convert == null) ? StringUtils.toString(value) : convert.format(value);

                        if (log.isTraceEnabled()) {
                            String newStr = str.substring(0, i) + valStr + str.substring(end);
                            log.trace("script.stderr.message112", "replaceShellVariable", name, value, valStr, str, newStr);
                        }

                        str = str.substring(0, i) + valStr + str.substring(end);
                        i--;
                        continue;
                    }

                    if (log.isTraceEnabled()) {
                        log.trace("script.stderr.message111", str, "replaceShellVariable", str, name);
                    }

                    if (!keepBlankValue) {
                        str = str.substring(0, i) + str.substring(end);
                        i--;
                    }
                    continue;
                }
            }
        }

        return str;
    }
}
