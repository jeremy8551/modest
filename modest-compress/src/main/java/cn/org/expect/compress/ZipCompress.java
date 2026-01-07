package cn.org.expect.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * ZIP压缩接口
 *
 * @author jeremy8551@gmail.com
 * @createtime 2014-07-23 16:16:03
 */
@EasyBean(value = "zip")
public class ZipCompress extends AbstractCompress {

    private ZipArchiveOutputStream zos;

    /** 递归压缩目录（必用） */
    private boolean recursion;

    /** 移动模式（压缩后删除源文件） */
    private boolean mobileMode;

    public ZipCompress() throws IOException {
        super();
        this.setLogWriter(new CompressLogWriter());
        this.mobileMode = false;
        this.recursion = false;
    }

    /**
     * 设置是否使用递归压缩目录
     *
     * @param recursion true表示使用递归压缩
     */
    public void setRecursion(final boolean recursion) {
        this.recursion = recursion;
    }

    /**
     * 设置是否使用移动模式（压缩后删除源文件）
     *
     * @param mobileMode true表示使用移动模式
     */
    public void setMobileMode(final boolean mobileMode) {
        this.mobileMode = mobileMode;
    }

    public void archiveFile(File file, String dir) throws IOException {
        this.addFile(file, dir, null, 0);
    }

    public void archiveFile(File file, String dir, String charsetName) throws IOException {
        this.addFile(file, dir, charsetName, 0);
    }

    protected void addFile(File file, String dir, String charsetName, int level) throws IOException {
        Ensure.notNull(this.compressFile);
        FileUtils.assertExists(file);

        if (this.zos == null) {
            this.zos = new ZipArchiveOutputStream(new FileOutputStream(this.compressFile));
        }

        this.zos.setEncoding(CharsetUtils.get(charsetName));

        if (this.verbose && this.canWriteLog()) {
            this.writeLog("   adding: " + file.getName());
        }

        if (file.isDirectory()) {
            String dirpath = "";
            File root = new File(file.getAbsolutePath());
            for (int i = 0; i < level; i++) {
                if (this.terminate) {
                    break;
                } else {
                    root = new File(root.getParent());
                    dirpath = root.getName() + "/" + dirpath;
                }
            }
            dirpath = dirpath + file.getName() + "/";

            ZipArchiveEntry entry = new ZipArchiveEntry(dirpath);
            this.zos.putArchiveEntry(entry);
            this.zos.closeArchiveEntry();

            // 遍历目录下的所有文件并压入压缩包中的目录下
            if (this.recursion) {
                File[] files = FileUtils.array(file.listFiles());
                for (int i = 0; i < files.length; i++) {
                    if (this.terminate) {
                        break;
                    } else {
                        this.addFile(files[i], dirpath, charsetName, level + 1);
                    }
                }
            }
        } else {
            // 处理目录
            dir = (dir == null) ? "" : StringUtils.trimBlank(dir);
            if (dir.equals("/")) {
                dir = "";
            }

            dir = StringUtils.ltrimBlank(dir, '/'); // 去掉最前面的斜线

            // 添加最后面的斜线
            if (dir.length() > 1 && dir.charAt(dir.length() - 1) != '/') {
                dir = dir + "/";
            }

            if (dir.length() > 1 && !dir.equals("//")) { // 创建父目录
                String entryName = StringUtils.ltrim(dir, '/');
                if (entryName.length() > 1) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    this.zos.putArchiveEntry(entry);
                    this.zos.closeArchiveEntry();
                }
            }

            String zipfile = dir + file.getName();
            InputStream in = new FileInputStream(file);
            try {
                ZipArchiveEntry entry = new ZipArchiveEntry(zipfile);
                this.zos.putArchiveEntry(entry);
                byte[] buffer = new byte[1024];
                for (int len; (len = in.read(buffer)) != -1; ) {
                    if (this.terminate) {
                        break;
                    } else {
                        this.zos.write(buffer, 0, len);
                    }
                }
                this.zos.closeArchiveEntry();
            } finally {
                in.close();
            }
        }

        // 删除源文件
        if (this.mobileMode) {
            FileUtils.delete(file);
        }
    }

    public void extract(File outputDir, String charsetName) throws IOException {
        FileUtils.assertFile(this.compressFile);
        FileUtils.assertCreateDirectory(outputDir);

        ZipFile zip = new ZipFile(this.compressFile, CharsetUtils.get(charsetName));
        try {
            byte[] buffer = new byte[128];
            for (Enumeration<ZipArchiveEntry> it = zip.getEntries(); it.hasMoreElements(); ) {
                if (this.terminate) {
                    break;
                }

                ZipArchiveEntry entry = it.nextElement();
                File file = new File(FileUtils.joinPath(outputDir.getAbsolutePath(), entry.getName()));
                if (this.verbose && this.canWriteLog()) {
                    this.writeLog((file.exists() ? "  inflating: " : "   creating: ") + FileUtils.replaceFolderSeparator(entry.getName(), '/'));
                }

                if (entry.isDirectory()) {
                    FileUtils.assertCreateDirectory(file);
                } else {
                    this.tofile(zip, entry, file, buffer);
                }
            }
        } finally {
            zip.close();
        }
    }

    public void extract(File outputDir, String charsetName, String entryName) throws IOException {
        FileUtils.assertFile(this.compressFile);
        Ensure.notBlank(entryName);
        FileUtils.assertCreateDirectory(outputDir);

        ZipFile zip = new ZipFile(this.compressFile, CharsetUtils.get(charsetName));
        try {
            byte[] buffer = new byte[128];
            Iterable<ZipArchiveEntry> itr = zip.getEntries(entryName);
            for (ZipArchiveEntry entry : itr) {
                if (this.terminate) {
                    break;
                }

                File file = new File(FileUtils.joinPath(outputDir.getAbsolutePath(), entry.getName()));
                if (this.verbose && this.canWriteLog()) {
                    this.writeLog((file.exists() ? "  inflating: " : "   creating: ") + FileUtils.replaceFolderSeparator(entry.getName(), '/'));
                }

                if (entry.isDirectory()) {
                    FileUtils.assertCreateDirectory(file);
                } else {
                    this.tofile(zip, entry, file, buffer);
                }
            }
        } finally {
            zip.close();
        }
    }

    public boolean removeEntry(String charsetName, String... entryNames) throws IOException {
        File tmpDir = FileUtils.createTempDirectory(ZipCompress.class.getSimpleName());
        try {
            ZipFile zip = new ZipFile(this.compressFile, charsetName); // 解压缩文件
            try {
                byte[] buffer = new byte[128];
                for (Enumeration<ZipArchiveEntry> it = zip.getEntries(); it.hasMoreElements(); ) {
                    if (this.terminate) {
                        break;
                    }

                    ZipArchiveEntry entry = it.nextElement();
                    if (this.match(entry, entryNames)) {
                        continue;
                    }

                    File file = new File(FileUtils.joinPath(tmpDir.getAbsolutePath(), entry.getName()));
                    if (entry.isDirectory()) {
                        FileUtils.assertCreateDirectory(file);
                    } else {
                        this.tofile(zip, entry, file, buffer);
                    }
                }
            } finally {
                zip.close();
            }

            // 重新压缩
            File newZipfile = new File(tmpDir.getAbsolutePath(), this.compressFile.getName());
            ZipCompress compress = new ZipCompress();
            try {
                compress.setRecursion(true);
                compress.setFile(newZipfile);
                File[] list = FileUtils.array(tmpDir.listFiles());
                for (File child : list) {
                    if (this.terminate) {
                        break;
                    } else {
                        compress.addFile(child, null, charsetName, 0);
                    }
                }

                return this.compressFile.delete() && FileUtils.rename(newZipfile, this.compressFile, null);
            } finally {
                compress.close();
            }
        } finally {
            FileUtils.delete(tmpDir);
        }
    }

    protected boolean match(ZipArchiveEntry entry, String[] array) {
        String str = FileUtils.replaceFolderSeparator(entry.getName(), '/');
        boolean success = false;
        for (String name : array) {
            String prefix = FileUtils.replaceFolderSeparator(StringUtils.trim(name, '/', '\\'), '/') + "/";
            if (str.startsWith(prefix)) {
                success = true;
                break;
            }
        }
        return success;
    }

    /**
     * 把压缩包中的 ZipEntry 转换为 File
     */
    protected void tofile(ZipFile file, ZipArchiveEntry entry, File filepath, byte[] buffer) throws IOException {
        FileUtils.assertCreateFile(filepath);
        InputStream in = file.getInputStream(entry);
        try {
            FileOutputStream out = new FileOutputStream(filepath);
            try {
                for (int len = in.read(buffer); len != -1; len = in.read(buffer)) {
                    if (this.terminate) {
                        break;
                    } else {
                        out.write(buffer, 0, len);
                    }
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public void close() {
        IO.close(this.zos);
    }
}
