package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.org.expect.ModestException;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;

/**
 * 统一图片名，删除未用图片
 */
public class ExecMavenPlugin {
    protected static MavenPluginLog log;

    public static void main(String[] args) throws IOException, ModestException {
        execute("学习笔记");
        execute("个人设备");
        execute("安装程序");
    }

    /**
     * 查找 markdown 文件的根目录
     *
     * @return 返回null表示不存在
     */
    private static File findDir(String name) {
        File userDir = new File(Settings.getUserHome(), "/Library/CloudStorage"); // 私有云
        if (FileUtils.isDirectory(userDir)) {
            List<File> dirs = FileUtils.find(userDir, name); // Markdown 文件目录
            if (!dirs.isEmpty()) {
                File mdDir = dirs.get(0);
                log.info("scan " + mdDir.getAbsolutePath());
                return mdDir;
            }
        }
        return null;
    }

    private static void execute(String leannoteName) throws IOException, ModestException {
        File dir = findDir(leannoteName);
        if (dir != null) {
            new RenameImages().execute(dir);
            new UnusedImages().execute(dir);
        }
    }

    private static void rename() throws IOException {
        File dir = findDir("学习笔记");
        if (dir != null) {
            File markdown = new File(dir, "2.编程语言/9.Python.md");
            String newFilename = "20.Python.md";

            if (dir.exists() && dir.isDirectory() && markdown.exists()) {
                new RenameMarkdown().rename(markdown.getAbsolutePath(), newFilename);
            }
        }
    }
}
