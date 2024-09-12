package cn.org.expect.script.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 脚本文件表达式
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/13 19:13
 */
public class ScriptFileExpression {

    /** 文件路径前缀 */
    public final static String PREFIX_CLASSPATH = "classpath:";

    /** 文件路径表达式 **/
    private String expression;

    /** 文件的字符集 */
    private String charsetName;

    /** 换行符 */
    private String lineSeparator;

    /** true表示是资源路径 false表示是文件路径 */
    private boolean isUri;

    /**
     * 替换文件路径中的变量
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param pathname 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     */
    public ScriptFileExpression(UniversalScriptSession session, UniversalScriptContext context, String pathname) {
        super();
        this.expression = this.parse(session, context, pathname);
        this.isUri = StringUtils.startsWith(this.expression, PREFIX_CLASSPATH, 0, true, false);
        this.charsetName = context.getCharsetName();
        this.lineSeparator = null;
    }

    /**
     * 替换文件路径中的变量
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param pathname 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     * @return 脚本文件信息
     */
    private String parse(UniversalScriptSession session, UniversalScriptContext context, String pathname) {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String filepath0 = analysis.replaceShellVariable(session, context, pathname, true, true, true, false);
        String filepath1 = FileUtils.replaceFolderSeparator(filepath0);
        String parent = FileUtils.getParent(filepath1);
        String filepath = parent == null ? FileUtils.joinPath(session.getDirectory(), filepath1) : filepath1;
        return StringUtils.trimBlank(filepath);
    }

    /**
     * 返回脚本文件换行符
     *
     * @throws IOException 读取脚本文件发生错误
     */
    private String readLineSeparator() throws IOException {
        Reader in = this.getReader();
        try {
            StringBuilder buf = IO.read(in, new StringBuilder(1024));
            String str = FileUtils.readLineSeparator(buf);
            return str == null || str.length() == 0 ? FileUtils.lineSeparator : str;
        } finally {
            in.close();
        }
    }

    /**
     * 返回脚本文件的字符输入流
     *
     * @return 字符输入流
     * @throws IOException 访问文件错误
     */
    public Reader getReader() throws IOException {
        if (StringUtils.startsWith(this.expression, PREFIX_CLASSPATH, 0, true, false)) { // 如果文件路径以 classpath: 开头表示资源定位符
            String path = this.expression.substring(PREFIX_CLASSPATH.length()); // 截取 classpath: 右侧的路径
            InputStream in = ClassUtils.getResourceAsStream(FileUtils.replaceFolderSeparator(path, '/'), this);
            if (in == null) {
                throw new IOException(this.expression);
            } else {
                return new InputStreamReader(in, this.charsetName);
            }
        } else {
            File file = new File(this.expression);
            return IO.getBufferedReader(file, this.charsetName);
        }
    }

    /**
     * 返回脚本文件的字节输入流
     *
     * @return 字节输入流
     * @throws IOException 访问文件错误
     */
    public InputStream getInputStream() throws IOException {
        if (StringUtils.startsWith(this.expression, PREFIX_CLASSPATH, 0, true, false)) { // 如果文件路径以 classpath: 开头表示资源定位符
            String path = this.expression.substring(PREFIX_CLASSPATH.length()); // 截取 classpath: 右侧的路径
            InputStream in = ClassUtils.getResourceAsStream(FileUtils.replaceFolderSeparator(path, '/'), this);
            if (in == null) {
                throw new IOException(this.expression);
            } else {
                return in;
            }
        } else {
            return new FileInputStream(this.expression);
        }
    }

    /**
     * 返回文件名
     *
     * @return 文件名
     */
    public String getName() {
        return FileUtils.getFilename(this.expression);
    }

    /**
     * 返回文件路径
     *
     * @return 文件绝对路径
     */
    public String getAbsolutePath() {
        return this.expression;
    }

    /**
     * 返回脚本文件中的行分隔符
     *
     * @return 行分隔符
     */
    public String getLineSeparator() throws IOException {
        if (this.lineSeparator == null) {
            synchronized (this) {
                if (this.lineSeparator == null) {
                    this.lineSeparator = this.readLineSeparator();
                }
            }
        }
        return this.lineSeparator;
    }

    /**
     * 返回文件字符集
     *
     * @return 文件字符集
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * 判断路径是资源路径还是文件路径
     *
     * @return 返回true表示是资源路径 false表示是文件路径
     */
    public boolean isUri() {
        return isUri;
    }
}