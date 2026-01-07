package cn.org.expect.maven.plugin.rm;

import java.io.File;
import java.util.List;

import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class DeleteFile {

    private final MavenPluginLog log;

    /** 正则表达式数组 */
    private List<String> patterns;

    public DeleteFile(MavenPluginLog log) {
        this.log = log;
    }

    /**
     * 删除所有匹配的文件
     *
     * @param rootDir  目录
     * @param patterns 正则表达式数组
     */
    public void rmfile(File rootDir, List<String> patterns) {
        log.info("delete file in " + rootDir + ", Regex Pattern is " + StringUtils.toString(patterns));
        this.patterns = patterns;
        this.rmfile(rootDir);
    }

    /**
     * 删除所有匹配的目录
     *
     * @param rootDir  目录
     * @param patterns 正则表达式数组
     */
    public void rmdir(File rootDir, List<String> patterns) {
        log.info("delete directory in " + rootDir + ", Regex Pattern is " + StringUtils.toString(patterns));
        this.patterns = patterns;
        this.rmdir(rootDir);
    }

    /**
     * 删除所有匹配的文件
     *
     * @param fileOrDir 文件或目录
     */
    protected void rmfile(File fileOrDir) {
        if (!fileOrDir.exists()) {
            return;
        }

        // 如过文件是一个目录
        if (fileOrDir.isDirectory()) {
            File[] files = FileUtils.array(fileOrDir.listFiles());
            for (File file : files) {
                if (file.isDirectory()) {
                    this.rmfile(file);
                } else if (this.match(file)) {
                    boolean value = FileUtils.delete(file, 10, 100);
                    log.info("Delete file " + file.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
                }
            }
        }

        if (this.match(fileOrDir)) {
            boolean value = FileUtils.delete(fileOrDir, 10, 100);
            log.info("Delete " + (fileOrDir.isDirectory() ? "directory" : "file") + " " + fileOrDir.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
        }
    }

    /**
     * 删除所有匹配的文件
     *
     * @param dir 文件或目录
     */
    protected void rmdir(File dir) {
        if (!dir.exists()) {
            return;
        }

        // 如过文件是一个目录
        if (dir.isDirectory()) {
            if (this.match(dir)) {
                boolean value = FileUtils.delete(dir, 10, 100);
                log.info("Delete " + (dir.isDirectory() ? "directory" : "file") + " " + dir.getAbsolutePath() + " " + (value ? "[success]" : "[fail]"));
                if (value) {
                    return;
                }
            }

            File[] files = FileUtils.array(dir.listFiles());
            for (File file : files) {
                this.rmdir(file);
            }
        }
    }

    /**
     * 判断文件是否是一个临时文件
     *
     * @param file 文件
     * @return 返回true表示匹配文件，返回false表示不匹配文件
     */
    protected boolean match(File file) {
        for (String patten : this.patterns) {
            if (file.getName().matches(patten)) {
                return true;
            }
        }
        return false;
    }
}
