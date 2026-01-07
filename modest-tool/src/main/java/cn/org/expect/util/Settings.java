package cn.org.expect.util;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.org.expect.Modest;

/**
 * JVM参数工具
 *
 * @author jeremy8551@gmail.com
 * @createtime 2011-08-20
 */
public class Settings {

    /** 文件系统的换行符 */
    protected final static String LINE_SEPARATOR = System.getProperty("line.separator");

    /** 属性集合 */
    protected final static Map<String, String> map = new ConcurrentHashMap<String, String>();

    /**
     * 判断是否存在变量
     *
     * @param name 变量名
     * @return 返回true表示存在变量
     */
    public static boolean containsVariable(String name) {
        return System.getenv().containsKey(name) || System.getProperties().containsKey(name);
    }

    /**
     * 返回变量值 <br>
     * 如果在不同的域中存在同名的变量名时，按域的优先级从高到低返回变量值，域的优先级如下：<br>
     * {@literal 局部变量域 > 全局变量域 > 环境变量域 }
     */
    public static String getVariable(String name) {
        if (System.getenv().containsKey(name)) {
            return System.getenv(name);
        }

        if (System.getProperties().containsKey(name)) {
            return System.getProperty(name);
        }

        return null;
    }

    /**
     * 返回属性
     *
     * @param key 属性名
     * @return 属性值
     */
    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = map.get(key);
        }
        return value == null ? "" : value;
    }

    /**
     * 设置属性
     *
     * @param key   属性名
     * @param value 属性值
     */
    public static void setProperty(String key, Object value) {
        map.put(key, String.valueOf(value));
    }

    /**
     * 返回当前项目名
     *
     * @return 项目名
     */
    public static String getProjectName() {
        return Modest.class.getSimpleName().toLowerCase();
    }

    /**
     * 返回项目目录
     *
     * @return 目录，例如: /home/user/.modest
     */
    public static File getProjectHome() {
        return new File(Settings.getUserHome(), "." + Settings.getProjectName());
    }

    /**
     * 返回项目包名
     *
     * @return 包名
     */
    public static String getPackageName() {
        return Modest.class.getPackage().getName();
    }

    /**
     * 返回属性名
     *
     * @param name 属性名
     * @return 属性名
     */
    public static String getPropertyName(String name) {
        return Modest.class.getPackage().getName() + "." + name;
    }

    /**
     * 返回java虚拟机当前的文件字符集
     * <p>
     * return System.getProperty("file.encoding");
     *
     * @return 属性值
     */
    public static String getFileEncoding() {
        return System.getProperty("file.encoding");
    }

    /**
     * 影响文件名字符集
     *
     * @return 属性值
     */
    public static String getFilenameEncoding() {
        return System.getProperty("sun.jnu.encoding");
    }

    /**
     * jvm版本
     * <p>
     * return System.getProperty("java.vm.version");
     * 1.5.0_22-b03
     *
     * @return 属性值
     */
    public static String getJavaVmVersion() {
        return System.getProperty("java.vm.version");
    }

    /**
     * jvm供应商
     * <p>
     * return System.getProperty("java.vm.vendor");
     * Sun Microsystems Inc.
     *
     * @return 属性值
     */
    public static String getJavaVmVendor() {
        return System.getProperty("java.vm.vendor");
    }

    /**
     * jvm名
     * <p>
     * return System.getProperty("java.vm.name");
     * Java HotSpot(TM) Client VM
     *
     * @return 属性值
     */
    public static String getJavaVmName() {
        return System.getProperty("java.vm.name");
    }

    /**
     * jvm默认使用的字符集的类包
     * <p>
     * return System.getProperty("file.encoding.pkg");
     *
     * @return 返回字符串 sun.io
     */
    public static String getFileEncodingPkg() {
        return System.getProperty("file.encoding.pkg");
    }

    /**
     * 国家代码
     * <p>
     * return System.getProperty("user.country");
     * CN
     *
     * @return 属性值
     */
    public static String getUserCountry() {
        return System.getProperty("user.country");
    }

    /**
     * 语言代码
     * <p>
     * return System.getProperty("user.language");
     * zh
     *
     * @return 属性值
     */
    public static String getUserLanguage() {
        return System.getProperty("user.language");
    }

    /**
     * 时区
     * <p>
     * return System.getProperty("user.timezone");
     * /Asia/Shanghai
     *
     * @return 属性值
     */
    public static String getUserTimezone() {
        return System.getProperty("user.timezone");
    }

    /**
     * JAVA_HOME 参数值
     * <p>
     * return System.getProperty("java.home");
     * C:\Program Files (x86)\Java\jdk1.5.0_22\jre
     *
     * @return 属性值
     */
    public static File getJavaHome() {
        return new File(System.getProperty("java.home"));
    }

    /**
     * 返回运行 Java 命令的目录
     *
     * @return 属性值
     */
    public static File getUserDir() {
        return new File(System.getProperty("user.dir"));
    }

    /**
     * user.home 参数值
     * <p>
     * return System.getProperty("user.home");
     * C:\Users\etl
     *
     * @return 属性值
     */
    public static File getUserHome() {
        return new File(System.getProperty("user.home"));
    }

    /**
     * user.name 参数值
     * JVM虚拟机所在操作系统用户名
     *
     * @return 属性值
     */
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     * 返回临时文件目录
     *
     * @return 目录
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * zh_CN
     *
     * @return 属性值
     */
    public static String getUserLocale() {
        StringBuilder buf = new StringBuilder(10);
        buf.append(System.getProperty("user.language")); // zh
        String country = System.getProperty("user.country"); // CN
        if (country != null && country.length() > 0) {
            buf.append('_');
            buf.append(country);
        }
        return buf.toString();
    }

    /**
     * 返回当前Java虚拟机启动命令
     * com.ibm.wsspi.bootstrap.WSPreLauncher -nosplash -application com.ibm.ws.bootstrap.WSLauncher com.ibm.ws.runtime.WsServer /was/IBM/WebSphere/AppServer/profiles/AppSrv01/config LocalhostNode01Cell LocalhostNode01 server1
     *
     * @return 属性值
     */
    public static String getJavaCommand() {
        String value = System.getProperty("sun.java.command");
        return value == null ? null : value.trim();
    }

    /**
     * 返回行分隔符
     *
     * @return 行分隔符
     */
    public static String getLineSeparator() {
        return LINE_SEPARATOR;
    }

    /**
     * 返回 JDK 的大版本号
     *
     * @return 大版本号 <br>
     * 如: <br>
     * JDK1.4 返回 4 <br>
     * JDK1.8 返回 8 <br>
     * JDK21 返回 21 <br>
     */
    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version == null) {
            throw new NullPointerException();
        }

        String[] array = version.split("\\.");
        if (array.length == 0) {
            throw new UnsupportedOperationException(version);
        }

        if (array.length == 1) {
            return Integer.parseInt(version);
        }

        return array[0].equals("1") ? Integer.parseInt(array[1]) : Integer.parseInt(array[0]);
    }
}
