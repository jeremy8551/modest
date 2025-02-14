package cn.org.expect.springboot.starter;

import java.util.List;

import cn.org.expect.log.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SpringBoot 场景启动器的属性
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/9/5 11:11
 */
@Component
@ConfigurationProperties("modest")
public class ModestProperties {

    /**
     * 日志属性
     */
    private LogProperties log;

    public LogProperties getLog() {
        return log;
    }

    public void setLog(LogProperties log) {
        this.log = log;
    }

    /**
     * 日志属性
     */
    public static class LogProperties {

        /**
         * 设置默认的日志级别, 如：trace、debug、info、warn、error
         * <br>
         * slf4j 表示使用日志门面接口输出日志 <br>
         * <br>
         * pattern 表示日志的输出格式，表达式规则与 log4j 类似, 日志格式详见 cn.org.expect.log.apd.LogPattern 类
         * <br>
         * sout 表示使用控制台输出日志 <br>
         * sout+ 表示使用默认的日志格式，在控制台输出日志 <br>
         * sout+pattern 表示使用指定的日志格式，在控制台输出日志 <br>
         * <br>
         * sout:info 表示使用控制台输出日志 <br>
         * sout+:info 表示使用默认的日志格式，在控制台输出日志 <br>
         * sout+pattern:info 表示使用指定的日志格式，在控制台输出日志 <br>
         * <br>
         * >${temp}/file.log 表示将日志输出到指定文件 <br>
         * >${temp}/file.log+ 表示使用默认的日志格式，将日志输出到指定文件 <br>
         * >${temp}/file.log+pattern 表示使用指定的日志格式，将日志输出到指定文件 <br>
         */
        private String level;

        /**
         * 针对不同的Java包设置不同的日志级别
         * <br>
         * 注意: 只有 modest.log.level 属性为 sout 时才生效
         * <br>
         * <br>
         * 如: 只设置包 cn.org.expect.db 的日志级别
         * <br>
         * cn.org.expect.db:debug <br>
         */
        private List<String> packages;

        /** 是否打印日志模块中用于跟踪代码位置的堆栈信息 */
        private boolean printTrace;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            LogFactory.load(level);
            this.level = level;
        }

        public List<String> getPackages() {
            return packages;
        }

        public void setPackages(List<String> packages) {
            for (String expression : packages) {
                LogFactory.load(expression);
            }
            this.packages = packages;
        }

        public boolean isPrintTrace() {
            return printTrace;
        }

        public void setPrintTrace(boolean printTrace) {
            this.printTrace = printTrace;
        }
    }
}
