package cn.org.expect.ioc;

import java.io.File;
import java.io.FileOutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import cn.org.expect.ioc.internal.ScanPattern;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Logs;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;

/**
 * 类扫描器
 *
 * @author jeremy8551@gmail.com
 */
public class EasyClassScanner extends Terminator {
    private final static Log log = LogFactory.getLog(EasyClassScanner.class);

    /** 扫描的 JAVA 包名与 jar 文件名 */
    public final static String PROPERTY_SCAN_PKG = Settings.getPropertyName("scan");

    /** 组件仓库 */
    private EasyBeanRepository repository;

    /** 类加载器 */
    private final ClassLoader classLoader;

    /** 需要扫描的JAVA包名 */
    private final LinkedHashSet<String> includePackageNames;

    /** 扫描时，需要排除的包名 */
    private final HashSet<String> excludePackageNames;

    /** 防止重复扫描相同 jar 文件建立的集合 */
    private final HashSet<File> jarFiles;

    /** 类扫描规则 */
    private List<EasyClassScan> scans;

    /**
     * 初始化
     *
     * @param classLoader 类加载器
     */
    public EasyClassScanner(ClassLoader classLoader) {
        this.includePackageNames = new LinkedHashSet<String>();
        this.excludePackageNames = new HashSet<String>();
        this.jarFiles = new HashSet<File>();
        this.classLoader = Ensure.notNull(classLoader);
    }

    /**
     * 扫描类
     *
     * @param repository 组件仓库
     * @param list       包名集合 <br>
     *                   java.lang 表示扫描包下的类 <br>
     *                   !java.lang.util（排除包名下的类）
     * @return 返回已加载类信息的个数
     */
    public synchronized int load(EasyBeanRepository repository, List<String> list) {
        this.repository = Ensure.notNull(repository);
        this.scans = repository.loadBean(EasyClassScan.class);
        this.parse(list, this.includePackageNames, this.excludePackageNames);

        // 扫描包中的类
        int count = 0;
        this.terminate = false;
        this.jarFiles.clear();
        try {
            for (String packageName : this.includePackageNames) {
                count += this.scanPackage(packageName);
                if (this.terminate) {
                    break;
                }
            }
        } finally {
            this.jarFiles.clear();
        }

        if (log.isDebugEnabled()) {
            log.debug("ioc.stdout.message006", this.includePackageNames.isEmpty() ? "" : " " + this.includePackageNames + " ", count);
        }
        return count;
    }

    /**
     * 解析参数 {@code packageNames}，将扫描的包名与排除的包名，分别添加到第二个参数与第三个参数中
     *
     * @param packageNames 包扫描配置
     * @param includes     扫描的包名
     * @param excludes     排除的包名
     */
    protected void parse(List<String> packageNames, LinkedHashSet<String> includes, HashSet<String> excludes) {
        includes.clear();
        excludes.clear();
        for (String str : packageNames) {
            String[] array = StringUtils.split(str, ',');
            for (String value : array) {
                if (StringUtils.isNotBlank(value)) {
                    ScanPattern pattern = new ScanPattern(value);
                    if (pattern.isBlank()) {
                        continue;
                    }

                    if (pattern.isExclude()) {
                        excludes.add(pattern.getPrefix());
                    } else {
                        includes.add(pattern.getPrefix());
                    }
                }
            }
        }
    }

    /**
     * 使用类加载器扫描包名下的所有类
     *
     * @param packageName java包名
     * @return 扫描到复合条件类的总数
     */
    public int scanPackage(String packageName) {
        String uri = packageName.replace('.', '/'); // 格式: xxx/xxx
        if (StringUtils.isNotBlank(uri) && !uri.endsWith("/")) {
            uri += "/"; // 右端需要有分隔符
        }

        Enumeration<URL> enumeration;
        try {
            enumeration = this.classLoader.getResources(uri);
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message022", uri, e);
            }
            return 0;
        }

        int count = 0;
        while (enumeration.hasMoreElements()) {
            URL url = enumeration.nextElement();
            if (url == null) {
                continue;
            }

            if (this.terminate) {
                break;
            }

            String urlPath = StringUtils.decodeJvmUtf8HexString(url.getPath());
            String lowerCase = urlPath.toLowerCase();

            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message018", urlPath, packageName);
            }

            // 加载类文件
            String protocol = url.getProtocol();
            if (protocol.toLowerCase().contains("file")) {
                try {
                    count += this.scanPackage(packageName, urlPath);
                } catch (Throwable e) {
                    if (log.isDebugEnabled()) {
                        log.debug("ioc.stdout.message024", packageName, urlPath, e);
                    }
                }
            }

            // 加载 jar 文件
            else if (protocol.toLowerCase().contains("jar") //
                || lowerCase.endsWith(".jar") //
                || lowerCase.contains(".jar!") //
                || lowerCase.contains(".jar/") //
                || lowerCase.endsWith(".jar/") //
            ) {
                JarFile jarfile = this.loadJarFile(url);
                if (jarfile == null) {
                    continue;
                } else {
                    try {
                        count += this.scanJarFile(jarfile, jarfile.getName());
                    } catch (Throwable e) {
                        log.error(jarfile.getName(), e);
                    }
                }
            }

            // 不能识别的资源信息
            else {
                if (log.isDebugEnabled()) {
                    log.debug("ioc.stdout.message029", protocol, urlPath);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug(""); // 添加一个换行，区分多个类路径的扫描日志
            }
        }

        return count;
    }

    /**
     * 扫描指定包名下的class文件，是否是目标类的子类
     *
     * @param packageName     限制扫描范围的JAVA包名
     * @param packageFilepath 包名所在路径
     * @return 加载类的个数
     */
    public int scanPackage(String packageName, String packageFilepath) {
        int count = 0;

        if (this.isExclude(packageName, true)) {
            return count;
        }

        if (log.isDebugEnabled()) {
            log.debug("ioc.stdout.message021", packageName.length() == 0 ? "" : packageName, packageFilepath);
        }

        File packageFile = new File(packageFilepath);
        if (!packageFile.exists()) {
            if (log.isDebugEnabled()) {
                log.debug("ioc.stdout.message036", packageName, packageFilepath);
            }
            return count;
        }

        File[] files;
        if (packageFile.isDirectory()) {
            files = packageFile.listFiles();
            if (files == null) {
                files = new File[0];
            }
        } else {
            files = new File[]{packageFile};
        }

        for (File file : files) {
            String filename = file.getName();
            String filepath = StringUtils.decodeJvmUtf8HexString(file.getAbsolutePath());
            String lowerPath = filepath.toLowerCase();
            String extName = FileUtils.getFilenameExt(filename).toLowerCase();

            if (this.terminate) {
                break;
            }

            if (log.isTraceEnabled()) {
                if (file.isDirectory()) {
                    log.trace("ioc.stdout.message033", packageName, filename, FileUtils.joinPath(StringUtils.decodeJvmUtf8HexString(packageFile.getAbsolutePath()), filename));
                }
            }

            // scan jar file
            if (lowerPath.endsWith(".jar") //
                || lowerPath.contains(".jar!") //
                || lowerPath.contains(".jar/") //
                || lowerPath.endsWith(".jar/") //
            ) { // 判断文件是否是 jar 包
                JarFile jarFile = this.loadJarFile(file);
                if (jarFile == null) {
                    continue;
                } else {
                    try {
                        count += this.scanJarFile(jarFile, jarFile.getName());
                    } catch (Throwable e) {
                        log.error(jarFile.getName(), e);
                    }
                }
            }

            // scan class file
            else if (file.isFile()) {
                if ("class".equalsIgnoreCase(extName)) {
                    String className = filename.substring(0, filename.lastIndexOf("."));
                    if (StringUtils.isNotBlank(packageName)) {
                        className = packageName + "." + className;
                    }

                    if (this.notInclude(className) || this.isExclude(className, false)) {
                        continue;
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("ioc.stdout.message020", className, filepath);
                    }

                    Class<?> cls = ClassUtils.forName(className, false, classLoader);
                    if (this.load(cls)) {
                        count++;
                    }
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("ioc.stdout.message023", file.getAbsolutePath());
                    }
                }
            }

            // directory
            else {
                String subPackagePath = filename;
                if (StringUtils.isNotBlank(packageFilepath)) {
                    subPackagePath = StringUtils.rtrimBlank(packageFilepath, '/') + "/" + StringUtils.ltrimBlank(subPackagePath, '/');
                }

                String subPackageName = filename;
                if (StringUtils.isNotBlank(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }

                count += this.scanPackage(subPackageName, subPackagePath);
            }
        }

        return count;
    }

    /**
     * 加载 jar 文件
     *
     * @param obj 资源对象
     * @return jar文件
     */
    private JarFile loadJarFile(Object obj) {
        if (obj instanceof URL) { // url 路径
            URL url = (URL) obj;
            String urlPath = StringUtils.decodeJvmUtf8HexString(url.getPath());

            JarFile jarfile;
            try {
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                jarfile = conn.getJarFile();
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("ioc.stdout.message037", urlPath);
                }
                return null;
            }

            File file = new File(StringUtils.decodeJvmUtf8HexString(jarfile.getName()));
            if (this.jarFiles.contains(file)) { // 判断是否已扫描
                if (log.isDebugEnabled()) {
                    log.debug("ioc.stdout.message028", jarfile.getName());
                }
                return null;
            } else {
                this.jarFiles.add(file);
                return jarfile;
            }
        }

        // jar文件
        else if (obj instanceof File) {
            File file = (File) obj;
            if (this.jarFiles.contains(file)) { // 判断是否已扫描jar
                if (log.isDebugEnabled()) {
                    log.debug("ioc.stdout.message028", file);
                }
                return null;
            }

            try {
                JarFile jarfile = new JarFile(file);
                this.jarFiles.add(file);
                return jarfile;
            } catch (Throwable e) {
                if (log.isDebugEnabled()) {
                    log.debug("ioc.stdout.message038", file.getAbsolutePath());
                }
                return null;
            }
        }

        throw new UnsupportedOperationException(StringUtils.toString(obj));
    }

    /**
     * 扫描 jar 文件中的 class 文件
     *
     * @param jarFile jar文件
     * @param name    jar文件描述信息
     * @return 返回加载类的个数
     */
    public int scanJarFile(JarFile jarFile, String name) {
        int count = 0;

        if (jarFile == null) {
            return count;
        }

        if (log.isDebugEnabled()) {
            log.debug("ioc.stdout.message019", StringUtils.decodeJvmUtf8HexString(name));
        }

        Set<String> parents = this.getManifestMF(jarFile);
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            String filename = entry.getName();
            String extName = FileUtils.getFilenameExt(filename);

            if (this.terminate) {
                break;
            }

            // 扫描类文件
            if ("class".equalsIgnoreCase(extName)) {
                String className = filename.substring(0, filename.lastIndexOf(".")).replace('/', '.').replace('\\', '.');

                for (String classpathPrefix : parents) {
                    if (className.startsWith(classpathPrefix)) {
                        className = className.substring(classpathPrefix.length()); // 删除前缀 BOOT-INF/classes
                    }
                }

                if (this.notInclude(className) || this.isExclude(className, false)) {
                    continue;
                }

                if (log.isDebugEnabled()) {
                    log.debug("ioc.stdout.message020", className);
                }

                Class<?> type = ClassUtils.forName(className, false, classLoader);
                if (this.load(type)) {
                    count++;
                }
            }

            // 扫描 jar 中嵌套的 jar 文件
            else if ("jar".equalsIgnoreCase(extName) && !entry.isDirectory()) {
                File tempDir = FileUtils.getTempDir(EasyClassScanner.class.getSimpleName(), "jar");
                File unzipJarfile = FileUtils.allocate(tempDir, filename); // 解压后的jar文件

                if (log.isDebugEnabled()) {
                    log.debug("ioc.stdout.message027", jarFile.getName(), filename, unzipJarfile.getParentFile());
                }

                FileUtils.assertCreateFile(unzipJarfile);
                JarFile newJarfile;
                try {
                    IO.write(jarFile.getInputStream(entry), new FileOutputStream(unzipJarfile), this); // 解压jar包中的jar文件
                    newJarfile = new JarFile(unzipJarfile);
                } catch (Throwable e) {
                    if (log.isDebugEnabled()) {
                        log.debug("ioc.stdout.message039", jarFile.getName(), entry.getName(), e);
                    }
                    continue;
                }

                // 扫描解压后的jar文件
                count += this.scanJarFile(newJarfile, jarFile.getName() + "!/" + filename); // 扫描 jar 文件
            }
        }
        return count;
    }

    /**
     * 返回 jar 文件中 META-INF/MANIFEST.MF 的属性集合
     *
     * @param jarFile jar 文件
     * @return 属性集合
     */
    protected Set<String> getManifestMF(JarFile jarFile) {
        String filepath = jarFile.getName() + "!/META-INF/MANIFEST.MF";
        Set<String> set = new HashSet<String>();
        try {
            Manifest manifest = jarFile.getManifest();
            if (manifest != null) {
                Attributes mainAttributes = manifest.getMainAttributes();
                Set<Map.Entry<Object, Object>> entrySet = mainAttributes.entrySet();
                if (!entrySet.isEmpty()) {
                    if (Logs.isTraceEnabled()) {
                        Logs.trace(filepath);
                    }

                    for (Map.Entry<Object, Object> entry : entrySet) {
                        String value = StringUtils.trimBlank(entry.getValue(), '/', '\\');

                        if (StringUtils.isNotBlank(value) && (value.indexOf('/') != -1 || value.indexOf('\\') != -1)) {
                            String classpathPrefix = value.replace('/', '.').replace('\\', '.') + ".";
                            set.add(classpathPrefix);

                            if (Logs.isTraceEnabled()) {
                                Logs.trace(entry.getKey() + " = " + value + " *");
                            }
                        } else {
                            if (Logs.isTraceEnabled()) {
                                Logs.trace(entry.getKey() + " = " + value);
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            if (Logs.isDebugEnabled()) {
                Logs.debug(filepath, e);
            }
        }

        return set;
    }

    /**
     * 加载组件
     *
     * @param type 类信息
     * @return true表示已加载，false表示未加载
     */
    public boolean load(Class<?> type) {
        if (type == null) {
            return false;
        }

        boolean value = false;
        for (EasyClassScan scan : this.scans) {
            if (scan.load(this.repository, type)) {
                value = true;
            }
        }
        return value;
    }

    /**
     * 判断是一个类名是否满足扫描规则
     *
     * @param className 类名
     * @return 返回true表示类不符合扫描规则
     */
    protected boolean notInclude(String className) {
        for (String name : this.includePackageNames) {
            if (className.startsWith(name)) {
                return false;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("ioc.stdout.message031", className, this.includePackageNames.toString());
        }
        return true;
    }

    /**
     * 判断类名是否匹配
     *
     * @param className 类名
     * @param pkgOrCls  true表示包名 false表示类名
     * @return 返回true表示不需要扫描类
     */
    protected boolean isExclude(String className, boolean pkgOrCls) {
        for (String name : this.excludePackageNames) {
            if (className.startsWith(name)) {
                if (log.isDebugEnabled()) {
                    if (pkgOrCls) {
                        log.debug("ioc.stdout.message034", className, name);
                    } else {
                        log.debug("ioc.stdout.message032", className, name);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 需要扫描的包名前缀集合
     *
     * @return 包名前缀集合
     */
    public Set<String> getIncludePackageNames() {
        return Collections.unmodifiableSet(this.includePackageNames);
    }

    /**
     * 需要排除的包名前缀
     *
     * @return 包名前缀集合
     */
    public Set<String> getExcludePackageNames() {
        return Collections.unmodifiableSet(this.excludePackageNames);
    }

    /**
     * 返回类过滤器集合
     *
     * @return 类过滤器集合
     */
    public List<EasyClassScan> getScans() {
        return Collections.unmodifiableList(this.scans);
    }
}
