package cn.org.expect.script.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.expression.GPatternExpression;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 文件路径表达式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/13 19:13
 */
public class PathExpression {

    /**
     * 将文件路径转为文件
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 文件路径
     * @return 文件
     */
    public static File toFile(UniversalScriptSession session, UniversalScriptContext context, String filepath) {
        return new File(resolveAbsolutePath(session, context, filepath));
    }

    /**
     * 将文件路径转为绝对路径
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     * @return 文件路径
     */
    protected static String resolveAbsolutePath(UniversalScriptSession session, UniversalScriptContext context, String filepath) {
        if (filepath == null) {
            throw new NullPointerException();
        }

        String path = resolve(session, context, filepath, true);
        String parent = FileUtils.getParent(path);
        return StringUtils.trimBlank(parent == null ? FileUtils.joinPath(session.getDirectory().getAbsolutePath(), path) : path);
    }

    /**
     * 解析文件路径
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 文件路径
     * @param local    true表示替换成当前操作系统的路径分隔符 false表示替换成 '/'
     * @return 文件路径
     */
    public static String resolve(UniversalScriptSession session, UniversalScriptContext context, String filepath, boolean local) {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        String str = analysis.replaceShellVariable(session, context, analysis.unQuotation(filepath), true, !analysis.containsQuotation(filepath));
        return FileUtils.replaceFolderSeparator(str, local);
    }

    /** 文件路径表达式 **/
    protected final String filepath;

    /** 文件路径是否以 {@linkplain ClassUtils#PREFIX_CLASSPATH} 为前缀 **/
    protected final boolean startWithClasspath;

    /**
     * 替换文件路径中的变量
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     */
    public PathExpression(UniversalScriptSession session, UniversalScriptContext context, String filepath) {
        this.filepath = resolveAbsolutePath(session, context, filepath);
        this.startWithClasspath = StringUtils.startsWith(this.filepath, ClassUtils.PREFIX_CLASSPATH, 0, true, true);
    }

    /**
     * 返回文件的字符输入流
     *
     * @param charsetName 文件字符集
     * @return 字符输入流
     * @throws IOException 访问文件错误
     */
    public Reader getReader(String charsetName) throws IOException {
        return new InputStreamReader(this.getInputStream(), charsetName);
    }

    /**
     * 返回脚本文件的字节输入流
     *
     * @return 字节输入流
     * @throws IOException 访问文件错误
     */
    public InputStream getInputStream() throws IOException {
        InputStream in = ClassUtils.getResourceAsStream(this.filepath);
        if (in == null) {
            throw new IOException(this.filepath);
        } else {
            return in;
        }
    }

    /**
     * 返回文件名
     *
     * @return 文件名
     */
    public String getName() {
        return FileUtils.getFilename(this.filepath);
    }

    /**
     * 返回文件路径
     *
     * @return 文件绝对路径
     */
    public String getAbsolutePath() {
        return this.filepath;
    }

    /**
     * 判断路径是资源路径还是文件路径
     *
     * @return 返回true表示是资源路径 false表示是文件路径
     */
    public boolean startWithClasspath() {
        return startWithClasspath;
    }

    /**
     * 获取匹配的文件列表
     *
     * @return 匹配的文件列表
     */
    public List<File> listFiles() throws Exception {
        final List<File> list = new ArrayList<File>();
        FileUtils.handlePathExpression(this.filepath, new FileUtils.Process() {
            public boolean match(String filename, String expression) {
                return GPatternExpression.match(filename, expression);
            }

            public boolean execute(File file) {
                list.add(file);
                return true;
            }
        });
        return list;
    }

    /**
     * 删除匹配的文件
     *
     * @return true表示全部处理成功 false表示发生错误
     */
    public boolean deleteFiles() throws Exception {
        return FileUtils.handlePathExpression(this.filepath, new FileUtils.Process() {

            public boolean match(String filename, String expression) {
                return GPatternExpression.match(filename, expression);
            }

            public boolean execute(File file) {
                return FileUtils.delete(file);
            }
        });
    }
}
