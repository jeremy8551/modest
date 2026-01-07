package cn.org.expect.increment.sort;

import java.io.File;
import java.io.FilenameFilter;

import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 临时文件工厂
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023-09-23
 */
public class TempFileFactory {

    private final Object lock1 = new Object();

    private final Object lock2 = new Object();

    private final Object lock3 = new Object();

    /** 排序文件 */
    private final File file;

    /** 临时文件存储目录 */
    private File parent;

    /**
     * 初始化
     *
     * @param context 排序文件的上下文信息
     * @param file    数据文件
     */
    public TempFileFactory(TableFileSortContext context, File file) throws SortTableFileException {
        this.file = Ensure.notNull(file);

        // 目录，在这个目录下建立临时文件目录
        File dir = context.getTempDir();
        if (dir == null) {
            dir = file.getParentFile(); // 使用文件所在目录
        }

        // 使用文件所在目录
        if (this.create(dir)) {
            return;
        }

        // 直接在数据文件所在的目录下创建临时文件
        File newfile = FileUtils.allocate(file.getParentFile(), null);
        if (FileUtils.createFile(newfile) && FileUtils.delete(newfile)) {
            this.parent = null;
            return;
        }

        // 使用临时目录
        if (this.create(FileUtils.getTempDir(TempFileFactory.class.getSimpleName(), "sort", "file"))) {
            return;
        }

        throw new SortTableFileException("increment.stdout.message042", context.getName(), this.parent.getAbsolutePath());
    }

    /**
     * 创建目录
     *
     * @param dir 目录
     * @return 返回true表示操作成功 false表示操作失败
     */
    protected boolean create(File dir) {
        String dirName = "." + FileUtils.getFilenameNoSuffix(this.file.getName());
        this.parent = new File(dir, dirName);

        // 创建目录
        if (FileUtils.createDirectory(this.parent)) {
            FileUtils.assertClearDirectory(this.parent);
        } else {
            this.parent = null;
            return false;
        }

        // 尝试创建文件
        File newfile = FileUtils.allocate(this.parent, null);
        if (FileUtils.createFile(newfile)) {
            return FileUtils.deleteFile(newfile);
        } else {
            this.parent = null;
            return false;
        }
    }

    /**
     * 返回最终排序结果文件
     *
     * @return 排序结果文件
     */
    public File toSortfile() {
        String filename = FileUtils.changeFilenameExt(this.file.getName(), "sort");
        return FileUtils.allocate(this.file.getParentFile(), filename);
    }

    /**
     * 返回备份文件
     *
     * @return 备份文件
     */
    public File toBakfile() {
        return FileUtils.allocate(this.file.getParentFile(), this.file.getName());
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFiles() {
        if (this.parent == null) {
            File parent = this.file.getParentFile();
            File[] array = FileUtils.array(parent.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String filename = FileUtils.getFilenameNoExt(name);
                    String ext = FileUtils.getFilenameExt(name);
                    String prefix = file.getName();
                    if (filename.startsWith(prefix) && StringUtils.inArray(ext, "list", "merge", "temp")) {
                        String part = filename.substring(prefix.length());
                        return StringUtils.isNumber(part);
                    }
                    return false;
                }
            }));

            for (File file : array) {
                FileUtils.deleteFile(file);
            }
        } else {
            FileUtils.delete(this.parent, 10, 100);
        }
    }

    /**
     * 生成清单文件
     *
     * @return 清单文件
     */
    public File createListFile() {
        synchronized (this.lock1) {
            if (this.parent == null) {
                return FileUtils.createNewFile(this.file.getParentFile(), this.file.getName() + ".list");
            } else {
                return FileUtils.createNewFile(this.parent, "list" + Dates.format17());
            }
        }
    }

    /**
     * 生成合并后的临时文件
     *
     * @return 临时文件
     */
    public File createMergeFile() {
        synchronized (this.lock2) {
            if (this.parent == null) {
                return FileUtils.createNewFile(this.file.getParentFile(), this.file.getName() + ".merge");
            } else {
                return FileUtils.createNewFile(this.parent, "merge" + Dates.format17());
            }
        }
    }

    /**
     * 生成临时文件
     *
     * @return 临时文件
     */
    public File createTempFile() {
        synchronized (this.lock3) {
            if (this.parent == null) {
                return FileUtils.createNewFile(this.file.getParentFile(), this.file.getName() + ".temp");
            } else {
                return FileUtils.createNewFile(this.parent, "temp" + Dates.format17());
            }
        }
    }

    /**
     * 返回上级目录
     *
     * @return 目录
     */
    public File getParent() {
        if (this.parent == null) {
            return this.file.getParentFile();
        } else {
            return this.parent;
        }
    }
}
