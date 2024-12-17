package cn.org.expect.database.export.inernal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.database.export.ExtractMessage;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.io.TableLine;
import cn.org.expect.io.TableWriter;
import cn.org.expect.io.TextTable;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.os.OSFtpCommand;
import cn.org.expect.util.Ensure;

@EasyBean(value = "sftp", description = "卸载数据到远程sftp服务器")
public class SftpFileWriter implements ExtractWriter, EasyContextAware {

    protected String target;

    protected OSFtpCommand ftp;

    protected String remotepath;

    /** 文件输出流 */
    protected TableWriter writer;

    protected long lineNumber;

    protected ExtractMessage message;

    protected EasyContext context;

    public void setContext(EasyContext context) {
        this.context = context;
    }

    /**
     * 初始化
     *
     * @param context    卸数引擎上下文信息
     * @param message    消息信息
     * @param host       远程服务器地址
     * @param port       远程服务器端口
     * @param username   远程服务器用户名
     * @param password   远程服务器密码
     * @param remotepath 远程服务器目录
     * @throws IOException 卸载数据发生错误
     */
    public SftpFileWriter(ExtracterContext context, ExtractMessage message, String host, String port, String username, String password, String remotepath) throws IOException {
        this.message = message;
        this.remotepath = remotepath;

        this.open(host, port, username, password, remotepath);

        TextTable template = context.getFormat();
        InputStreamImpl in = new InputStreamImpl();
        WriterImpl out = new WriterImpl(new Object(), template.getCharsetName());

        in.setOut(out);
        out.setIn(in);

        this.writer = template.getWriter(out, context.getCacheLines());
        this.ftp.upload(in, this.remotepath);
    }

    protected void open(String host, String port, String username, String password, String remotepath) {
        this.ftp = this.context.getBean(OSFtpCommand.class, "sftp");
        Ensure.isTrue(this.ftp.connect(host, Integer.parseInt(port), username, password), host, port, username, password);
        this.target = "sftp://" + username + "@" + host + ":" + port + "?password=" + password;
    }

    public void write(TableLine line) throws IOException {
        this.lineNumber++;
        this.writer.addLine(line);
    }

    public boolean rewrite() throws IOException {
        return false;
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
        if (this.ftp != null) {
            this.ftp.close();
            this.ftp = null;
        }

        this.message.setRows(this.lineNumber);
        this.message.setBytes(0);
        this.message.setTarget(this.target);
    }

    private static class InputStreamImpl extends InputStream {

        private byte[] bytes;

        private int index;

        private WriterImpl out;

        protected final Lock lock = new ReentrantLock();

        public InputStreamImpl() {
            super();
            this.index = 0;
        }

        public void setOut(WriterImpl out) {
            this.out = out;
        }

        public synchronized int read(byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            try {
                this.lock.lockInterruptibly();

                while (!this.lock.tryLock()) {
                }

                int read = 0, left = 0;
                do {
                    left = this.bytes.length - this.index;
                    read = Math.min(left, len);
                    System.arraycopy(this.bytes, this.index, b, off, read);
                    this.index += read;
                } while (read <= 0 && !this.out.isClose());
                return this.out.isClose() ? -1 : read;
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            } finally {
                this.lock.unlock();
            }
        }

        public synchronized int read() {
            return this.bytes[this.index++];
        }

        public synchronized void setBytes(byte[] bytes) {
            this.bytes = bytes;
            this.index = 0;
        }

        public boolean empty() {
            return this.bytes == null || this.index >= this.bytes.length;
        }

    }

    private static class WriterImpl extends Writer {

        private InputStreamImpl in;

        private String charsetName;

        private volatile boolean close;

        public WriterImpl(Object lock, String charsetName) {
            super(lock);
            this.close = false;
            this.charsetName = charsetName;
        }

        public void setIn(InputStreamImpl in) {
            this.in = in;
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            synchronized (this.lock) {
                String str = new String(cbuf, off, len);
                byte[] bytes = str.getBytes(this.charsetName);
                while (true) {
                    if (this.in.empty()) {
                        this.in.setBytes(bytes);
                        break;
                    }
                }
            }
        }

        public void flush() {
        }

        public void close() {
            this.close = true;
        }

        public boolean isClose() {
            return close;
        }

    }

}
