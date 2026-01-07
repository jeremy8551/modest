package cn.org.expect.compress;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;

public abstract class AbstractCompress extends Terminator implements Compress {

    /** 压缩文件 */
    protected File compressFile;

    /** 日志输出流 */
    private BufferedWriter out;

    /** 显示详细信息 */
    protected boolean verbose;

    public AbstractCompress() {
        this.verbose = false;
    }

    public void setFile(File file) throws IOException {
        this.compressFile = file;
    }

    /**
     * 设置是否打印日志
     *
     * @param verbose true表示打印解压与压缩日志
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * 返回是否打印日志
     *
     * @return true表示打印解压与压缩日志
     */
    public boolean isVerbose() {
        return verbose;
    }

    public void setLogWriter(Writer out) {
        this.out = out instanceof BufferedWriter ? (BufferedWriter) out : new BufferedWriter(out);
    }

    /**
     * 判断是否能输出日志
     *
     * @return 返回true表示能输出日志，false表示不能输出日志
     */
    protected boolean canWriteLog() {
        return this.out != null;
    }

    /**
     * 输出日志
     *
     * @param str 字符串
     * @throws IOException IO流错误
     */
    protected void writeLog(String str) throws IOException {
        if (this.out != null) {
            this.out.write(str);
            this.out.newLine();
            this.out.flush();
        }
    }

    /**
     * 输出日志
     *
     * @param leftStr     左侧列的内容
     * @param leftLen     左侧列的宽度
     * @param middleStr   中间列的内容
     * @param middleLen   中间列的宽度
     * @param rightStr    右侧列的内容
     * @param rightLen    右侧列的宽度
     * @param charsetName 字符集
     * @throws IOException 字符集编码错误
     */
    protected void writeLog(String leftStr, int leftLen, String middleStr, int middleLen, String rightStr, int rightLen, String charsetName) throws IOException {
        this.writeLog(StringUtils.left(leftStr, leftLen, charsetName, ' ') + StringUtils.left(middleStr, middleLen, charsetName, ' ') + StringUtils.right(rightStr, rightLen, charsetName, ' '));
    }
}
