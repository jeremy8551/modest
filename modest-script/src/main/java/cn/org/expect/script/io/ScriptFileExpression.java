package cn.org.expect.script.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 脚本文件表达式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/13 19:13
 */
public class ScriptFileExpression {

    public static ScriptFileExpression parse(UniversalScriptSession session, UniversalScriptContext context, String expression) {
        UniversalScriptAnalysis analysis = session.getAnalysis();
        List<String> list = analysis.split(expression, new ArrayList<String>());
        for (int i = 1; i < list.size(); i++) { // 从第二个元素开始
            list.set(i, analysis.unQuotation(analysis.replaceShellVariable(session, context, list.get(i), true, true)));
        }

        String filepath = analysis.unQuotation(list.get(0)); // 文件路径
        ScriptFileExpression expr = new ScriptFileExpression(session, context, filepath);
        expr.parameters = list.toArray(new String[list.size()]);
        return expr;
    }

    /** 文件路径表达式 **/
    protected final String filepath;

    /** 文件的字符集 */
    protected final String charsetName;

    /** 换行符 */
    protected volatile String lineSeparator;

    /** true表示是资源路径 false表示是文件路径 */
    protected final boolean isURI;

    /** 参数数组 */
    protected String[] parameters;

    /**
     * 替换文件路径中的变量
     *
     * @param session  用户会话信息
     * @param context  脚本引擎上下文信息
     * @param filepath 路径信息（如果是文件名，则默认使用当前目录作为父目录）
     */
    public ScriptFileExpression(UniversalScriptSession session, UniversalScriptContext context, String filepath) {
        super();
        this.filepath = ScriptFile.replaceFilepath(session, context, filepath);
        this.isURI = StringUtils.startsWith(this.filepath, ClassUtils.PREFIX_CLASSPATH, 0, true, false);
        this.charsetName = context.getCharsetName();
        this.lineSeparator = null;
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
            return str == null || str.length() == 0 ? Settings.LINE_SEPARATOR : str;
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
        return new InputStreamReader(this.getInputStream(), this.charsetName);
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
    public boolean isResource() {
        return isURI;
    }

    /**
     * 参数数组
     *
     * @return 参数数组（两端无引号）
     */
    public String[] getParameters() {
        return parameters;
    }
}
