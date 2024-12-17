package cn.org.expect.database.export.inernal;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.database.export.ExtractReader;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.ioc.EasyBeanBuilder;
import cn.org.expect.ioc.EasyBeanInfo;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StringUtils;

/**
 * 数据输入流
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-02-18
 */
@EasyBean
public class ReaderBuilder implements EasyBeanBuilder<ExtractReader> {

    public ExtractReader getBean(EasyContext context, Object... args) throws Exception {
        ExtracterContext cxt = ArrayUtils.indexOf(args, ExtracterContext.class, 0);
        String source = cxt.getSource();
        if (StringUtils.startsWith(source, "select", 0, true, true)) {
            return context.createBean(DatabaseReader.class, "database", cxt);
        }

        Class<Object> cls = ClassUtils.forName(source, false, context.getClassLoader());
        if (cls != null) {
            return (ExtractReader) context.createBean(cls);
        }

        // 解析 http://xxx/xxx/xxx 格式
        String[] split = StringUtils.split(source, "://");
        if (split.length > 0) {
            EasyBeanInfo beanInfo = context.getBeanInfo(ExtractReader.class, split[0]);
            if (beanInfo != null) {
                return (ExtractReader) context.createBean(beanInfo.getType());
            }
        }

        throw new UnsupportedOperationException(source);
    }

}
