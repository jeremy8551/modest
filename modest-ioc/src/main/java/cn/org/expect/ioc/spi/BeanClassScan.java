package cn.org.expect.ioc.spi;

import java.util.List;

import cn.org.expect.ioc.EasyBeanAnnotation;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanRepository;
import cn.org.expect.ioc.EasyClassScan;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import com.google.auto.service.AutoService;

/**
 * 类扫描规则 <br>
 * 扫描类路径中使用组件注解（如: @EasyBean, @Bean, @Resource）标记的类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-08
 */
@AutoService(EasyClassScan.class)
public class BeanClassScan implements EasyClassScan, EasyContextAware {

    protected List<EasyBeanAnnotation> list;

    /**
     * 初始化，
     */
    public BeanClassScan() {
    }

    public void setContext(EasyContext context) {
        this.list = context.loadBean(EasyBeanAnnotation.class);
    }

    public boolean load(EasyBeanRepository repository, Class<?> type) {
        for (int i = 0; i < this.list.size(); i++) {
            EasyBeanAnnotation annotation = this.list.get(i);
            if (annotation.isPresent(type)) {
                EasyBeanEntry entry = annotation.getBean(type);
                if (entry != null && repository.addBean(entry)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        return obj != null && BeanClassScan.class.equals(obj.getClass());
    }
}
