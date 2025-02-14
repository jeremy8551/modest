package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MarkdownTest {

    @DisplayName("测试：统一图片名，删除未用图片")
    @Test
    public void test0() throws IOException {
        File userDir = Settings.getUserDir();
        File dir = FileUtils.findUpward(userDir, "learning-notes");
        if (dir != null) {
            new RenameImages().execute(dir);
            new UnusedImages().execute(dir);
        }
    }

    @DisplayName("测试重命名markdown文档")
    @Disabled
    public void test() throws IOException {
        File userDir = Settings.getUserDir();
        File dir = FileUtils.findUpward(userDir, "learning-notes");
        if (dir != null) {
            File markdown = new File(dir, "2.编程语言/9.Python.md");
            String newFilename = "20.Python.md";

            if (dir.exists() && dir.isDirectory() && markdown.exists()) {
                new RenameMarkdown().rename(markdown.getAbsolutePath(), newFilename);
            }
        }
    }
}
