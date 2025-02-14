package cn.org.expect.database.export.inernal;

import cn.org.expect.database.export.ExtractReader;
import cn.org.expect.database.export.ExtracterContext;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

/**
 * 数据输入流
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-18
 */
@EasyBean
public class ReaderFactory implements EasyBeanFactory<ExtractReader> {

    public ExtractReader build(EasyContext context, Object... args) throws Exception {
        ExtracterContext extractContext = ArrayUtils.indexOf(args, ExtracterContext.class, 0);
        assert extractContext != null;
        String source = extractContext.getSource();
        if (StringUtils.startsWith(source, "select", 0, true, true)) {
            return new DatabaseReader(context, extractContext);
        }

        Class<Object> cls = context.forName(source);
        if (cls != null) {
            return (ExtractReader) context.newInstance(cls);
        }

        // 解析 http://xxx/xxx/xxx 格式
        String[] array = StringUtils.split(source, "://");
        if (array.length > 0) {
            EasyBeanEntry entry = context.getBeanEntry(ExtractReader.class, array[0]);
            if (entry != null) {
                return (ExtractReader) context.newInstance(entry.getType());
            }
        }

        throw new UnsupportedOperationException(source);
    }
}
