package cn.org.expect.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import cn.org.expect.util.Terminator;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

@EasyBean(value = "tar")
public class TarCompress extends Terminator implements Compress {
    private final static Log log = LogFactory.getLog(TarCompress.class);

    private File tarFile;

    private TarArchiveOutputStream outputStream;

    private TarArchiveInputStream inputStream;

    /** 缓冲区 */
    protected byte[] buffer;

    /** true表示tar文件使用gzip格式压缩 */
    protected boolean isGzipCompress = false;

    /**
     * 构造函数
     */
    public TarCompress() {
        this.buffer = new byte[512];
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
        this.setFile(file);
        this.buffer = new byte[size];
        this.setGzipCompress(b);
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

    public void archiveFile(File file, String dir) throws IOException {
        this.addFile(file, dir, CharsetUtils.get(), 0);
    }

    public void archiveFile(File file, String dir, String charsetName) throws IOException {
        this.addFile(file, dir, charsetName, 0);
    }

    protected void addFile(File file, String dir, String charsetName, int level) throws IOException {
        Ensure.notNull(this.tarFile);
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

            if (log.isDebugEnabled()) {
                if (StringUtils.isBlank(dir)) {
                    log.debug("tar " + file.getAbsolutePath() + " " + this.tarFile.getAbsolutePath() + " ..");
                } else {
                    log.debug("tar " + file.getAbsolutePath() + " " + this.tarFile.getAbsolutePath() + " -> " + dir + " ..");
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
        FileUtils.assertCreateDirectory(outputDir);
        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                } else {
                    this.untar(this.inputStream, outputDir.getAbsolutePath(), entry);
                }
            }
        } finally {
            this.closeInputStream();
        }
    }

    public void extract(String outputDir, String charsetName, String... excludeNames) throws IOException {
        FileUtils.assertCreateDirectory(outputDir);
        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                } else if (!StringUtils.inArray(entry.getName(), excludeNames)) {
                    this.untar(this.inputStream, outputDir, entry);
                }
            }
        } finally {
            this.closeInputStream();
        }
    }

    public void extract(File outputDir, String charsetName, String entryName) throws IOException {
        FileUtils.assertCreateDirectory(outputDir);
        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                } else if (entry.getName().equals(entryName)) {
                    this.untar(this.inputStream, outputDir.getAbsolutePath(), entry);
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
    public void untar(TarArchiveInputStream in, String outputDir, TarArchiveEntry entry) throws IOException {
        Ensure.notNull(entry);
        if (entry.isDirectory()) { // 如果是目录
            File dir = new File(outputDir, entry.getName());
            FileUtils.assertCreateDirectory(dir);
            ArchiveEntry[] entries = entry.getDirectoryEntries(); // 遍历目录下的文件
            for (int i = 0; i < entries.length; i++) {
                if (this.terminate) {
                    break;
                } else {
                    this.untar(in, dir.getAbsolutePath(), entries[i].getName());
                }
            }
        } else {
            this.untar(in, outputDir, entry.getName());
        }
    }

    /**
     * 解压tar文件到指定目录
     *
     * @param inputStream io数据流
     * @param outputDir   解压后目录
     * @param filename    文件名
     * @throws IOException 访问文件错误
     */
    public void untar(TarArchiveInputStream inputStream, String outputDir, String filename) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("untar {} ..", FileUtils.replaceFolderSeparator(FileUtils.joinPath(outputDir, filename)));
        }

        FileOutputStream out = new FileOutputStream(new File(outputDir, filename), false);
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

    public List<TarArchiveEntry> getEntrys(String charsetName, String regex, boolean ignoreCase) throws IOException {
        this.initInputStream(CharsetUtils.get(charsetName));
        try {
            List<TarArchiveEntry> list = new ArrayList<TarArchiveEntry>();
            TarArchiveEntry entry;
            while ((entry = this.inputStream.getNextTarEntry()) != null) {
                if (this.terminate) {
                    break;
                } else {
                    this.getTarEntrys(entry, regex, ignoreCase, list);
                }
            }
            return list;
        } finally {
            this.closeInputStream();
        }
    }

    protected void getTarEntrys(TarArchiveEntry entry, String regex, boolean ignoreCase, List<TarArchiveEntry> list) {
        if (entry.isDirectory()) {
            String[] array = StringUtils.split(FileUtils.replaceFolderSeparator(entry.getName()), File.separatorChar);
            int index = StringUtils.lastIndexOfNotBlank(array);
            String name = index >= 0 ? array[index] : null;
            if (name != null && this.match(name, regex, ignoreCase)) {
                list.add(entry);
            }

            TarArchiveEntry[] entries = entry.getDirectoryEntries();
            for (TarArchiveEntry tarEntry : entries) {
                if (this.terminate) {
                    break;
                } else {
                    this.getTarEntrys(tarEntry, regex, ignoreCase, list);
                }
            }
        } else if (this.match(FileUtils.getFilename(entry.getName()), regex, ignoreCase)) {
            list.add(entry);
        }
    }

    /**
     * 判断 entryName 与 regex 是否匹配
     *
     * @param entryName  压缩实例名
     * @param regex      正则表达式
     * @param ignoreCase 忽略大小写
     * @return 返回true表示压缩包中对象与正则表达式匹配 false表示不匹配
     */
    protected boolean match(String entryName, String regex, boolean ignoreCase) {
        if (ignoreCase) {
            return entryName.equalsIgnoreCase(regex);
        } else {
            return entryName.equals(regex) || entryName.matches(regex);
        }
    }

    public boolean removeEntry(String charsetName, String... entryNames) throws IOException {
        charsetName = CharsetUtils.get(charsetName);
        String dirName = StringUtils.replaceAll(FileUtils.getFilenameNoExt(this.tarFile.getName()) + Numbers.getRandom() + Numbers.getRandom() + Numbers.getRandom() + Numbers.getRandom() + "tmp", ".", "");
        File tmpDir = new File(FileUtils.joinPath(this.tarFile.getParent(), dirName));
        FileUtils.assertCreateDirectory(tmpDir);
        try {
            this.extract(tmpDir.getAbsolutePath(), charsetName, entryNames);
            this.close();

            // 重新压缩
            File newTarfile = new File(tmpDir, this.tarFile.getName());
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

            return FileUtils.delete(this.tarFile) && FileUtils.rename(newTarfile, this.tarFile, null);
        } finally {
            FileUtils.delete(tmpDir);
        }
    }

    protected void initOutputStream(String charsetName) throws IOException, FileNotFoundException {
        if (this.outputStream == null) {
            if (this.isGzipCompress()) {
                this.outputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(this.tarFile, true)), charsetName);
            } else {
                this.outputStream = new TarArchiveOutputStream(new FileOutputStream(this.tarFile, true), charsetName);
            }
        }
    }

    protected void initInputStream(String charsetName) throws IOException {
        if (this.inputStream == null) {
            if (this.isGzipCompress()) {
                this.inputStream = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(this.tarFile)), charsetName);
            } else {
                this.inputStream = new TarArchiveInputStream(new FileInputStream(this.tarFile), charsetName);
            }
        }
    }

    public void setFile(File file) {
        this.tarFile = file;
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
