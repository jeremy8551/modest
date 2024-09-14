package cn.org.expect.database.export.inernal;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.database.export.ExtractReader;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.ioc.EasyetlBeanBuilder;
import cn.org.expect.ioc.EasyetlBean;
import cn.org.expect.ioc.EasyetlContext;
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
public class ReaderBuilder implements EasyetlBeanBuilder<ExtractReader> {

    public ExtractReader getBean(EasyetlContext context, Object... args) throws Exception {
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
            EasyetlBean beanInfo = context.getBeanInfo(ExtractReader.class, split[0]);
            if (beanInfo != null) {
                return (ExtractReader) context.createBean(beanInfo.getType());
            }
        }

        throw new UnsupportedOperationException(source);
    }

}
