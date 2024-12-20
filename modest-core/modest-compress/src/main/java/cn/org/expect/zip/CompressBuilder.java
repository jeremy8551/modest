package cn.org.expect.zip;

import java.io.File;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 压缩工具工厂
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-02-09
 */
@EasyBean
public class CompressBuilder implements EasyBeanBuilder<Compress> {

    public Compress getBean(EasyContext context, Object... args) throws Exception {
        String suffix = null;

        File file = ArrayUtils.indexOf(args, File.class, 0);
        if (file != null) {
            suffix = FileUtils.getFilenameExt(file.getName());
        }

        if (StringUtils.isBlank(suffix)) {
            suffix = ArrayUtils.indexOf(args, String.class, 0);
        }

        // 设置默认值
        if (StringUtils.isBlank(suffix)) {
            suffix = "zip";
        }

        EasyBeanInfo beanInfo = Ensure.notNull(context.getBeanInfo(Compress.class, suffix));
        return context.createBean(beanInfo.getType());
    }

}
