package cn.org.expect.os.internal;

import java.io.File;
import java.util.Date;

import cn.org.expect.jdk.JavaDialectFactory;
import cn.org.expect.os.OSFile;
import cn.org.expect.os.linux.Linuxs;
import cn.org.expect.util.Dates;

/**
 * 操作系统上文件的接口实现
 */
public class OSFileImpl implements OSFile {

    private String name;

    private String parent;

    private String absolutePath;

    private long size;

    private Date createTime;

    private Date modifyTime;

    private boolean isDir;

    private boolean isLink;

    private boolean isFile;

    private boolean isBlk;

    private boolean isPipe;

    private boolean isSock;

    private boolean isChr;

    private String link;

    private String longname;

    private boolean canRead;

    private boolean canWrite;

    private boolean canExecute;

    public OSFileImpl() {
    }

    public OSFileImpl(File file) {
        this.setName(file.getName());
        this.setParent(file.getParent());
        this.setModifyTime(new Date(file.lastModified()));
        this.setLength(file.length());
        this.setFile(file.isFile());
        this.setDirectory(file.isDirectory());
        String link = JavaDialectFactory.get().getLink(file);
        if (link != null) {
            this.setLink(true);
            this.setLink(link);
        }
        this.setCreateTime(JavaDialectFactory.get().getCreateTime(this.getAbsolutePath()));
        this.setLongname(Linuxs.toLongname(file).toString());
        this.setCanRead(file.canRead());
        this.setCanWrite(file.canWrite());
        this.setCanExecute(JavaDialectFactory.get().canExecute(file));
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
        this.absolutePath = null;
    }

    public String getLongname() {
        return longname;
    }

    public void setLongname(String line) {
        this.longname = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String filename) {
        this.name = filename;
        this.absolutePath = null;
    }

    public long length() {
        return size;
    }

    public void setLength(long size) {
        this.size = size;
    }

    public Date getCreateDate() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyDate() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isDirectory() {
        return isDir;
    }

    public void setDirectory(boolean isDir) {
        this.isDir = isDir;
    }

    public boolean isLink() {
        return isLink;
    }

    public void setLink(boolean isLink) {
        this.isLink = isLink;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isBlockDevice() {
        return isBlk;
    }

    public void setBlockDevice(boolean isBlk) {
        this.isBlk = isBlk;
    }

    public boolean isPipe() {
        return isPipe;
    }

    public void setPipe(boolean isPipe) {
        this.isPipe = isPipe;
    }

    public boolean isSock() {
        return isSock;
    }

    public void setSock(boolean isSock) {
        this.isSock = isSock;
    }

    public boolean isCharDevice() {
        return isChr;
    }

    public void setCharDevice(boolean isChr) {
        this.isChr = isChr;
    }

    public String getAbsolutePath() {
        if (this.absolutePath == null) {
            StringBuilder buf = new StringBuilder();
            buf.append(this.parent);
            if (this.parent.endsWith("/") || this.parent.endsWith("\\")) {
                if (this.name.startsWith("/") || this.name.startsWith("\\")) {
                    buf.append(this.name.substring(1));
                } else {
                    buf.append(this.name);
                }
            } else {
                if (this.name.startsWith("/") || this.name.startsWith("\\")) {
                    buf.append(this.name);
                } else {
                    int up = this.parent.indexOf('/');
                    if (up == -1) {
                        int wp = this.parent.indexOf('\\');
                        buf.append(wp == -1 ? '/' : '\\');
                    } else {
                        buf.append('/');
                    }
                    buf.append(this.name);
                }
            }
            this.absolutePath = buf.toString();
        }
        return this.absolutePath;
    }

    public boolean canRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean canExecute() {
        return canExecute;
    }

    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }

    public String toString() {
        String str = "[name=" + name + ", ";
        str += "parent=" + this.parent + ", ";
        str += "size=" + size + ", ";
        str += "createTime=" + Dates.format21(this.createTime) + ", ";
        str += "modifyTime=" + Dates.format21(this.modifyTime) + ", ";
        str += "isFile=" + this.isFile + ", ";
        str += "isDir=" + this.isDir + ", ";
        str += "isBlk=" + this.isBlk + ", ";
        str += "isChr=" + this.isChr + ", ";
        str += "isLink=" + this.isLink + ", ";
        str += "isPipe=" + this.isPipe + ", ";
        str += "isSock=" + this.isSock + ", ";
        str += "link=" + this.link + ", ";
        str += "canRead=" + this.canRead + ", ";
        str += "canWrite=" + this.canWrite + ", ";
        str += "canExecute=" + this.canExecute + ", ";
        str += "longname=" + this.longname + "]";
        return str;
    }
}
