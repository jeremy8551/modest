package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import org.junit.Ignore;
import org.junit.Test;

public class MarkdownTest {
    private final static Log log = LogFactory.getLog(MarkdownTest.class);

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
                log.info("扫描 " + mdDir.getAbsolutePath());
                return mdDir;
            }
        }
        return null;
    }

    /**
     * 统一图片名，删除未用图片
     *
     * @throws IOException 发生错误
     */
    @Test
    public void test0() throws IOException {
        this.execute("学习笔记");
        this.execute("个人设备");
    }

    private void execute(String leannoteName) throws IOException {
        File dir = findDir(leannoteName);
        if (dir != null) {
            new RenameImages().execute(dir);
            new UnusedImages().execute(dir);
        }
    }

    /**
     * 重命名markdown文档
     *
     * @throws IOException 发生错误
     */
    @Ignore
    public void test1() throws IOException {
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
