package cn.org.expect.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;
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
public class ZipCompress extends Terminator implements Compress {
    private final static Log log = LogFactory.getLog(ZipCompress.class);

    private File zipFile;

    private ZipArchiveOutputStream zos;

    public ZipCompress() {
    }

    public void setFile(File file) {
        this.zipFile = file;
    }

    public void archiveFile(File file, String dir) throws IOException {
        this.addFile(file, dir, null, 0);
    }

    public void archiveFile(File file, String dir, String charsetName) throws IOException {
        this.addFile(file, dir, charsetName, 0);
    }

    protected void addFile(File file, String dir, String charsetName, int level) throws IOException {
        Ensure.notNull(this.zipFile);
        FileUtils.assertExists(file);

        if (this.zos == null) {
            this.zos = new ZipArchiveOutputStream(new FileOutputStream(this.zipFile));
        }

        this.zos.setEncoding(CharsetUtils.get(charsetName));

        // 处理目录
        dir = (dir == null) ? "" : dir.trim();
        if (dir.equals("/")) {
            dir = "";
        }

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

            if (log.isDebugEnabled()) {
                log.debug("zip file, create dir: {} ..", dirpath);
            }

            ZipArchiveEntry entry = new ZipArchiveEntry(dirpath);
            this.zos.putArchiveEntry(entry);
            this.zos.closeArchiveEntry();

            // 遍历目录下的所有文件并压入压缩包中的目录下
            File[] files = FileUtils.array(file.listFiles());
            for (int i = 0; i < files.length; i++) {
                if (this.terminate) {
                    break;
                } else {
                    this.addFile(files[i], dirpath, charsetName, level + 1);
                }
            }
        } else {
            if (dir.length() > 1 && !dir.equals("//")) { // 创建父目录
                String d = dir.charAt(0) == '/' ? dir.substring(1) : dir;
                if (d.length() > 1) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(d);
                    this.zos.putArchiveEntry(entry);
                    this.zos.closeArchiveEntry();
                }
            }

            String zipfile = dir + file.getName();
            InputStream in = new FileInputStream(file);
            try {
                if (log.isDebugEnabled()) {
                    if (StringUtils.isBlank(dir)) {
                        log.debug("zip {} {} ..", file.getAbsolutePath(), this.zipFile.getAbsolutePath());
                    } else {
                        log.debug("zip {} {} -> {} ..", file.getAbsolutePath(), this.zipFile.getAbsolutePath(), dir);
                    }
                }

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
    }

    public void extract(File outputDir, String charsetName) throws IOException {
        FileUtils.assertFile(this.zipFile);
        FileUtils.assertCreateDirectory(outputDir);

        ZipFile file = new ZipFile(zipFile, CharsetUtils.get(charsetName));
        try {
            if (log.isDebugEnabled()) {
                log.debug("unzip " + zipFile + " " + outputDir + " ..");
            }

            byte[] buffer = new byte[128];
            for (Enumeration<ZipArchiveEntry> it = file.getEntries(); it.hasMoreElements(); ) {
                if (this.terminate) {
                    break;
                }

                ZipArchiveEntry entry = it.nextElement();
                String filePath = FileUtils.joinPath(outputDir.getAbsolutePath(), entry.getName());

                if (log.isDebugEnabled()) {
                    log.debug("unzip " + entry.getName() + " " + outputDir + " ..");
                }

                if (entry.isDirectory()) {
                    FileUtils.assertCreateDirectory(filePath);
                } else {
                    this.tofile(file, entry, filePath, buffer);
                }
            }
        } finally {
            file.close();
        }
    }

    public void extract(File outputDir, String charsetName, String entryName) throws IOException {
        FileUtils.assertFile(this.zipFile);
        Ensure.notBlank(entryName);
        FileUtils.assertCreateDirectory(outputDir);

        ZipFile file = new ZipFile(this.zipFile, CharsetUtils.get(charsetName));
        try {
            if (log.isDebugEnabled()) {
                log.debug("unzip " + this.zipFile + " -> " + entryName + " " + outputDir + " ..");
            }

            byte[] buffer = new byte[128];
            Iterable<ZipArchiveEntry> itr = file.getEntries(entryName);
            for (ZipArchiveEntry entry : itr) {
                if (this.terminate) {
                    break;
                }

                String filePath = FileUtils.joinPath(outputDir.getAbsolutePath(), entry.getName());
                if (entry.isDirectory()) {
                    FileUtils.assertCreateDirectory(filePath);
                } else {
                    this.tofile(file, entry, filePath, buffer);
                }
            }
        } finally {
            file.close();
        }
    }

    public List<ZipEntry> getEntrys(String charsetName, String filename, boolean ignoreCase) throws IOException {
        FileUtils.assertFile(this.zipFile);
        Ensure.notBlank(filename);

        ZipFile file = new ZipFile(this.zipFile, CharsetUtils.get(charsetName));
        try {
            List<ZipEntry> list = new ArrayList<ZipEntry>();
            for (Enumeration<ZipArchiveEntry> it = file.getEntries(); it.hasMoreElements(); ) {
                if (this.terminate) {
                    break;
                }

                ZipEntry entry = it.nextElement();
                String name = FileUtils.getFilename(entry.getName());
                if (ignoreCase) {
                    if (name.equalsIgnoreCase(filename)) {
                        list.add(entry);
                    }
                } else {
                    if (name.equals(filename)) {
                        list.add(entry);
                    }
                }
            }
            return list;
        } finally {
            file.close();
        }
    }

    public boolean removeEntry(String charsetName, String... entryNames) throws IOException {
        File tmpDir = FileUtils.createTempDirectory(ZipCompress.class.getSimpleName());
        try {
            ZipFile file = new ZipFile(this.zipFile, charsetName); // 解压缩文件
            try {
                if (log.isDebugEnabled()) {
                    log.debug("delete zipfile " + this.zipFile + "'s entry: " + StringUtils.join(entryNames, ", ") + " ..");
                }

                byte[] buffer = new byte[128];
                for (Enumeration<ZipArchiveEntry> it = file.getEntries(); it.hasMoreElements(); ) {
                    if (this.terminate) {
                        break;
                    }

                    ZipArchiveEntry entry = it.nextElement();
                    if (this.find(entryNames, entry)) {
                        continue;
                    }

                    String filepath = FileUtils.joinPath(tmpDir.getAbsolutePath(), entry.getName());
                    if (entry.isDirectory()) {
                        FileUtils.assertCreateDirectory(filepath);
                    } else {
                        this.tofile(file, entry, filepath, buffer);
                    }
                }
            } finally {
                file.close();
            }

            // 重新压缩
            File newzipFile = new File(tmpDir.getAbsolutePath(), this.zipFile.getName());
            ZipCompress c = new ZipCompress();
            try {
                c.setFile(newzipFile);
                File[] list = FileUtils.array(tmpDir.listFiles());
                for (File child : list) {
                    if (this.terminate) {
                        break;
                    } else {
                        c.addFile(child, null, charsetName, 0);
                    }
                }
            } finally {
                c.close();
            }

            return this.zipFile.delete() && FileUtils.rename(newzipFile, this.zipFile, null);
        } finally {
            FileUtils.delete(tmpDir);
        }
    }

    private boolean find(String[] array, ZipArchiveEntry entry) {
        boolean success = false;
        for (String name : array) {
            if (entry.getName().indexOf(name) == 0) {
                success = true;
                break;
            }
        }
        return success;
    }

    public void close() {
        IO.close(this.zos);
    }

    /**
     * 把压缩包中的 ZipEntry 转换为 File
     */
    protected void tofile(ZipFile file, ZipArchiveEntry entry, String filepath, byte[] buffer) throws IOException {
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
}
