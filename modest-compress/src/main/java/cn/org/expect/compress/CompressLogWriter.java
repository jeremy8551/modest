package cn.org.expect.compress;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import cn.org.expect.util.Settings;

public class CompressLogWriter extends BufferedWriter {

    public CompressLogWriter() throws UnsupportedEncodingException {
        super(new OutputStreamWriter(new OutputStream() {
            public void write(int b) {
            }
        }, Settings.getFileEncoding()));
    }

    public void write(String str) throws IOException {
        System.out.print(str);
    }

    public void newLine() {
        System.out.println();
    }
}
