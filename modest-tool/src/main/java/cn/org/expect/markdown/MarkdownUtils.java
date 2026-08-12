package cn.org.expect.markdown;

import java.io.File;

import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Logs;

/**
 * 提供 Markdown 文档解析与链接处理工具
 */
public class MarkdownUtils {

    public static void deleteTempfile(File file) {
        if (file.getName().startsWith("._") || file.getName().equals(".DS_Store")) {
            Logs.info("markdown.stdout.message001", file.getAbsolutePath(), (FileUtils.delete(file, 10, 100) ? "[success]" : "[fail]"));
        }
    }
}
