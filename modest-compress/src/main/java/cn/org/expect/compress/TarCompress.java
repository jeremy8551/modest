package cn.org.expect.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.StringUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

@EasyBean(value = "tar")
public class TarCompress extends AbstractCompress {
    private final static Log log = LogFactory.getLog(TarCompress.class);

    /** 写 tar 文件 */
    protected TarArchiveOutputStream outputStream;

    /** 读 tar 文件 */
    protected TarArchiveInputStream inputStream;

    /** 缓冲区 */
    protected byte[] buffer;

    /** true表示tar文件使用gzip格式压缩 */
    protected boolean isGzipCompress;

    /** 不解压文件只打印 */
    protected boolean notExtract;

    /**
     * 构造函数
     */
    public TarCompress() {
        super();
        this.buffer = new byte[IO.getByteArrayLength()];
        this.isGzipCompress = false;
        this.notExtract = false;
        this.verbose = false;
    }

    /**
     * 构造函数
     *
     * @param file tar文件
     * @param size 缓冲区大小
     * @param b    true表示tar文件使用gzip格式压缩
     */
    public TarCompress(File file, int size, boolean b) {
        this();
        this.buffer = new byte[size];
        this.compressFile = file;
        this.isGzipCompress = b;
    }

    /**
     * 是否使用使用gzip格式压缩
     *
     * @return 返回true表示使用gzip格式压缩
     */
    public boolean isGzipCompress() {
        return isGzipCompress;
    }

    /**
     * 使用使用gzip格式压缩
     *
     * @param b true表示使用gzip格式压缩
     */
    public void setGzipCompress(boolean b) {
        this.isGzipCompress = b;
    }

    public void setNotExtract(boolean notExtract) {
        this.notExtract = notExtract;
    }

    public void archiveFile(File file, String dir) throws IOException {
        this.addFile(file, dir, CharsetUtils.get(), 0);
    }

    public void archiveFile(File file, String dir, String charsetName) throws IOException {
        this.addFile(file, dir, charsetName, 0);
    }

    protected void addFile(File file, String dir, String charsetName, int level) throws IOException {
        Ensure.notNull(this.compressFile);
        FileUtils.assertExists(file);
        this.initOutputStream(CharsetUtils.get(charsetName));

        // 处理目录
        dir = (dir == null) ? "" : StringUtils.trimBlank(dir);
        if (dir.equals("/")) {
            dir = "";
        }

        // 长度
        int length = dir.length();

        // 去掉最前面的斜线
        if (length > 1 && dir.charAt(0) == '/') {
            dir = dir.substring(1);
        }

        // 去掉最后面的斜线
        if (length > 1 && dir.charAt(length - 1) != '/') {
            dir = dir + "/";
        }

        if (this.verbose && this.canWriteLog()) {
            this.writeLog("a " + file.getAbsolutePath());
        }

        if (file.isDirectory()) {
            StringBuilder buf = new StringBuilder();
            File root = new File(file.getAbsolutePath());
            for (int i = 0; i < level; i++) {
                root = new File(root.getParent());
                buf.insert(0, root.getName() + "/");
            }
            buf = new StringBuilder(buf + file.getName() + "/");

            if (log.isDebugEnabled()) {
                log.debug("tar file, create dir: " + buf + " ..");
            }

            TarArchiveEntry entry = new TarArchiveEntry(buf.toString());
            entry.setSize(0);
            this.outputStream.putArchiveEntry(entry);
            this.outputStream.closeArchiveEntry();

            // 遍历目录下的所有文件并压入压缩包中的目录下
            File[] array = FileUtils.array(file.listFiles());
            for (int i = 0; i < array.length; i++) {
                if (this.terminate) {
                    break;
                } else {
                    this.addFile(array[i], buf.toString(), charsetName, level + 1);
                }
            }
        } else {
            if (dir.length() > 1 && !dir.equals("//")) { // 创建父目录
                String d = dir.charAt(0) == '/' ? dir.substring(1) : dir;
                if (d.length() > 1) {
                    TarArchiveEntry entry = new TarArchiveEntry(d);
                    entry.setSize(0);
                    this.outputStream.putArchiveEntry(entry);
                    this.outputStream.closeArchiveEntry();
                }
            }

            String tarFile = dir + file.getName();
            InputStream in = new FileInputStream(file);
            try {
                TarArchiveEntry entry = new TarArchiveEntry(tarFile);
                entry.setSize(file.length());
                this.outputStream.putArchiveEntry(entry);
                byte[] buffer = new byte[1024];
                for (int size; (size = in.read(buffer)) != -1; ) {
                    if (this.terminate) {
                        break;
                    } else {
                        this.outputStream.write(buffer, 0, size);
                    }
                }
                this.outputStream.closeArchiveEntry();
            } finally {
                in.close();
            }
        }
    }

    public void extract(File outputDir, String charsetName) throws IOException {
        if (!this.notExtract) {
            FileUtils.assertCreateDirectory(outputDir);
        }

        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                } else {
                    this.untar(this.inputStream, outputDir, entry);
                }
            }
        } finally {
            this.closeInputStream();
        }
    }

    public void extract(File outputDir, String charsetName, String entryName) throws IOException {
        if (!this.notExtract) {
            FileUtils.assertCreateDirectory(outputDir);
        }

        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                } else if (entry.getName().equals(entryName)) {
                    this.untar(this.inputStream, outputDir, entry);
                }
            }
        } finally {
            this.closeInputStream();
        }
    }

    /**
     * 解压 TarEntry 对象
     *
     * @param in        输入流
     * @param outputDir 解压后目录
     * @param entry     TarEntry实例
     * @throws IOException 访问文件错误
     */
    public void untar(TarArchiveInputStream in, File outputDir, TarArchiveEntry entry) throws IOException {
        Ensure.notNull(entry);

        // 输出日志
        if (this.verbose && this.canWriteLog()) {
            this.writeLog("x " + FileUtils.replaceFolderSeparator(entry.getName(), '/'));
        }

        if (!this.notExtract) {
            // 文件或目录
            File file = new File(outputDir, entry.getName());

            // 如果是目录
            if (entry.isDirectory()) {
                FileUtils.assertCreateDirectory(file);
            } else {
                this.untarFile(in, file);
            }
        }
    }

    /**
     * 解压tar文件到指定目录
     *
     * @param inputStream IO数据流
     * @param file        文件
     * @throws IOException 访问文件错误
     */
    public void untarFile(TarArchiveInputStream inputStream, File file) throws IOException {
        FileUtils.createFile(file);
        FileOutputStream out = new FileOutputStream(file, false);
        try {
            for (int len = inputStream.read(this.buffer, 0, this.buffer.length); len != -1; len = inputStream.read(this.buffer, 0, this.buffer.length)) {
                if (this.terminate) {
                    break;
                } else {
                    out.write(this.buffer, 0, len);
                }
            }
            out.flush();
        } finally {
            out.close();
        }
    }

    public boolean removeEntry(String charsetName, String... entryNames) throws IOException {
        charsetName = CharsetUtils.get(charsetName);
        String dirName = StringUtils.replaceAll(FileUtils.getFilenameNoExt(this.compressFile.getName()) + Numbers.getRandom() + Numbers.getRandom() + Numbers.getRandom() + Numbers.getRandom() + "tmp", ".", "");
        File tmpDir = new File(FileUtils.joinPath(this.compressFile.getParent(), dirName));
        FileUtils.assertCreateDirectory(tmpDir);
        try {
            this.extract(tmpDir, charsetName, entryNames);
            this.close();

            // 重新压缩
            File newTarfile = new File(tmpDir, this.compressFile.getName());
            TarCompress c = new TarCompress(newTarfile, this.buffer.length, this.isGzipCompress);
            try {
                File[] array = FileUtils.array(tmpDir.listFiles());
                for (File file : array) {
                    if (this.terminate) {
                        break;
                    } else {
                        c.archiveFile(file, null, charsetName);
                    }
                }
            } finally {
                c.close();
            }

            return FileUtils.delete(this.compressFile) && FileUtils.rename(newTarfile, this.compressFile, null);
        } finally {
            FileUtils.delete(tmpDir);
        }
    }

    public void extract(File outputDir, String charsetName, String... excludeNames) throws IOException {
        if (!this.notExtract) {
            FileUtils.assertCreateDirectory(outputDir);
        }

        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                }

                if (!StringUtils.inArray(entry.getName(), excludeNames)) {
                    this.untar(this.inputStream, outputDir, entry);
                }
            }
        } finally {
            this.closeInputStream();
        }
    }

    protected void initOutputStream(String charsetName) throws IOException {
        if (this.outputStream == null) {
            if (this.isGzipCompress()) {
                this.outputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(this.compressFile, true)), charsetName);
            } else {
                this.outputStream = new TarArchiveOutputStream(new FileOutputStream(this.compressFile, true), charsetName);
            }
        }
    }

    protected void initInputStream(String charsetName) throws IOException {
        if (this.inputStream == null) {
            if (this.isGzipCompress(this.compressFile)) {
                this.inputStream = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(this.compressFile)), charsetName);
            } else {
                this.inputStream = new TarArchiveInputStream(new FileInputStream(this.compressFile), charsetName);
            }
        }
    }

    /**
     * 判断文件是否使用 gzip 压缩 <br>
     *
     * @param file tar文件
     * @return 返回true表示是 gzip 压缩，false表示不是 gzip 压缩
     * @throws IOException 访问文件发生错误
     */
    public boolean isGzipCompress(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            int b1 = in.read();
            int b2 = in.read();
            return b1 == 0x1F && b2 == 0x8B; // 只要文件的前两个字节是 0x1F 0x8B 就是 gzip 压缩格式
        } finally {
            in.close();
        }
    }

    /**
     * 遍历输入流后需要关闭
     */
    private void closeInputStream() {
        IO.close(this.inputStream);
        this.inputStream = null;
    }

    public void close() {
        IO.close(this.inputStream, this.outputStream);
    }
}
