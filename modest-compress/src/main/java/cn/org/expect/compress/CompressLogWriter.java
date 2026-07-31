package cn.org.expect.compress;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import cn.org.expect.util.Settings;

/**
 * 将处理结果写入目标输出
 */
public class CompressLogWriter extends BufferedWriter {

    /**
     * 初始化 CompressLogWriter
     */
    public CompressLogWriter() throws UnsupportedEncodingException {
        super(new OutputStreamWriter(new OutputStream() {
            /** {@inheritDoc} */
            public void write(int b) {
            }
        }, Settings.getFileEncoding()));
    }

    /** {@inheritDoc} */
    public void write(String str) throws IOException {
        System.out.print(str);
    }

    /** {@inheritDoc} */
    public void newLine() {
        System.out.println();
    }
}
