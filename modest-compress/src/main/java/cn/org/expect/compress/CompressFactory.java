package cn.org.expect.compress;

import java.io.File;
import java.io.UnsupportedEncodingException;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 压缩工具工厂
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-09
 */
@EasyBean
public class CompressFactory implements EasyBeanFactory<Compress> {

    public Compress build(EasyContext context, Object... args) throws Exception {
        String ext = null;

        File file = ArrayUtils.indexOf(args, File.class, 0);
        if (file != null) {
            String suffix = FileUtils.getFilenameSuffix(file.getName());
            if (suffix.toLowerCase().endsWith(".tar.gz")) {
                TarCompress tarCompress = new TarCompress();
                tarCompress.setGzipCompress(true);
                return init(tarCompress);
            }

            ext = FileUtils.getFilenameExt(file.getName());
        }

        if (StringUtils.isBlank(ext)) {
            ext = StringUtils.trimBlank(ArrayUtils.indexOf(args, CharSequence.class, 0));

            if (ext != null && ext.equalsIgnoreCase("tar.gz")) {
                TarCompress compress = new TarCompress();
                compress.setGzipCompress(true);
                return init(compress);
            }
        }

        // 设置默认值
        if (StringUtils.isBlank(ext)) {
            return init(new ZipCompress());
        }

        EasyBeanEntry entry = Ensure.notNull(context.getBeanEntry(Compress.class, ext));
        Compress compress = context.newInstance(entry.getType());
        return init(compress);
    }

    private static Compress init(Compress compress) throws UnsupportedEncodingException {
        if (compress instanceof ZipCompress) {
            ZipCompress zipCompress = (ZipCompress) compress;
            zipCompress.setMobileMode(true);
            zipCompress.setRecursion(true);
            zipCompress.setLogWriter(new CompressLogWriter());
        }

        if (compress instanceof RarCompress) {
            RarCompress rarCompress = (RarCompress) compress;
            rarCompress.setVerbose(true);
            rarCompress.setLogWriter(new CompressLogWriter());
        }

        if (compress instanceof TarCompress) {
            TarCompress tarCompress = (TarCompress) compress;
            tarCompress.setVerbose(true);
            tarCompress.setLogWriter(new CompressLogWriter());
        }

        if (compress instanceof GzipCompress) {
            GzipCompress gzipCompress = (GzipCompress) compress;
            gzipCompress.setVerbose(true);
            gzipCompress.setLogWriter(new CompressLogWriter());
        }

        return compress;
    }
}
