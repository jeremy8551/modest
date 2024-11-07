package cn.org.expect.intellijidea.plugin.maven.action;

import java.io.File;
import java.io.OutputStreamWriter;

import cn.org.expect.intellijidea.plugin.maven.MavenFinder;
import cn.org.expect.intellijidea.plugin.maven.MavenFinderContext;
import cn.org.expect.intellijidea.plugin.maven.local.LocalRepositoryConfig;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.MessageFormatter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * 删除 Maven 本地仓库中的 *.lastUpdated 文件
 */
public class MavenFinderDeleteLastUpdated extends AnAction {
    private static final Logger log = Logger.getInstance(MavenFinderDeleteLastUpdated.class);

    /** 找到的文件数量 */
    private int find;

    /** 删除的文件数量 */
    private int success;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        this.find = 0;
        this.success = 0;

        MavenFinderContext context = new MavenFinderContext(event);
        MavenFinder mavenFinder = new MavenFinder(context);
        File repository = LocalRepositoryConfig.getInstance(event).getRepository();
        if (repository == null) {
            mavenFinder.sendErrorNotification("Cannot find Maven local repository!");
            return;
        }

        synchronized (MavenFinderDeleteLastUpdated.class) {
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

            String message = new MessageFormatter("Found {}, Delete {}").fill(this.find, this.success);
            if (this.success > 0) {
                mavenFinder.sendNotification(message, "View Log File", logfile);
            } else {
                mavenFinder.sendNotification(message);
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
