package cn.org.expect.markdown;

import java.io.File;
import java.io.IOException;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 重命名 Markdown 文档中的图片
 *
 * @author jeremy8551@gmail.com
 * @createtime 2024/6/1
 */
public class RenameImages {
    private final static Log log = LogFactory.getLog(RenameMarkdown.class);

    public static String NEW_IMAGE_NAME_PREFIX = "markdown_local_image_";

    public void execute(File fileOrDir) throws IOException {
        if (fileOrDir.isDirectory()) {
            File[] files = FileUtils.array(fileOrDir.listFiles());
            for (File file : files) {
                this.execute(file);
            }
            return;
        }

        this.rename(fileOrDir);
    }

    private void rename(File file) throws IOException {
        Utils.deleteTempfile(file);

        // 读取文件内容
        String extName = FileUtils.getFilenameExt(file.getName());
        if (StringUtils.inArrayIgnoreCase(extName, "md")) {
            String charsetName = Settings.getFileEncoding();
            String content = FileUtils.readline(file, charsetName, 0);
            if (content == null) {
                return;
            }

            File imgDir = new File(FileUtils.changeFilenameExt(file.getAbsolutePath(), "assets"));
            if (imgDir.exists() && imgDir.isDirectory()) {
                File[] files = FileUtils.array(imgDir.listFiles());
                int number = this.getFileNumber(files) + 1;
                if (number > 0) {
                    log.info("目录 " + imgDir.getName() + " 起始图片序号是: " + number);
                }

                for (File imgfile : files) {
                    if (this.isImageName(imgfile)) {
                        continue;
                    }

                    if (StringUtils.isBlank(FileUtils.getFilenameSuffix(imgfile.getName()))) {
                        log.warn("图片文件缺失扩展名 " + imgfile.getAbsolutePath());
                    }

                    String oldImageName = "/" + imgfile.getName();
                    if (content.contains(oldImageName)) {
                        String newImageName = FileUtils.changeFilename(oldImageName, RenameImages.NEW_IMAGE_NAME_PREFIX + number++);
                        content = StringUtils.replaceAll(content, oldImageName, newImageName);

                        File newImagefile = new File(imgfile.getParentFile(), newImageName);
                        if (newImagefile.exists()) {
                            throw new IOException("文件 " + newImagefile.getAbsolutePath() + " 已存在!");
                        }

                        log.info("将图片 " + imgfile.getAbsolutePath() + " 重命名为 " + newImagefile.getName() + " " + (FileUtils.rename(imgfile, newImagefile, null) ? "[success]" : "[fail]"));
                        continue;
                    }
                }
            }

            FileUtils.write(file, charsetName, false, content);
        }
    }

    /**
     * 判断文件名是否是标准的格式
     *
     * @param file 文件
     * @return 返回true表示图片名合法 false表示图片名非法
     */
    public boolean isImageName(File file) {
        if (file.getName().startsWith(RenameImages.NEW_IMAGE_NAME_PREFIX)) {
            String filename = FileUtils.getFilenameNoSuffix(file.getName());
            String numberStr = filename.substring(RenameImages.NEW_IMAGE_NAME_PREFIX.length());
            return StringUtils.isNumber(numberStr);
        }
        return false;
    }

    /**
     * 计算图片文件名中序号的最大值
     *
     * @param files 图片数组
     * @return 序号最大值
     */
    public int getFileNumber(File[] files) {
        int no = 0;
        for (File file : files) {
            String filename = FileUtils.getFilenameNoSuffix(file.getName());
            if (filename.startsWith(RenameImages.NEW_IMAGE_NAME_PREFIX)) {
                String numberStr = filename.substring(RenameImages.NEW_IMAGE_NAME_PREFIX.length());
                if (StringUtils.isNumber(numberStr)) {
                    int number = Integer.parseInt(numberStr);
                    if (number > no) {
                        no = number;
                    }
                }
            }
        }
        return no;
    }
}
