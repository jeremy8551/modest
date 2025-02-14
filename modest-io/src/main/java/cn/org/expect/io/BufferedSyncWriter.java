package cn.org.expect.io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * 支持多线程
 */
public class BufferedSyncWriter extends BufferedWriter {

    public BufferedSyncWriter(File file, String charsetName, int cache) throws IOException {
        super(file, charsetName, cache);
    }

    public BufferedSyncWriter(File file, String charsetName, boolean append, int cache) throws IOException {
        super(file, charsetName, append, cache);
    }

    public BufferedSyncWriter(Writer out, int cache) throws IOException {
        super(out, cache);
    }

    public synchronized void write(String str) {
        super.write(str);
    }

    public synchronized boolean writeLine(String line) throws IOException {
        return super.writeLine(line);
    }

    public synchronized boolean writeLine(String line, String lineSeperator) throws IOException {
        return super.writeLine(line, lineSeperator);
    }

    public synchronized void flush() throws IOException {
        super.flush();
    }

    public synchronized void close() throws IOException {
        super.close();
    }
}
