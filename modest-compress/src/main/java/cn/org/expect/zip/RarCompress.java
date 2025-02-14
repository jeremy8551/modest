package cn.org.expect.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Terminator;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

@EasyBean(value = "rar")
public class RarCompress extends Terminator implements Compress {
    private final static Log log = LogFactory.getLog(RarCompress.class);

    private Archive archive;

    private File rarFile;

    public RarCompress() {
    }

    public void archiveFile(File file, String dir) {
        throw new UnsupportedOperationException();
    }

    public void archiveFile(File file, String dir, String charsetName) {
        throw new UnsupportedOperationException();
    }

    public void extract(File outputDir, String charsetName) throws IOException {
        if (outputDir == null) {
            outputDir = this.rarFile.getParentFile();
        }

        List<FileHeader> headers = this.archive.getFileHeaders();
        for (FileHeader head : headers) {
            if (this.terminate) {
                break;
            } else {
                this.unrar(outputDir.getAbsolutePath(), charsetName, head);
            }
        }
    }

    public void extract(File outputDir, String charsetName, String entryName) throws IOException {
        List<FileHeader> headers = this.archive.getFileHeaders();
        for (FileHeader head : headers) {
            if (this.terminate) {
                break;
            }

//            String filename = head.isUnicode() ? head.getFileNameW() : head.getFileNameString(); // 文件entryName
            String filename = head.getFileName(); // 文件entryName
            String name = filename.replace('\\', '/');
            if (name.equals(entryName)) {
                this.unrar(outputDir.getAbsolutePath(), charsetName, head);
            }
        }
    }

    /**
     * 解压rar文件
     *
     * @param outputDir   解压后根目录（null表示解压到当前目录）
     * @param charsetName 压缩文件的字符集
     * @param head        头信息
     * @throws IOException 访问文件错误
     */
    public void unrar(String outputDir, String charsetName, FileHeader head) throws IOException {
//        String filename = head.isUnicode() ? head.getFileNameW() : head.getFileNameString(); // 文件entryName
        String filename = head.getFileName(); // 文件entryName
        String filepath = FileUtils.replaceFolderSeparator(FileUtils.joinPath(outputDir, filename));
        if (head.isDirectory()) {
            if (log.isDebugEnabled()) {
                log.debug("unrar " + filepath + " ..");
            }

            File dir = new File(filepath);
            FileUtils.assertCreateDirectory(dir);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("unrar " + filepath + " ..");
            }

            File file = new File(filepath);
            FileUtils.assertCreateDirectory(file.getParentFile());
            FileOutputStream out = new FileOutputStream(file, false);
            try {
                this.archive.extractFile(head, out);
            } catch (RarException e) {
                throw new IOException(outputDir);
            } finally {
                out.close();
            }
        }
    }

    public List<FileHeader> getEntrys(String charsetName, String regex, boolean ignoreCase) {
        List<FileHeader> headers = this.archive.getFileHeaders();
        List<FileHeader> list = new ArrayList<FileHeader>(headers.size());
        for (FileHeader head : headers) {
            if (this.terminate) {
                break;
            }

            if (regex != null) {
//                String name = head.isUnicode() ? head.getFileNameW() : head.getFileNameString(); // 文件entryName
                String name = head.getFileName(); // 文件entryName
                name = name.replace('\\', '/');
                String fname = FileUtils.getFilename(regex);
                if (ignoreCase) {
                    if (name.equalsIgnoreCase(fname)) {
                        list.add(head);
                    }
                } else {
                    if (name.equals(fname) || name.matches(regex)) {
                        list.add(head);
                    }
                }
            }
        }
        return list;
    }

    public void setFile(File file) throws IOException {
        IO.close(this.archive);
        try {
            this.archive = new Archive(file);
            this.rarFile = file;

            if (log.isDebugEnabled()) {
                this.archive.getMainHeader().print();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public boolean removeEntry(String charsetName, String... entryName) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        IO.close(this.archive);
    }
}
