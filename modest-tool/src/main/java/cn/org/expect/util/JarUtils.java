package cn.org.expect.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

    /**
     * 返回类文件（*.class）所在 jar 文件的绝对路径, 如果是多层jar包嵌套，则返回第一层jar所在路径
     *
     * @param type 类信息
     * @return 如果类信息参数不在 jar 包中时返回 null
     */
    public static String getPath(Class<?> type) {
        if (type == null) {
            return null;
        }

        ProtectionDomain domain = type.getProtectionDomain();
        if (domain == null) {
            throw new RuntimeException(type.getName());
        }

        CodeSource codeSource = domain.getCodeSource();
        if (codeSource == null) {
            throw new RuntimeException(type.getName());
        }

        URL url = codeSource.getLocation();
        if (url == null) {
            throw new RuntimeException(type.getName());
        }

        // 解压文件路径中的非ascii字符
        String filepath = StringUtils.decodeJvmUtf8HexString(url.getFile());
        if (filepath == null) {
            return null;
        }

        int end = filepath.length() - 1;
        String dest = ".jar";
        int index;
        while ((index = StringUtils.lastIndexOfStr(filepath, dest, 0, end, true)) != -1) {
            int position = index + dest.length();
            if (position == end && StringUtils.isLetter(filepath.charAt(position))) {
                end = index - 1;
            } else {
                return filepath.substring(0, position);
            }
        }
        return null;
    }

    /**
     * 返回 jar 文件对应的源代码文件
     *
     * @param jarFilepath jar 文件绝对路径
     * @return 源代码文件 *-sources.jar
     */
    public static File toSourceJar(String jarFilepath) {
        if (StringUtils.isBlank(jarFilepath)) {
            return null;
        }

        String filepath = StringUtils.replaceLast(jarFilepath, ".jar", "-sources.jar");
        return new File(filepath);
    }

    /**
     * 从 jar 文件中读取文件内容
     *
     * @param jarFile     jar 文件
     * @param target      资源定位符: cn/org/expect/util/Messages.properties
     * @param charsetName 资源文件的字符集编码
     * @return 文件内容
     * @throws IOException 读 jar 文件发生错误
     */
    public static String read(File jarFile, String target, String charsetName) throws IOException {
        if (FileUtils.isFile(jarFile)) {
            JarFile jar = new JarFile(jarFile);
            JarEntry entry = jar.getJarEntry(target);
            if (entry != null) {
                InputStream in = jar.getInputStream(entry);
                if (in != null) {
                    return new String(IO.read(in), charsetName);
                }
            }
        }
        return null;
    }
}
