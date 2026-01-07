package cn.org.expect.maven.plugin.lastupdated;

import java.io.File;

import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.util.FileUtils;

public class Lastupdated {

    private final MavenPluginLog log;

    public Lastupdated(MavenPluginLog log) {
        this.log = log;
    }

    /**
     * 清空目录中的所有文件
     *
     * @param dir 目录
     */
    public void execute(File dir) {
        log.info("Detected lastupdated in localRepository: " + dir);
        this.delete(dir);
    }

    private void delete(File dir) {
        File[] files = FileUtils.array(dir.listFiles());
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.isDirectory()) {
                this.delete(file);
                continue;
            }

            if (file.isFile() && file.getName().endsWith(".lastupdated")) {
                this.log.info("Delete file " + file.getAbsolutePath() + " " + (file.delete() ? "[success]" : "[fail]"));
            }
        }
    }
}
