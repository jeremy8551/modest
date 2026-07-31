package cn.org.expect.io;

import java.io.Writer;

/**
 * 将处理结果写入目标输出
 */
public class NullWriter extends Writer {

    /**
     * 初始化 NullWriter
     */
    public NullWriter() {
        super();
    }

    /**
     * 初始化 NullWriter
     */
    public NullWriter(Object lock) {
        super(lock);
    }

    /** {@inheritDoc} */
    public void write(char[] cbuf, int off, int len) {
    }

    /** {@inheritDoc} */
    public void flush() {
    }

    /** {@inheritDoc} */
    public void close() {
    }

    /** {@inheritDoc} */
    public void write(int c) {
    }

    /** {@inheritDoc} */
    public void write(char[] cbuf) {
    }

    /** {@inheritDoc} */
    public void write(String str) {
    }

    /** {@inheritDoc} */
    public void write(String str, int off, int len) {
    }

    /** {@inheritDoc} */
    public Writer append(CharSequence csq) {
        return this;
    }

    /** {@inheritDoc} */
    public Writer append(CharSequence csq, int start, int end) {
        return this;
    }

    /** {@inheritDoc} */
    public Writer append(char c) {
        return this;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "";
    }
}
