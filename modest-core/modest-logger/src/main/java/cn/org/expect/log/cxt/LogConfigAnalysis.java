package cn.org.expect.log.cxt;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.log.LogContext;
import cn.org.expect.log.LogFactory;
import cn.org.expect.log.LogLevel;
import cn.org.expect.log.PatternConsoleAppender;
import cn.org.expect.log.PatternLogBuilder;
import cn.org.expect.log.apd.file.FileAppender;
import cn.org.expect.log.slf4j.Slf4jLogBuilder;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 日志配置信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/21
 */
public class LogConfigAnalysis {

    /**
     * 解析日志配置信息
     *
     * @param context 日志上下文信息
     * @param args    配置信息 <br>
     *                <br>
     *                info: 设置默认的日志级别 <br>
     *                :info 设置默认的日志级别 <br>
     *                <br>
     *                cn.org.expect.db:debug 设置包名下的日志级别 <br>
     *                info:cn.org.expect.db 设置包名下的日志级别 <br>
     *                <br>
     *                slf4j 使用日志门面接口输出日志 <br>
     *                <br>
     *                sout 直接输出日志 <br>
     *                sout+ 使用格式输出日志 <br>
     *                sout+pattern 使用指定格式输出日志 <br>
     *                <br>
     *                sout:info 使用指定日志级别输出日志 <br>
     *                sout+:info 使用指定日志级别输出日志 <br>
     *                sout+pattern:info 使用指定日志级别输出日志 <br>
     *                <br>
     *                >${temp}/file.log 不带格式输出日志 <br>
     *                >${temp}/file.log+ 带格式输出日志 <br>
     *                >${temp}/file.log+pattern 使用指定格式输出日志 <br>
     * @return 返回与日志配置无关的配置信息
     */
    public static String[] parse(LogContext context, String... args) {
        if (context == null) {
            context = LogFactory.getContext();
        }

        return new LogConfigAnalysis(context).parseSome(args);
    }

    /** 日志上下文信息 */
    private LogContext context;

    protected LogConfigAnalysis(LogContext context) {
        this.context = Ensure.notNull(context);
    }

    /**
     * 加载日志配置信息
     *
     * @param args 参数数组
     * @return 返回true表示解析成功 false表示失败
     */
    protected String[] parseSome(String[] args) {
        List<String> list = new ArrayList<String>(args.length);
        for (String str : args) {
            String[] array = StringUtils.split(str, ',');
            for (String field : array) {
                if (StringUtils.isNotBlank(field)) {
                    String config = StringUtils.trimBlank(field);
                    if (!this.readOne(config)) {
                        list.add(config);
                    }
                }
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 解析日志配置信息
     *
     * @param str 日志配置信息
     *            sout: 表示使用控制台输出info级别的日志
     *            slf4j: 表示使用slf4j输出debug级别的日志
     *            :info 设置默认的日志级别
     *            info: 设置默认的日志级别
     *            cn.org.expect.db:debug 设置包名下的日志级别
     *            info:cn.org.expect.db 设置包名下的日志级别
     * @return 返回true表示解析成功 false表示失败
     */
    protected boolean readOne(String str) {
        if (str == null) {
            return false;
        }

        int index = str.indexOf(':');
        if (index == -1) { // 只有一个字段
            if (LogLevel.is(str)) { // 如果是日志级别
                this.context.updateLevel("", LogLevel.of(str));
                return true;
            } else {
                return this.parseBuilder(str);
            }
        }

        String[] array = new String[2];
        array[0] = StringUtils.trimBlank(str.substring(0, index));
        array[1] = StringUtils.trimBlank(str.substring(index + 1));

        boolean value = this.parseBuilders(array, str);

        // 如果第一个元素是日志级别
        if (LogLevel.is(array[0])) {
            this.context.updateLevel(array[1], LogLevel.of(array[0]));
            return true;
        }

        // 如果第二个元素是日志级别
        if (LogLevel.is(array[1])) {
            this.context.updateLevel(array[0], LogLevel.of(array[1]));
            return true;
        }

        return value;
    }

    /**
     * 解析日志工厂配置信息
     *
     * @param array      数组
     * @param expression 表达式
     * @return 返回true表示解析成功 false表示解析失败
     */
    protected boolean parseBuilders(String[] array, String expression) {
        boolean b0 = this.parseBuilder(array[0]);
        boolean b1 = this.parseBuilder(array[1]);

        // 第一个元素与第二个元素不能同时指定日志工厂，如：sout:sout 或 sout:slf4j
        if (b0 && b1) {
            throw new IllegalArgumentException(expression);
        }

        // 如果第一个元素指定日志工厂，则需要将配置信息删除
        if (b0) {
            array[0] = "";
        } else if (b1) {  // 如果第二个元素指定日志工厂，则需要将配置信息删除
            array[1] = "";
        }

        return b0 || b1;
    }

    /**
     * 解析参数中的日志工厂
     *
     * @param expression 配置信息
     */
    protected boolean parseBuilder(String expression) {
        // >${temp}/file.log 不带格式输出日志
        // >${temp}/file.log+ 带格式输出日志
        // >${temp}/file.log+pattern 使用指定格式输出日志
        if (expression.length() >= 1 && expression.charAt(0) == '>') {
            boolean append = false;
            String logfileExpr = StringUtils.trimBlank(expression.substring(1));
            if (logfileExpr.length() > 0 && logfileExpr.charAt(0) == '>') { // 判断是否是 >> 追加模式写入日志
                logfileExpr = logfileExpr.substring(1);
                append = true;
            }

            String pattern = "";
            int index = logfileExpr.indexOf('+');
            if (index != -1) {
                pattern = LogFactory.getPattern(logfileExpr.substring(index + 1), true);
                logfileExpr = StringUtils.trimBlank(logfileExpr.substring(0, index));
            }

            // 如果没有日志文件路径，则抛出异常
            if (logfileExpr.length() == 0) {
                throw new IllegalArgumentException(expression);
            }

            String logfilepath = StringUtils.replaceProperties(StringUtils.replaceEnvironment(logfileExpr));
            String charsetName = Settings.getFileEncoding();
            this.context.setBuilder(new PatternLogBuilder());
            new FileAppender(logfilepath, charsetName, pattern, append).setup(this.context);
            return true;
        }

        // sout 直接输出日志
        // sout+ 使用格式输出日志
        // sout+pattern 使用指定格式输出日志
        int size = "sout".length();
        if (StringUtils.startsWith(expression, "sout", 0, true, false)) { // 使用控制台输出
            if (expression.length() == size) {
                new PatternConsoleAppender("").setup(this.context);
                this.context.setBuilder(new PatternLogBuilder());
                return true;
            } else {
                String pattern = "";
                if (expression.charAt(size) == '-') { // 关闭控制台输出
                    this.context.removeAppender(PatternConsoleAppender.class);
                    return true;
                } else if (expression.charAt(size) == '+') { // sout+ 右侧的都是日志格式信息
                    String str = StringUtils.trimBlank(expression.substring("sout+".length()));
                    pattern = LogFactory.getPattern(str, true);
                } else {
                    throw new IllegalArgumentException(expression);
                }

                this.context.setBuilder(new PatternLogBuilder());
                new PatternConsoleAppender(pattern).setup(this.context);
                return true;
            }
        }

        // 使用slf4j输出
        if ("slf4j".equalsIgnoreCase(expression)) {
            this.context.setBuilder(new Slf4jLogBuilder());
            return true;
        }

        return false;
    }

}
