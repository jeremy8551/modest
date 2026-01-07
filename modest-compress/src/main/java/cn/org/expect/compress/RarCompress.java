package cn.org.expect.compress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

@EasyBean(value = "rar")
public class RarCompress extends AbstractCompress {
    private final static Log log = LogFactory.getLog(RarCompress.class);

    private Archive archive;

    public RarCompress() throws UnsupportedEncodingException {
        super();
        this.setLogWriter(new CompressLogWriter());
    }

    public void archiveFile(File file, String dir) {
        throw new UnsupportedOperationException();
    }

    public void archiveFile(File file, String dir, String charsetName) {
        throw new UnsupportedOperationException();
    }

    public void extract(File outputDir, String charsetName) throws IOException {
        if (outputDir == null) {
            outputDir = this.compressFile.getParentFile();
        }

        this.writeLog("Extracting from " + this.compressFile.getAbsolutePath());
        List<FileHeader> headers = this.sort(this.archive.getFileHeaders());
        int maxLength = this.getMaxLength(headers, charsetName);
        for (FileHeader head : headers) {
            if (this.terminate) {
                break;
            }

            this.unrar(outputDir, charsetName, head, maxLength);
        }

        this.writeLog("All OK");
    }

    public void extract(File outputDir, String charsetName, String entryName) throws IOException {
        if (outputDir == null) {
            outputDir = this.compressFile.getParentFile();
        }

        this.writeLog("Extracting from " + this.compressFile.getAbsolutePath());
        List<FileHeader> headers = this.sort(this.archive.getFileHeaders());
        int maxLength = this.getMaxLength(headers, charsetName);
        for (FileHeader head : headers) {
            if (this.terminate) {
                break;
            }

            String filename = head.getFileName(); // 文件entryName
            String name = filename.replace('\\', '/');
            if (name.equals(entryName)) {
                this.unrar(outputDir, charsetName, head, maxLength);
            }
        }

        this.writeLog("All OK");
    }

    /**
     * 解压rar文件
     *
     * @param outputDir   解压后根目录（null表示解压到当前目录）
     * @param charsetName 压缩文件的字符集
     * @param head        头信息
     * @param maxLength   输出日志的最大长度
     * @throws IOException 访问文件错误
     */
    public void unrar(File outputDir, String charsetName, FileHeader head, int maxLength) throws IOException {
        String filename = head.getFileName(); // 文件entryName
        String filepath = FileUtils.replaceFolderSeparator(FileUtils.joinPath(outputDir.getAbsolutePath(), filename));

        if (head.isDirectory()) {
            if (log.isDebugEnabled()) {
                log.debug("unrar " + filepath + " ..");
            }

            File dir = new File(filepath);
            if (!dir.exists()) {
                if (this.verbose && this.canWriteLog()) {
                    this.writeLog("Creating", 17, FileUtils.replaceFolderSeparator(filename, '/'), Math.max(maxLength, 50), "OK", 5, charsetName);
                }

                FileUtils.assertCreateDirectory(dir);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("unrar " + filepath + " ..");
            }

            File file = new File(filepath);

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                if (this.verbose && this.canWriteLog()) {
                    this.writeLog("Creating", 17, FileUtils.getParent(FileUtils.replaceFolderSeparator(filename, '/')), Math.max(maxLength, 50), "OK", 5, charsetName);
                }
            }

            if (this.verbose && this.canWriteLog()) {
                this.writeLog("Extracting", 17, FileUtils.replaceFolderSeparator(filename, '/'), Math.max(maxLength, 50), "OK", 5, charsetName);
            }

            FileUtils.createFile(file);
            FileOutputStream out = new FileOutputStream(file, false);
            try {
                this.archive.extractFile(head, out);
            } catch (RarException e) {
                throw new IOException(outputDir.getAbsolutePath());
            } finally {
                out.close();
            }
        }
    }

    /**
     * 按目录层级排序
     *
     * @param headers FileHeader集合
     * @return 集合副本
     */
    public List<FileHeader> sort(List<FileHeader> headers) {
        List<FileHeader> list = new ArrayList<FileHeader>(headers);
        Collections.sort(list, new Comparator<FileHeader>() {
            public int compare(FileHeader o1, FileHeader o2) {
                ArrayList<String> delimiter = ArrayUtils.asList("\\", "/");
                String[] a1 = StringUtils.split(o1.getFileName(), delimiter, false);
                String[] a2 = StringUtils.split(o2.getFileName(), delimiter, false);
                return a1.length - a2.length;
            }
        });
        return list;
    }

    /**
     * 计算最大的 entry 名长度
     *
     * @param headers     FileHeader集合
     * @param charsetName 字符集
     * @return entry 名的最大长度
     */
    protected int getMaxLength(List<FileHeader> headers, String charsetName) {
        int max = 0;
        for (FileHeader head : headers) {
            if (this.terminate) {
                break;
            } else {
                int length = StringUtils.length(head.getFileName(), charsetName);
                if (length > max) {
                    max = length;
                }
            }
        }
        return max;
    }

    public void setFile(File file) throws IOException {
        IO.close(this.archive);
        try {
            this.archive = new Archive(file);
            this.compressFile = file;

            if (log.isDebugEnabled()) {
                this.archive.getMainHeader().print();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }

            e.printStackTrace();
            throw new IOException(file.getAbsolutePath());
        }
    }

    public boolean removeEntry(String charsetName, String... entryName) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        IO.close(this.archive);
    }
}
