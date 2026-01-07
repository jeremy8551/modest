package cn.org.expect.script.method;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.org.expect.os.linux.LinuxLocalOS;
import cn.org.expect.os.linux.Linuxs;
import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;

@EasyVariableExtension
public class FileExtension {

    /**
     * 读取文件内容
     *
     * @param filepath    文件绝对路径
     * @param charsetName 文件的字符集编码
     * @return 文件内容
     * @throws IOException 发生错误
     */
    public static String read(CharSequence filepath, String charsetName) throws IOException {
        String file = filepath.toString();
        InputStream in = ClassUtils.getResourceAsStream(file);
        assert in != null;
        return new String(IO.read(in), charsetName);
    }

    /**
     * 删除文件
     *
     * @param filepath 文件绝对路径
     * @return 返回true表示成功，false表示失败
     */
    public static boolean deleteFile(CharSequence filepath) {
        return FileUtils.delete(new File(FileUtils.replaceFolderSeparator(filepath.toString())));
    }

    /**
     * 判断文件是否存在
     *
     * @param filepath 文件绝对路径
     * @return 返回true表示存在，false表示不存在
     */
    public static boolean existsFile(CharSequence filepath) {
        return new File(FileUtils.replaceFolderSeparator(filepath.toString())).exists();
    }

    /**
     * 返回文件扩展名
     *
     * @param filepath 文件绝对路径
     * @return 文件扩展名
     */
    public static String getFileExt(CharSequence filepath) {
        return FileUtils.getFilenameExt(filepath.toString());
    }

    /**
     * 返回文件的换行符
     *
     * @param filepath 文件绝对路径
     * @return 换行符
     * @throws IOException 发生错误
     */
    public static String getFileLineSeparator(CharSequence filepath) throws IOException {
        return FileUtils.readLineSeparator(new File(FileUtils.replaceFolderSeparator(filepath.toString())));
    }

    /**
     * 返回文件名
     *
     * @param filepath 文件绝对路径
     * @return 文件名
     * @throws IOException 发生错误
     */
    public static String getFilename(CharSequence filepath) throws IOException {
        return FileUtils.getFilename(filepath.toString());
    }

    /**
     * 返回不含扩展名的文件名
     *
     * @param filepath 文件绝对路径
     * @return 文件名
     * @throws IOException 发生错误
     */
    public static String getFilenameNoExt(CharSequence filepath) throws IOException {
        return FileUtils.getFilenameNoExt(filepath.toString());
    }

    /**
     * 返回不含后缀的文件名
     *
     * @param filepath 文件绝对路径
     * @return 文件名
     * @throws IOException 发生错误
     */
    public static String getFilenameNoSuffix(CharSequence filepath) throws IOException {
        return FileUtils.getFilenameNoSuffix(filepath.toString());
    }

    /**
     * 返回文件名后缀
     *
     * @param filepath 文件绝对路径
     * @return 文件名后缀
     * @throws IOException 发生错误
     */
    public static String getFileSuffix(CharSequence filepath) throws IOException {
        return FileUtils.getFilenameSuffix(filepath.toString());
    }

    /**
     * 返回文件的目录
     *
     * @param filepath 文件绝对路径
     * @return 文件的上级目录
     * @throws IOException 发生错误
     */
    public static String getParent(CharSequence filepath) throws IOException {
        return FileUtils.getParent(filepath.toString());
    }

    /**
     * 判读文件路径是否是目录
     *
     * @param filepath 文件绝对路径
     * @return 返回true表示是目录，false表示不是目录
     * @throws IOException 发生错误
     */
    public static boolean isDirectory(CharSequence filepath) throws IOException {
        File file = new File(FileUtils.replaceFolderSeparator(filepath.toString()));
        return file.exists() && file.isDirectory();
    }

    /**
     * 判断文件路径是否是一个文件
     *
     * @param filepath 文件绝对路径
     * @return 返回true表示是文件，false表示不是文件
     * @throws IOException 发生错误
     */
    public static boolean isFile(CharSequence filepath) throws IOException {
        File file = new File(FileUtils.replaceFolderSeparator(filepath.toString()));
        return file.exists() && file.isFile();
    }

    /**
     * 创建目录
     *
     * @param filepath 文件绝对路径
     * @return 返回true表示成功，false表示失败
     * @throws IOException 发生错误
     */
    public static boolean mkdir(CharSequence filepath) throws IOException {
        return FileUtils.createDirectory(new File(FileUtils.replaceFolderSeparator(filepath.toString())));
    }

    /**
     * 创建文件
     *
     * @param filepath 文件绝对路径
     * @return 返回true表示成功，false表示失败
     * @throws IOException 发生错误
     */
    public static boolean touch(CharSequence filepath) throws IOException {
        return FileUtils.createFile(new File(FileUtils.replaceFolderSeparator(filepath.toString())));
    }

    /**
     * 把文件路径参数 filepath 中的 '/' 和 '\' 字符替换成当前操作系统的路径分隔符
     *
     * @param filepath 文件路径
     * @return 文件路径
     */
    public static String replaceFolderSeparator(String filepath) {
        return FileUtils.replaceFolderSeparator(filepath);
    }

    /**
     * 在路径后面拼接一个文件或目录
     *
     * @param filepath 文件路径
     * @param array    文件绝对路径数组
     * @return 文件路径
     */
    public static String joinPath(String filepath, String... array) {
        return FileUtils.joinPath(filepath, FileUtils.joinPath(array));
    }

    /**
     * 加载属性集合
     *
     * @param in 属性集合输入流
     * @return 属性集合
     */
    public static Properties loadProperties(InputStream in) {
        return FileUtils.loadProperties(in);
    }

    /**
     * 加载 Properties 文件中的属性
     *
     * @param classLoader     类加载器
     * @param name            资源文件位置
     * @param envPropertyName 分环境资源文件
     * @return 属性集合
     */
    public static Properties loadProperties(ClassLoader classLoader, String name, String envPropertyName) {
        return FileUtils.loadProperties(classLoader, name, envPropertyName);
    }

    /**
     * 显示目录中的文件
     *
     * @param filepath 文件绝对路径
     * @return 目录中文件
     * @throws IOException 发生错误
     */
    public static String ls(CharSequence filepath) throws IOException {
        File file = new File(FileUtils.replaceFolderSeparator(filepath.toString()));
        CharTable table = new CharTable();
        table.addTitle("filename");
        if (file.isDirectory()) {
            File[] files = FileUtils.array(file.listFiles());
            for (File f : files) {
                if (LinuxLocalOS.KEY_FILENAMES.contains(file.getName())) {
                    continue;
                } else {
                    table.addCell(Linuxs.toLongname(f));
                }
            }
        } else {
            table.addCell(Linuxs.toLongname(file));
        }

        return table.toString(CharTable.Style.SIMPLE);
    }
}
