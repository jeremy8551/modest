package cn.org.expect.compress;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.org.expect.crypto.MD5Encrypt;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;

/**
 * 工具类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2009-12-19
 */
public class CompressUtils {

    public CompressUtils() {
    }

    /**
     * 创建测试目录、子目录与子文件
     *
     * @return 测试目录
     * @throws IOException 访问文件发生错误
     */
    public static File createTestDir() throws IOException {
        File dir = FileUtils.createTempDirectory(null); // 文件所在目录
        FileUtils.createDirectory(dir);

        File f0 = new File(dir, "t1.txt");
        FileUtils.assertCreateFile(f0);
        FileUtils.write(f0, CharsetName.UTF_8, false, "f0");

        File f1 = new File(dir, "t2.txt");
        FileUtils.assertCreateFile(f1);

        createChildDir(dir, 1);
        createChildDir(dir, 2);
        createChildDir(dir, 3);
        createChildDir(dir, 4);
        createChildDir(dir, 5);

        return dir;
    }

    /**
     * 创建子目录与子文件
     *
     * @param dir 子目录
     * @param n   子目录序号
     * @throws IOException 访问文件发生错误
     */
    private static void createChildDir(File dir, int n) throws IOException {
        File childDir = new File(dir, "child" + n);
        FileUtils.assertCreateDirectory(childDir);

        File f1 = new File(childDir, "t1.txt");
        FileUtils.assertCreateFile(f1);
        FileUtils.write(f1, CharsetName.UTF_8, false, "f21中文zh，英文en");

        File f2 = new File(childDir, "t1.txt");
        FileUtils.assertCreateFile(f2);
        FileUtils.write(f2, CharsetName.UTF_8, false, "f21中文zh，英文en");

        File f3 = new File(childDir, "child10");
        FileUtils.assertCreateDirectory(f3);

        File f4 = new File(f3, "f231.txt");
        FileUtils.assertCreateFile(f4);
        FileUtils.write(f4, CharsetName.UTF_8, false, "f231\nasdfas\r\n\r\n");

        File f5 = new File(f3, "f232.txt");
        FileUtils.assertCreateFile(f5);
        FileUtils.write(f5, CharsetName.UTF_8, false, "f232中文zh，英文en");

        File f6 = new File(f3, "child");
        FileUtils.assertCreateDirectory(f6);

        File f7 = new File(f6, "f231.txt");
        FileUtils.assertCreateFile(f7);
        FileUtils.write(f7, CharsetName.UTF_8, false, "f7asdfasdf\n\nsadfasdf中文zh，英文en\n");

        File f8 = new File(f6, "f232.txt");
        FileUtils.assertCreateFile(f8);
        FileUtils.write(f8, CharsetName.UTF_8, false, "f8\nasdfas中文zh，英文en\n");
    }

    /**
     * 判断参数目录、子目录与子文件是否相等
     *
     * @param dir1 目录1
     * @param dir2 目录2
     * @return 返回true表示相等，false表示不相等
     * @throws IOException 访问文件发生错误
     */
    public static boolean equals(File dir1, File dir2) throws IOException {
        Map<String, File> map1 = scanDir(dir1);
        Map<String, File> map2 = scanDir(dir2);

        if (map1.size() != map2.size()) {
            System.err.println(dir1.getAbsolutePath() + ", file size: " + map1.size());
            System.err.println(dir2.getAbsolutePath() + ", file size: " + map2.size());
            return false;
        }

        Set<String> keys1 = map1.keySet();
        for (String key : keys1) {
            File val1 = map1.get(key);
            File val2 = map2.get(key);

            if (val1 == null) {
                return false;
            }

            if (val2 == null) {
                return false;
            }

            if (val1.isDirectory()) {
                if (val2.isDirectory()) {
                    continue;
                } else {
                    return false;
                }
            }

            if (val1.isFile()) {
                if (val2.isFile()) {
                    String fileMd51 = MD5Encrypt.encrypt(val1, null);
                    String fileMd52 = MD5Encrypt.encrypt(val2, null);

                    if (fileMd51.equals(fileMd52)) {
                        continue;
                    } else {
                        System.err.println(val1.getAbsoluteFile() + ", md5: " + fileMd51);
                        System.err.println(val2.getAbsoluteFile() + ", md5: " + fileMd52);
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 扫描目录、子目录、子文件
     *
     * @param dir 目录
     * @return 文件路径与文件的映射关系
     */
    private static Map<String, File> scanDir(File dir) {
        Map<String, File> map = new HashMap<String, File>();
        scanDir0(dir.getAbsolutePath(), dir, map);
        return map;
    }

    /**
     * 扫描目录、子目录、子文件
     *
     * @param prefix 文件路径前缀
     * @param dir    目录
     * @param map    文件路径与文件的映射关系
     */
    private static void scanDir0(String prefix, File dir, Map<String, File> map) {
        map.put(StringUtils.removePrefix(dir.getAbsolutePath(), prefix), dir);

        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file.isDirectory()) {
                scanDir0(prefix, file, map);
            } else {
                map.put(StringUtils.removePrefix(file.getAbsolutePath(), prefix), file);
            }
        }
    }

    /**
     * 创建测试目录、子目录与子文件，使用压缩接口压缩文件，解压缩
     *
     * @param compress 压缩接口
     * @throws IOException 访问文件发生错误
     */
    public static void test(Compress compress) throws IOException {
        File dir = CompressUtils.createTestDir();
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            compress.archiveFile(file, null, CharsetName.UTF_8);
        }
        compress.close();

        // 解压文件
        File unCompressDir = FileUtils.createTempDirectory(null);
        FileUtils.assertCreateDirectory(unCompressDir);
        compress.extract(unCompressDir, CharsetName.UTF_8);
        compress.close();

        // 判断解压前后是否一致
        Assert.assertTrue(CompressUtils.equals(dir, unCompressDir));
    }
}
