package cn.org.expect.maven.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * JAVA类文件的帮助类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/2
 */
public class JDKMojoUtils {

    /**
     * 读取类文件中的包名
     *
     * @param file        类的源文件
     * @param charsetName 源文件的字符集
     * @return 包名
     * @throws IOException 读取文件发生错误
     */
    public static String readPackageName(File file, String charsetName) throws IOException {
        String content = FileUtils.readline(file, charsetName, 0);
        List<String> list = new ArrayList<String>();
        StringUtils.splitLines(content, list);

        for (CharSequence line : list) {
            if (StringUtils.startsWith(line, "package", 0, true, true)) {
                return StringUtils.trimBlank(StringUtils.splitByBlank(StringUtils.trimBlank(line))[1], ';');
            }
        }
        return null;
    }

    /**
     * 读取忽略文件中的所有规则
     *
     * @param file        忽略文件
     * @param charsetName 字符集
     * @return 所有规则
     * @throws IOException 读取文件发生错误
     */
    public static Set<String> readIgnorefile(File file, String charsetName) throws IOException {
        Set<String> list = new LinkedHashSet<String>();
        BufferedReader in = IO.getBufferedReader(file, charsetName);
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    list.add(StringUtils.ltrimBlank(StringUtils.rtrimBlank(line, '/')));
                }
            }
            return list;
        } finally {
            in.close();
        }
    }

    public static Set<String> readPatterns(List<File> files, File root) {
        Set<String> set = new LinkedHashSet<String>();
        for (File file : files) {
            String filename = FileUtils.changeFilenameExt(file.getName(), "java");
            String pattern = filename.substring(0, "JDK".length()) + "*" + filename.substring(filename.lastIndexOf('.'));

            List<String> list = new ArrayList<String>();
            list.add(pattern); // JDK*.java
            File parent = file.getParentFile();
            while (parent != null && !parent.equals(root)) {
                list.add(0, parent.getName());
                parent = parent.getParentFile();
            }

            StringBuilder buf = new StringBuilder();
            for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append("/");
                }
            }
            set.add(buf.toString());
        }

        return set;
    }
}
