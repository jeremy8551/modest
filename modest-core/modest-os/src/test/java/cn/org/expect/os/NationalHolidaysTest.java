package cn.org.expect.os;

import java.io.IOException;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.cn.NationalHoliday;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.impl.EasyBeanDefineImpl;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.util.Dates;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
public class NationalHolidaysTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        NationalHoliday bean = this.context.getBean(NationalHoliday.class, "zh_CN");
        Assert.assertNotNull(bean);
        Assert.assertFalse(bean.getRestDays().contains(Dates.parse("2021-12-24")));
        Assert.assertFalse(bean.getWorkDays().contains(Dates.parse("2021-12-24")));

        EasyBeanDefineImpl beanInfo = new EasyBeanDefineImpl(USHolidays.class);
        beanInfo.setName("zh_cn");
        beanInfo.setLazy(false);

        Assert.assertTrue(this.context.addBean(beanInfo));
        Assert.assertTrue(bean.getWorkDays().contains(Dates.parse("2021-12-24")));
        Assert.assertFalse(bean.getRestDays().contains(Dates.parse("2021-12-24")));
    }
}
