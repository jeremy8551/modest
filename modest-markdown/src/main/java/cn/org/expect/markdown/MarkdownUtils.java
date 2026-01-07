package cn.org.expect.markdown;

import java.io.File;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;

public class MarkdownUtils {
    private final static Log log = LogFactory.getLog(MarkdownUtils.class);

    public static void deleteTempfile(File file) {
        if (file.getName().startsWith("._") || file.getName().equals(".DS_Store")) {
            log.info("markdown.stdout.message001", file.getAbsolutePath(), (FileUtils.delete(file, 10, 100) ? "[success]" : "[fail]"));
        }
    }
}
