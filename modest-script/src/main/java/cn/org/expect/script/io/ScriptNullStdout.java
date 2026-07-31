package cn.org.expect.script.io;

import java.io.Writer;
import java.text.Format;

import cn.org.expect.script.UniversalScriptStdout;

/**
 * 提供操作系统命令标准输出的适配实现
 */
public class ScriptNullStdout implements UniversalScriptStdout {

    private UniversalScriptStdout proxy;

    /**
     * 初始化 ScriptNullStdout
     */
    public ScriptNullStdout(UniversalScriptStdout proxy) {
        this.proxy = proxy;
    }

    public Writer getWriter() {
        return this.proxy == null ? null : this.proxy.getWriter();
    }

    public void setWriter(Writer writer) {
        if (this.proxy != null) {
            this.proxy.setWriter(writer);
        }
    }

    public void setFormatter(Format f) {
        if (this.proxy != null) {
            this.proxy.setFormatter(f);
        }
    }

    public Format getFormatter() {
        return this.proxy == null ? null : this.proxy.getFormatter();
    }

    /** {@inheritDoc} */
    public void println(String id, CharSequence message) {
    }

    /** {@inheritDoc} */
    public void print(CharSequence msg) {
    }

    /** {@inheritDoc} */
    public void print(Object obj) {
    }

    /** {@inheritDoc} */
    public void println() {
    }

    /** {@inheritDoc} */
    public void println(CharSequence msg) {
    }

    /** {@inheritDoc} */
    public void println(CharSequence msg, Throwable e) {
    }

    /** {@inheritDoc} */
    public void println(Object obj) {
    }

    /** {@inheritDoc} */
    public void close() {
        if (this.proxy != null) {
            this.proxy.close();
        }
    }
}
