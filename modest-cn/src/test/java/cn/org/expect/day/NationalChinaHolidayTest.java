package cn.org.expect.day;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.Dates;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class NationalChinaHolidayTest {

    @EasyBean
    private EasyContext context;

    @Test
    public void testIsChinaRestDay() {
        Assert.assertFalse(context.getBean(NationalHoliday.class, "zh_CN").isRestDay(Dates.parse("2019-08-30")));
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").isRestDay(Dates.parse("20191001")));
    }

    @Test
    public void testIsChinaWorkDay() {
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").isWorkDay(Dates.parse("2019-08-30")));
        Assert.assertFalse(context.getBean(NationalHoliday.class, "zh_CN").isWorkDay(Dates.parse("2019-08-31")));
    }

    @Test
    public void test1() {
        NationalChinaHoliday holiday = new NationalChinaHoliday();
        Assert.assertFalse(holiday.isRestDay(Dates.parse("2019-08-30")));
        Assert.assertTrue(holiday.isRestDay(Dates.parse("20191001")));
    }

    @Test
    public void test2() {
        NationalChinaHoliday holiday = new NationalChinaHoliday();
        Assert.assertTrue(holiday.isWorkDay(Dates.parse("2019-08-30")));
        Assert.assertFalse(holiday.isWorkDay(Dates.parse("2019-08-31")));
    }

    @Test
    public void testReloadLegalHolidays() {
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().size() > 0);
    }

    @Test
    public void testGetLegalRestDay() {
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().size() > 0);
    }

    @Test
    public void testGetLegalWorkDay() {
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getWorkDays().size() > 0);
    }

    @Test
    public void testIsChinaLegalRestDay() {
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().contains(Dates.parse("2019-10-01")));
        Assert.assertFalse(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().contains(Dates.parse("2019-08-31")));
        Assert.assertFalse(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().contains(null));
    }

    @Test
    public void testIsChinaLegalWorkDay() {
        Assert.assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getWorkDays().contains(Dates.parse("2017-02-04")));
        Assert.assertFalse(context.getBean(NationalHoliday.class, "zh_CN").getWorkDays().contains(Dates.parse("2017-02-05")));
    }
}
