package cn.org.expect.compress;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.FileUtils;

/**
 * 只对文件或目录下的所有子文件进行压缩: <br>
 * 1) 压缩后文件扩展名 gz, 如 JavaConfig.java == JavaConfig.gz <br>
 * 2) 成功压缩文件后自动删除原文件（目录不会删除）<br>
 * 3) 如果压缩目录, 自动遍历目录下的所有文件（包含子目录下的文件） <br>
 *
 * @author jeremy8551@gmail.com
 * @createtime 2017-11-30
 */
@EasyBean(value = "gz")
public class GzipCompress extends TarCompress {

    public GzipCompress() {
        super();
        this.setGzipCompress(true);
    }

    protected void addFile(File file, String dir, String charsetName, int level) throws IOException {
        if (!FileUtils.isFile(file)) {
            throw new IOException(file.getAbsolutePath() + " is not file!");
        }

        super.addFile(file, dir, charsetName, level);
        FileUtils.deleteFile(file);
    }
}
