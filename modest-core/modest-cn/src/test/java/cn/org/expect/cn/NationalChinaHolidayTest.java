package cn.org.expect.cn;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.util.Dates;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NationalChinaHolidayTest {

    @Test
    public void testIsChinaRestDay() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertFalse(context.getBean(NationalHoliday.class, "zh_CN").isRestDay(Dates.parse("2019-08-30")));
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").isRestDay(Dates.parse("20191001")));
    }

    @Test
    public void testIsChinaWorkDay() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").isWorkDay(Dates.parse("2019-08-30")));
        assertFalse(context.getBean(NationalHoliday.class, "zh_CN").isWorkDay(Dates.parse("2019-08-31")));
    }

    @Test
    public void test1() {
        NationalChinaHoliday context = new NationalChinaHoliday();
        assertFalse(context.isRestDay(Dates.parse("2019-08-30")));
        assertTrue(context.isRestDay(Dates.parse("20191001")));
    }

    @Test
    public void test2() {
        NationalChinaHoliday context = new NationalChinaHoliday();
        assertTrue(context.isWorkDay(Dates.parse("2019-08-30")));
        assertFalse(context.isWorkDay(Dates.parse("2019-08-31")));
    }

    @Test
    public void testReloadLegalHolidays() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().size() > 0);
    }

    @Test
    public void testGetLegalRestDay() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().size() > 0);
    }

    @Test
    public void testGetLegalWorkDay() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getWorkDays().size() > 0);
    }

    @Test
    public void testIsChinaLegalRestDay() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().contains(Dates.parse("2019-10-01")));
        assertFalse(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().contains(Dates.parse("2019-08-31")));
        assertFalse(context.getBean(NationalHoliday.class, "zh_CN").getRestDays().contains(null));
    }

    @Test
    public void testIsChinaLegalWorkDay() {
        DefaultEasyContext context = new DefaultEasyContext();
        assertTrue(context.getBean(NationalHoliday.class, "zh_CN").getWorkDays().contains(Dates.parse("2017-02-04")));
        assertFalse(context.getBean(NationalHoliday.class, "zh_CN").getWorkDays().contains(Dates.parse("2017-02-05")));
    }

}
