package cn.org.expect.springboot.starter.configuration;

import java.io.File;
import java.util.List;

import cn.org.expect.database.Jdbc;
import cn.org.expect.expression.Expression;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;
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
    private Log log;

    /**
     * 设置默认字符集
     */
    private String charset;

    /**
     * 临时文件存储目录
     */
    private String temp;

    /**
     * IO流属性
     */
    private IOConfig io;

    /**
     * 国际化资源信息属性
     */
    private Resource resource;

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        CharsetUtils.set(charset);
        this.charset = charset;
    }

    public String getTemp() {
        return this.temp;
    }

    public void setTemp(String temp) {
        FileUtils.setTempDir(new File(temp));
        this.temp = temp;
    }

    public IOConfig getIo() {
        return io;
    }

    public void setIo(IOConfig io) {
        this.io = io;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * 日志属性
     */
    public static class Log {

        /**
         * 详见: {@linkplain cn.org.expect.log.LogSettings#load(String...)}
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

        /**
         * 是否打印日志模块中用于跟踪代码位置的堆栈信息
         */
        private boolean printTrace;

        /**
         * 是否打印SQL语句
         */
        private boolean printSql;

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
            LogFactory.load(packages.toArray(new String[0]));
            this.packages = packages;
        }

        public boolean isPrintTrace() {
            return printTrace;
        }

        public void setPrintTrace(boolean printTrace) {
            this.printTrace = printTrace;
        }

        public boolean isPrintSql() {
            return printSql;
        }

        public void setPrintSql(boolean printSql) {
            Settings.setProperty(Jdbc.PROPERTY_DATABASE_LOG, printSql);
            this.printSql = printSql;
        }
    }

    public static class Resource {

        /**
         * 设置国际化资源文件的地区语言信息
         */
        private String locale;

        /**
         * 设置国际化资源文件
         */
        private String external;

        /**
         * 国际化资源名（不包含扩展名，扩展名默认是 .properties）
         */
        private String name;

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            Settings.setProperty(ResourcesUtils.PROPERTY_RESOURCE_LOCALE, locale);
            ResourcesUtils.getRepository().load();
            this.locale = locale;
        }

        public String getExternal() {
            return external;
        }

        public void setExternal(String external) {
            Settings.setProperty(ResourcesUtils.PROPERTY_RESOURCE, external);
            ResourcesUtils.getRepository().load();
            this.external = external;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            Settings.setProperty(ResourcesUtils.PROPERTY_RESOURCE_NAME, name);
            ResourcesUtils.getRepository().load();
            this.name = name;
        }
    }

    public static class IOConfig {

        /**
         * 输入流中字符数组的长度
         */
        private String charArrayLength;

        /**
         * 输入流中字节数组的长度
         */
        private String byteArrayLength;

        public String getCharArrayLength() {
            return charArrayLength;
        }

        public void setCharArrayLength(String charArrayLength) {
            Settings.setProperty(IO.PROPERTY_CHAR_ARRAY_LENGTH, new Expression(charArrayLength).longValue());
            this.charArrayLength = charArrayLength;
        }

        public String getByteArrayLength() {
            return byteArrayLength;
        }

        public void setByteArrayLength(String byteArrayLength) {
            Settings.setProperty(IO.PROPERTY_BYTE_ARRAY_LENGTH, new Expression(byteArrayLength).longValue());
            this.byteArrayLength = byteArrayLength;
        }
    }
}
