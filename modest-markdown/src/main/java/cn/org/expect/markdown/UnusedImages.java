package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 删除 Markdown 文档中未用到的图片和临时文件（._xxx.md）
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/6/1
 */
public class UnusedImages {
    private final static Log log = LogFactory.getLog(RenameMarkdown.class);

    public void execute(File fileOrDir) throws IOException {
        if (fileOrDir.isDirectory()) {
            File[] files = FileUtils.array(fileOrDir.listFiles());
            for (File file : files) {
                this.execute(file);
            }
            return;
        }

        this.delFile(fileOrDir);
    }

    private void delFile(File file) throws IOException {
        MarkdownUtils.deleteTempfile(file);

        // 读取文件内容
        String ext = FileUtils.getFilenameExt(file.getName());
        if (StringUtils.inArrayIgnoreCase(ext, "md")) {
            String content = FileUtils.readline(file, Settings.getFileEncoding(), 0);
            if (content == null) {
                return;
            }

            File imgDir = new File(FileUtils.changeFilenameExt(file.getAbsolutePath(), "assets"));
            if (imgDir.exists() && imgDir.isDirectory()) {
                File[] files = imgDir.listFiles();
                if (files != null) {
                    for (File imgfile : files) {
                        String name = imgfile.getName();
                        String hexName = StringUtils.encodeJvmUtf8HexString(name);
                        String hexName1 = StringUtils.replaceAll(hexName, " ", "%20");
                        if (!content.contains(name) && StringUtils.indexOf(content, hexName, 0, true) == -1 && StringUtils.indexOf(content, hexName1, 0, true) == -1) {
                            log.info("markdown.stdout.message008", file.getAbsolutePath(), imgfile.getAbsolutePath(), (FileUtils.deleteFile(imgfile) ? "[success]" : "[fail]"));
                            continue;
                        }
                    }
                }
            }
        }
    }
}
