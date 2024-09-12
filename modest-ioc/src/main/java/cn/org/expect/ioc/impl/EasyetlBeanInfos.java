package cn.org.expect.ioc.impl;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.ioc.EasyetlContext;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.StrAsIntComparator;
import cn.org.expect.util.StrAsNumberComparator;
import cn.org.expect.util.StringComparator;

/**
 * 注册工具包、并发包、表达式包中的组件信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2024/1/16 14:41
 */
@EasyBean(singleton = true, lazy = false, description = "加载组件")
public class EasyetlBeanInfos {

    public EasyetlBeanInfos(EasyetlContext context) {
        EasyetlBeanDefineImpl bean1 = new EasyetlBeanDefineImpl(StrAsIntComparator.class);
        bean1.setName("int");
        bean1.setDescription("将字符串作为整数来比较");
        context.addBean(bean1);

        EasyetlBeanDefineImpl bean2 = new EasyetlBeanDefineImpl(StrAsNumberComparator.class);
        bean2.setName("number");
        bean2.setDescription("将字符串作为浮点数来比较");
        context.addBean(bean2);

        EasyetlBeanDefineImpl bean3 = new EasyetlBeanDefineImpl(StringComparator.class);
        bean3.setName("string");
        bean3.setDescription("字符串比较规则");
        context.addBean(bean3);

        // 加载线程池
        Class<Object> type1 = ClassUtils.forName("cn.org.expect.concurrent.ThreadSourceImpl");
        if (type1 != null) {
            EasyetlBeanDefineImpl bean = new EasyetlBeanDefineImpl(type1);
            bean.setSingleton(true);
            bean.setLazy(true);
            context.addBean(bean);
        }

        Class<Object> type2 = ClassUtils.forName("cn.org.expect.expression.AnalysisImpl");
        if (type2 != null) {
            EasyetlBeanDefineImpl bean = new EasyetlBeanDefineImpl(type2);
            context.addBean(bean);
        }
    }

}
