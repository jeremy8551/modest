package cn.org.expect.intellij.idea.plugin.maven.action;

import java.io.File;
import java.io.OutputStreamWriter;

import cn.org.expect.intellij.idea.plugin.maven.MavenSearchPlugin;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.MavenMessage;
import cn.org.expect.maven.search.ArtifactSearchNotification;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;

/**
 * 删除 Maven 本地仓库中的 *.lastUpdated 文件
 */
public class CleanLocalRepositoryActioin extends LocalRepositoryAction {
    private final static Log log = LogFactory.getLog(CleanLocalRepositoryActioin.class);

    /** 锁 */
    protected final static Object lock = new Object();

    /** 找到的文件数量 */
    private int find;

    /** 删除的文件数量 */
    private int success;

    public CleanLocalRepositoryActioin() {
        super(MavenMessage.get("maven.search.delete.local.repository.lastUpdated.menu"));
    }

    public void execute(MavenSearchPlugin plugin, File repository) {
        this.find = 0;
        this.success = 0;

        synchronized (lock) {
            File logfile = FileUtils.createTempFile("clean_last_updated.log");

            OutputStreamWriter out = null;
            try {
                if (FileUtils.createFile(logfile)) {
                    try {
                        out = IO.getFileWriter(logfile, CharsetName.UTF_8, true);
                    } catch (Throwable e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
                this.execute(repository, out);
            } finally {
                IO.flushQuiet(out);
                IO.closeQuietly(out);
            }

            if (this.success > 0) {
                plugin.sendNotification(ArtifactSearchNotification.NORMAL, "maven.search.delete.local.repository.lastUpdated.notify", "maven.search.delete.local.repository.lastUpdated.action", logfile, this.find, this.success);
            } else {
                plugin.sendNotification(ArtifactSearchNotification.NORMAL, "maven.search.delete.local.repository.lastUpdated.notify", this.find, this.success);
            }
        }
    }

    /**
     * 清空目录中的所有文件
     *
     * @param dir 目录
     * @param out 日志输出
     */
    public void execute(File dir, OutputStreamWriter out) {
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory()) {
                this.execute(file, out);
                continue;
            }

            if (file.exists() && file.isFile()) {
                String ext = FileUtils.getFilenameExt(file.getName());
                if ("lastUpdated".equalsIgnoreCase(ext)) {
                    this.find++;

                    boolean delete = file.delete();
                    if (delete) {
                        this.success++;
                    }

                    if (out != null) {
                        try {
                            out.write("Delete file " + file.getAbsolutePath() + " " + (delete ? "[success]" : "[fail]") + FileUtils.lineSeparator);
                        } catch (Throwable e) {
                            log.error(e.getLocalizedMessage(), e);
                        }
                    }
                }
            }
        }
    }
}
