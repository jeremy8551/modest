package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import org.junit.Ignore;
import org.junit.Test;

public class MarkdownTest {

    @Test
    public void test0() throws IOException {
        File userDir = Settings.getUserDir();
        File dir = new File(FileUtils.getParent(userDir.getAbsolutePath()), "learning-notes");
        System.out.println(dir.getAbsolutePath() + " " + dir.exists());

        if (dir.exists() && dir.isDirectory()) {
            RenameImages renameImages = new RenameImages();
            renameImages.execute(dir);

            UnusedImages unusedImages = new UnusedImages();
            unusedImages.execute(dir);
        }
    }

    @Ignore
    public void test1() throws IOException {
        File userDir = Settings.getUserDir();
        File dir = new File(FileUtils.getParent(userDir.getAbsolutePath()), "learning-notes");
        System.out.println(dir.getAbsolutePath() + " " + dir.exists());

        if (dir.exists() && dir.isDirectory()) {
            File markdown = new File(dir, "2.编程语言/9.Python.md");
            new RenameMarkdown().rename(markdown.getAbsolutePath(), "20.Python.md");
        }
    }
}

