package cn.org.expect.util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class DatesTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testsleep() {
        long start = System.currentTimeMillis();
        try {
            Dates.sleep(1000, TimeUnit.MILLISECONDS);
            long wait = (System.currentTimeMillis() - start) / 1000;
            Assert.assertEquals(1L, wait);
        } catch (Throwable e) {
            Logs.error(e.getLocalizedMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void testTimeUnit() {
        Assert.assertEquals("0", this.remove(Dates.format(0, TimeUnit.SECONDS, true)));
        Assert.assertEquals("11", this.remove(Dates.format(61, TimeUnit.SECONDS, true))); // (61) == 1 分 1 秒
        Assert.assertEquals("11", this.remove(Dates.format(3660, TimeUnit.SECONDS, true))); // (3660) == 1 小时 1 分
        Assert.assertEquals("2774640", this.remove(Dates.format(1000000, TimeUnit.SECONDS, true))); // (1000000) == 277 小时 46 分 40 秒
    }

    private String remove(CharSequence cs) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (StringUtils.isNumber(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    @Test
    public void testcalcDate() {
        Date date = Dates.parse("2020-01-01");
        Assert.assertEquals(Dates.parse("2020-01-02"), Dates.calcDay(date, Calendar.DAY_OF_MONTH, 1));
        Assert.assertEquals(Dates.parse("2020-02-01"), Dates.calcDay(date, Calendar.MONTH, 1));
        Assert.assertEquals(Dates.parse("2021-01-01"), Dates.calcDay(date, Calendar.YEAR, 1));
        Assert.assertEquals(Dates.parse("2020-01-01 01:00:00"), Dates.calcDay(date, Calendar.HOUR, 1));
        Assert.assertEquals(Dates.parse("2020-01-01 00:01:00"), Dates.calcDay(date, Calendar.MINUTE, 1));
        Assert.assertEquals(Dates.parse("2020-01-01 00:00:01"), Dates.calcDay(date, Calendar.SECOND, 1));
        Assert.assertEquals(Dates.parse("2020-01-01 00:00:00:001"), Dates.calcDay(date, Calendar.MILLISECOND, 1));
    }

    @Test
    public void testgetRandom() {
        for (int i = 1; i < 1000; i++) {
            Date s = Dates.random(Dates.parse("1960-01-01"), new Date());
            Random r = new Random();
            Date e = Dates.calcDay(s, r.nextInt(1000));

            Date date = Dates.random(s, e);
            Assert.assertTrue(Dates.between(date, s, e));
        }
    }

    @Test
    public void test123() {
        Timestamp t1 = new Timestamp(new Date().getTime());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Logs.error(e.getLocalizedMessage(), e);
        }

        Timestamp t2 = new Timestamp(new Date().getTime());
        Assert.assertEquals(0, Dates.compareIgnoreTime(t1, t2));
    }

    @Test
    public void testCurDate06() {
        Assert.assertEquals(Dates.currentTimeStamp(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @Test
    public void testFormatStringArray() {
        Date[] array = Dates.parse("20190801", "20190802", "20190831");
        Assert.assertEquals(array[0], Dates.parse("20190801"));
        Assert.assertEquals(array[1], Dates.parse("20190802"));
        Assert.assertEquals(array[2], Dates.parse("20190831"));

        // 因为不同操作系统的时区可能不同，导致java打印日期与时间时时区也不同，所以要先得到时区信息
        Date date = new Date();
        String str = date.toString();
        String[] array1 = str.split("\\s+");
        String zone = array1[array1.length - 2];

        Assert.assertEquals("Date[Sun Jan 01 00:00:00 " + zone + " 2017, Wed Feb 01 00:00:00 " + zone + " 2017, null]", StringUtils.toString(Dates.parse("2017-01-01", "2017-02-01", null)));
    }

    @Test
    public void testFormatObject() throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String str = "2017-01-01";
        Date date = f.parse(str);

        Date currentDate = new Date();

        Assert.assertEquals("20171130", Dates.format08(Dates.parse(" 20171130 ")));
        Assert.assertEquals("20170228", Dates.format08(Dates.parse(" 20170228 ")));
        Assert.assertEquals("20170130", Dates.format08(Dates.parse(" 2017-01-30 ")));
        Assert.assertEquals("20171130", Dates.format08(Dates.parse(" 2017-11-30 ")));
        Assert.assertEquals("20171130", Dates.format08(Dates.parse(" 二零一七年 十一月三十号 ")));
        Assert.assertEquals("20171130", Dates.format08(Dates.parse(" 二零一七年 十一月三十日 ")));
        Assert.assertEquals("20171230", Dates.format08(Dates.parse(" 二零一七年 十二月三十号 ")));
        Assert.assertEquals("20171231", Dates.format08(Dates.parse(" 二零一七年 十二月三十一号 ")));
        Assert.assertEquals("20171031", Dates.format08(Dates.parse(" 二零一七年 十月三十一号 ")));
        Assert.assertEquals("20171011", Dates.format08(Dates.parse(" 二零一七年 十月十一号 ")));
        Assert.assertEquals("20170911", Dates.format08(Dates.parse(" 二零一七年 九月十一号 ")));
        Assert.assertEquals("20171001", Dates.format08(Dates.parse(" 二零一七年十月一号 ")));
        Assert.assertEquals("20171010", Dates.format08(Dates.parse(" 二零一七年十月十号 ")));
        Assert.assertEquals("20170101", Dates.format08(Dates.parse(" 二零一七年一月一号 ")));
        Assert.assertEquals("20170110", Dates.format08(Dates.parse(" 二零一七年一月十号 ")));
        Assert.assertEquals("20170112", Dates.format08(Dates.parse(" 二零一七年一月十二号 ")));
        Assert.assertEquals("20170120", Dates.format08(Dates.parse(" 二零一七年一月二十号 ")));
        Assert.assertEquals("20170121", Dates.format08(Dates.parse(" 二零一七年一月二十一号 ")));
        Assert.assertEquals("20170130", Dates.format08(Dates.parse(" 二零一七年一月三十号 ")));
        Assert.assertEquals("20170131", Dates.format08(Dates.parse(" 二零一七年一月三十一号 ")));
        Assert.assertEquals("20171031", Dates.format08(Dates.parse(" 二零一七年十月三十一号 ")));
        Assert.assertEquals("20171231", Dates.format08(Dates.parse(" 二零一七年十二月三十一号 ")));
        Assert.assertEquals("20171210", Dates.format08(Dates.parse(" 二零一七年十二月十号 ")));
        Assert.assertEquals("20171210", Dates.format08(Dates.parse(" 二零一七年十二月十 ")));

        Assert.assertEquals("20171210", Dates.format08(Dates.parse(" 二零一七年十二月十日")));
        Assert.assertEquals("20170102", Dates.format08(Dates.parse(" 二零一七年一月二日")));

        Assert.assertEquals("20171213", Dates.format08(Dates.parse(" 二零一七年十二月十三 ")));
        Assert.assertEquals("20171223", Dates.format08(Dates.parse(" 二零一七年十二月二十三 ")));
        Assert.assertEquals("19981223", Dates.format08(Dates.parse(" 1九九八年十二月二十三 ")));
        Assert.assertEquals("19981223", Dates.format08(Dates.parse(" 1九九八 年 十二 月 二十三 ")));
        Assert.assertEquals("19981011", Dates.format08(Dates.parse(" 1998年10月11号 ")));
        Assert.assertEquals("19981011", Dates.format08(Dates.parse(" 1998年10月11号13点13分13秒120毫秒 ")));
        Assert.assertEquals("19981011", Dates.format08(Dates.parse(" Sun Oct 11 00:00:00 GMT+08:00 1998")));

        try {
            Dates.format08(Dates.parse(" Thu Oct 11 00:00:00 GMT+08:00 1998"));
            fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        Assert.assertEquals("20170101", Dates.format08(Dates.parse(date)));
        Assert.assertEquals("20170203", Dates.format08(Dates.parse("2017-2-03")));
        Assert.assertEquals("20170203", Dates.format08(Dates.parse("2017-2-3")));
        Assert.assertEquals("20171203", Dates.format08(Dates.parse("2017-12-3")));
        Assert.assertEquals("20171231", Dates.format08(Dates.parse("2017-12-31")));
        Assert.assertEquals("20171202", Dates.format08(Dates.parse("2017-12-2")));
        Assert.assertEquals("20171202", Dates.format08(Dates.parse("2017.12.2")));
        Assert.assertEquals("20170203", Dates.format08(Dates.parse("2017.2.3")));
        Assert.assertEquals("20170101", Dates.format08(Dates.parse(new java.sql.Date(date.getTime()))));
        Assert.assertEquals("2019-04-29 17:07:00:000", Dates.format21(Dates.parse("2019/04/29 17:07")));

        Assert.assertEquals(Dates.format19(currentDate), Dates.format19(Dates.parse(currentDate.toString())));
        Assert.assertEquals("2017-12-31 13:45:00", Dates.format19(Dates.parse("2017-12-31 13:45")));
        Assert.assertEquals("2017-12-31 13:00:00", Dates.format19(Dates.parse("2017-12-31 13")));
        Assert.assertEquals("2017-12-31 13:45:32", Dates.format19(Dates.parse("2017-12-31 13:45:32")));
        Assert.assertEquals("2017-12-31 13:45:32", Dates.format19(Dates.parse("2017-12-31 13.45.32")));
        Assert.assertEquals("2017-12-31 13:45:32", Dates.format19(Dates.parse("2017/12/31 13.45.32")));
        Assert.assertEquals("2017-12-31 13:45:59", Dates.format19(Dates.parse("2017/12/31 13.45.59")));
        Assert.assertEquals("2017-12-31 23:59:59", Dates.format19(Dates.parse("2017/12/31 23.59.59")));
        Assert.assertEquals("2017-12-31 23:59:59:123", Dates.format21(Dates.parse("2017/12/31 23.59.59:123")));
        Assert.assertEquals("2017-12-31 23:59:59:999", Dates.format21(Dates.parse("2017/12/31 23.59.59:999")));
        Assert.assertEquals("2017-12-31 00:00:00", Dates.format19(Dates.parse("2017/12/31 00.00.00")));
        Assert.assertEquals("20171223", Dates.format08(Dates.parse(" 二零一七 年 十 二 月 二 十 三 ")));
        Assert.assertEquals("20170102", Dates.format08(Dates.parse("01/02/2017")));
        Assert.assertEquals("20170202", Dates.format08(Dates.parse("02/02/2017")));
//		assertTrue(Dates.format07(Dates.format("31/05/2017")).equals("20170531"));
        Assert.assertEquals("20170531", Dates.format08(Dates.parse("05/31/2017")));

        Assert.assertEquals("2019-01-23 01:00:00:000", Dates.format21(Dates.parse("2019-01-23 01")));
        Assert.assertEquals("2019-01-23 01:23:00:000", Dates.format21(Dates.parse("2019-01-23 01:23")));
        Assert.assertEquals("2019-01-23 01:23:45:000", Dates.format21(Dates.parse("2019-01-23 01:23:45")));
        Assert.assertEquals("2019-01-23 01:23:45:067", Dates.format21(Dates.parse("2019-01-23 01:23:45:67")));
        Assert.assertEquals("2019-01-23 01:23:45:167", Dates.format21(Dates.parse("2019-01-23 01:23:45:167")));

        Assert.assertEquals("2019-01-23 01:00:00:000", Dates.format21(Dates.parse("2019012301")));
        Assert.assertEquals("2019-01-23 01:23:00:000", Dates.format21(Dates.parse("201901230123")));
        Assert.assertEquals("2019-04-29 17:07:00:000", Dates.format21(Dates.parse("201904291707")));
        Assert.assertEquals("2019-04-29 17:07:08:000", Dates.format21(Dates.parse("20190429170708")));
        Assert.assertEquals("2019-04-29 17:07:08:001", Dates.format21(Dates.parse("201904291707081")));
        Assert.assertEquals("2019-04-29 17:07:08:012", Dates.format21(Dates.parse("2019042917070812")));
        Assert.assertEquals("2019-04-29 17:07:08:123", Dates.format21(Dates.parse("20190429170708123")));
        Assert.assertEquals("2019-04-29 17:07:08:012", Dates.format21(Dates.parse("20190429170708012")));
        Assert.assertEquals("2019-04-29 17:07:08:002", Dates.format21(Dates.parse("20190429170708002")));

        Assert.assertEquals("2019-03-21 06:14:26", Dates.format19(Dates.parse("Thu Mar 21 06:14:26 UTC 2019")));
        Assert.assertEquals("2019-03-21 06:14:26:999", Dates.format21(Dates.parse("Thu Mar 21 06:14:26:999 UTC 2019")));
        Assert.assertEquals("2019-03-21 06:13:57", Dates.format19(Dates.parse("2019年 03月 21日 星期四 06:13:57 UTC")));
        Assert.assertEquals("2019-03-21 06:13:57", Dates.format19(Dates.parse("2019年 03月 21日 星期四 06:13:57 UTC+08:00")));

        Assert.assertEquals("2017-12-31 08:38:00", Dates.format19(Dates.parse(" 31 december 2017 at   08:38")));
        Assert.assertEquals("2017-12-31 08:38:00", Dates.format19(Dates.parse("31 december 2017 at 08:38")));
        Assert.assertEquals("2017-12-31 08:38:12", Dates.format19(Dates.parse("31 december 2017 at 08:38:12")));
        Assert.assertEquals("2017-12-31 08:38:12:099", Dates.format21(Dates.parse("31 december 2017 at 08:38:12:99")));
        Assert.assertEquals("2017-12-31 08:38:00", Dates.format19(Dates.parse("31  december 2017 at 08:38 ")));
        Assert.assertEquals("2017-12-31 00:00:00", Dates.format19(Dates.parse("31 dec 2017")));

        Assert.assertEquals(Dates.format19(Dates.parse(currentDate.toString())), Dates.format19(currentDate));
        Assert.assertEquals("2019-04-29 00:00:00", Dates.format19(Dates.parse("2019年4月29号")));
        Assert.assertEquals("2019-04-29 00:00:00", Dates.format19(Dates.parse("2019年 4月29号")));
        Assert.assertEquals("2019-04-29 00:00:00", Dates.format19(Dates.parse("2019年 4月   29号")));

        date = Dates.parse("2014-04-03");
        Assert.assertEquals(2014, Dates.getYear(date));
        Assert.assertEquals(4, Dates.getMonth(date));

        Assert.assertEquals(1, Dates.getMonth(Dates.parse("20190101")));
        Assert.assertEquals(2, Dates.getMonth(Dates.parse("20190201")));
        Assert.assertEquals(3, Dates.getMonth(Dates.parse("20190301")));
        Assert.assertEquals(4, Dates.getMonth(Dates.parse("20190401")));
        Assert.assertEquals(5, Dates.getMonth(Dates.parse("20190501")));
        Assert.assertEquals(6, Dates.getMonth(Dates.parse("20190601")));
        Assert.assertEquals(7, Dates.getMonth(Dates.parse("20190701")));
        Assert.assertEquals(8, Dates.getMonth(Dates.parse("20190801")));
        Assert.assertEquals(9, Dates.getMonth(Dates.parse("20190901")));
        Assert.assertEquals(10, Dates.getMonth(Dates.parse("20191001")));
        Assert.assertEquals(11, Dates.getMonth(Dates.parse("20191101")));
        Assert.assertEquals(12, Dates.getMonth(Dates.parse("20191201")));
        try {
            Dates.getMonth(Dates.parse("20191301"));
            throw new RuntimeException();
        } catch (Exception ignored) {
        }
        Assert.assertEquals(3, Dates.getDayOfMonth(date));

        Assert.assertEquals(4, Dates.getDayOfWeek(date));
        Assert.assertEquals(1, Dates.getDayOfWeek(Dates.parse("20190429")));
        Assert.assertEquals(2, Dates.getDayOfWeek(Dates.parse("20190430")));
        Assert.assertEquals(3, Dates.getDayOfWeek(Dates.parse("20190501")));
        Assert.assertEquals(4, Dates.getDayOfWeek(Dates.parse("20190502")));
        Assert.assertEquals(5, Dates.getDayOfWeek(Dates.parse("20190503")));
        Assert.assertEquals(6, Dates.getDayOfWeek(Dates.parse("20190504")));
        Assert.assertEquals(7, Dates.getDayOfWeek(Dates.parse("20190505")));

        Assert.assertEquals(93, Dates.getDayOfYear(date));
        Assert.assertEquals(1, Dates.getDayOfYear(Dates.parse("20190101")));
        Assert.assertEquals(365, Dates.getDayOfYear(Dates.parse("20191231")));
        Assert.assertEquals(366, Dates.getDayOfYear(Dates.parse("20201231")));

        Assert.assertEquals(0, Dates.calcMonth(Dates.parse("2017-07-01"), Dates.parse("2017-07-01")));
        Assert.assertEquals(1, Dates.calcMonth(Dates.parse("2017-07-01"), Dates.parse("2017-08-01")));
        Assert.assertEquals(2, Dates.calcMonth(Dates.parse("2017-07-01"), Dates.parse("2017-09-01")));
        Assert.assertEquals(-1, Dates.calcMonth(Dates.parse("2017-10-01"), Dates.parse("2017-09-01")));
    }

    @Test
    public void testxiaolv() {
        // 测试效率
        TimeWatch watch = new TimeWatch();
        for (int i = 1; i <= 100000; i++) { // 循环一百万次
            Dates.parse("2019-01-23");
        }
        Assert.assertTrue(watch.useSeconds() < 2);

        watch.start();
        for (int i = 1; i <= 100000; i++) { // 循环一百万次
            Dates.parse("2019-01-23");
        }
        Assert.assertTrue(watch.useSeconds() < 10);
    }

    @Test
    public void testFormateCNDate() {
        Assert.assertEquals("2019年1月23日", Dates.formatCN(Dates.parse("2019-01-23")));
        Assert.assertEquals("2019年1月9日", Dates.formatCN(Dates.parse("2019-01-09")));
    }

    @Test
    public void testFormat01Date() {
        Assert.assertEquals("2019-01-23", Dates.format10(Dates.parse("2019-01-23")));
        Assert.assertEquals("2019-12-01", Dates.format10(Dates.parse("12/01/2019")));
        Assert.assertEquals("2013-12-31", Dates.format10(Dates.parse("2013-12-31")));
    }

    @Test
    public void testFormat01String() {
        Assert.assertNull(Dates.parse((String) null));
        Assert.assertEquals(Dates.parse("2019-01-23"), Dates.parse("2019-01-23"));
    }

    @Test
    public void testFormat02Date() {
        Assert.assertNull(Dates.format08((Date) null));
        Assert.assertEquals("20190123", Dates.format08(Dates.parse("2019-01-23")));
    }

    @Test
    public void testFormat02String() {
        Assert.assertNull(Dates.parse((String) null));
        Assert.assertEquals(Dates.parse("20190123"), Dates.parse("2019-01-23"));
    }

    @Test
    public void testFormat04String() {
        Assert.assertNull(Dates.parse((String) null));
        Assert.assertEquals(Dates.parse("2019-01-23 01:23:45:06"), Dates.parse("2019-01-23 01:23:45:06"));
    }

    @Test
    public void testFormat05Date() {
        Assert.assertNull(Dates.format16(null));
        Assert.assertEquals("2019-01-23 01:23", Dates.format16(Dates.parse("2019-01-23 01:23:45")));
    }

    @Test
    public void testFormat05String() {
        Assert.assertNull(Dates.parse((String) null));
        Assert.assertEquals(Dates.parse("2019-01-23 01:23"), Dates.parse("2019-01-23 01:23:00"));
    }

    @Test
    public void testFormat06Date() {
        Assert.assertNull(Dates.format19(null));
        Assert.assertEquals("2019-01-23 01:23:45", Dates.format19(Dates.parse("2019-01-23 01:23:45")));
    }

    @Test
    public void testFormat06String() {
        Assert.assertNull(Dates.parse((String) null));
        Assert.assertEquals(Dates.parse("2019-01-23 01:23"), Dates.parse("2019-01-23 01:23:00"));
    }

    @Test
    public void testFormat07Date() {
        Assert.assertNull(Dates.format14(null));
        Assert.assertEquals("20190123012345", Dates.format14(Dates.parse("2019-01-23 01:23:45")));
    }

    @Test
    public void testFormatUSADate() {
        Assert.assertNull(Dates.format01(null));
        Assert.assertEquals("01/23/2019", Dates.format01(Dates.parse("2019-01-23 01:23:45")));
    }

    @Test
    public void testFormatUKDate() {
        Assert.assertNull(Dates.format02(null));
        Assert.assertEquals("23/01/2019", Dates.format02(Dates.parse("2019-01-23 01:23:45")));
    }

    @Test
    public void testFormateStringDateString() {
        Assert.assertNull(Dates.format(null, ""));
        Assert.assertEquals("2019-01-23", Dates.format(Dates.parse("2019-01-23"), "yyyy-MM-dd"));
    }

    @Test
    public void testFormatStringString() {
        Assert.assertNull(Dates.parse((String) null));
        Assert.assertEquals(Dates.parse("20190123"), Dates.parse("2019-01-23"));
    }

    @Test
    public void testCalcMonth() {
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-01-23"), 0), Dates.parse("2019-01-23"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-01-23"), 1), Dates.parse("2019-02-23"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-02-28"), 12), Dates.parse("2020-02-28"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-03-23"), -1), Dates.parse("2019-02-23"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-01-23"), -1), Dates.parse("2018-12-23"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-12-23"), -12), Dates.parse("2018-12-23"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2019-12-23"), -13), Dates.parse("2018-11-23"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2016-03-30"), -1), Dates.parse("2016-02-29"));
        Assert.assertEquals(Dates.calcMonth(Dates.parse("2016-01-29"), 1), Dates.parse("2016-02-29"));

        Assert.assertEquals("2017-08-01", Dates.format10(Dates.calcMonth(Dates.getBeginOfMonth(Dates.parse("2017-09-12")), -1)));
        Assert.assertEquals("2019-04-30", Dates.format10(Dates.calcMonth(Dates.parse("2019-04-30"), 0)));
        Assert.assertEquals("2019-05-30", Dates.format10(Dates.calcMonth(Dates.parse("2019-04-30"), 1)));
        Assert.assertEquals("2020-02-29", Dates.format10(Dates.calcMonth(Dates.parse("2019-04-30"), 10)));
        Assert.assertEquals("2018-04-30", Dates.format10(Dates.calcMonth(Dates.parse("2019-04-30"), -12)));
    }

    @Test
    public void testCalcYear() {
        Assert.assertEquals(Dates.calcYear(Dates.parse("2019-01-23"), 0), Dates.parse("2019-01-23"));
        Assert.assertEquals(Dates.calcYear(Dates.parse("2016-02-29"), 1), Dates.parse("2017-02-28"));
        Assert.assertEquals("2019-04-30", Dates.format10(Dates.calcYear(Dates.parse("2019-04-30"), 0)));
        Assert.assertEquals("2020-04-30", Dates.format10(Dates.calcYear(Dates.parse("2019-04-30"), 1)));
        Assert.assertEquals("2029-04-30", Dates.format10(Dates.calcYear(Dates.parse("2019-04-30"), 10)));
        Assert.assertEquals("2018-04-30", Dates.format10(Dates.calcYear(Dates.parse("2019-04-30"), -1)));
        Assert.assertEquals("2009-04-30", Dates.format10(Dates.calcYear(Dates.parse("2019-04-30"), -10)));
    }

    @Test
    public void testCalcDateDateInt() {
        Assert.assertEquals("2019-04-30", Dates.format10(Dates.calcDay(Dates.parse("2019-04-30"), 0)));
        Assert.assertEquals("2019-05-01", Dates.format10(Dates.calcDay(Dates.parse("2019-04-30"), 1)));
        Assert.assertEquals("2019-05-10", Dates.format10(Dates.calcDay(Dates.parse("2019-04-30"), 10)));
        Assert.assertEquals("2019-04-29", Dates.format10(Dates.calcDay(Dates.parse("2019-04-30"), -1)));
        Assert.assertEquals("2019-02-28", Dates.format10(Dates.calcDay(Dates.parse("2019-04-30"), -61)));
    }

    @Test
    public void testCalcDays() {
        Assert.assertEquals(153, Dates.calcDay(Dates.parse("2016-05-01"), Dates.parse("2016-10-01")));
        Assert.assertEquals(0, Dates.calcDay(Dates.parse("2016-05-01"), Dates.parse("2016-05-01")));
        Assert.assertEquals(1, Dates.calcDay(Dates.parse("2016-05-01"), Dates.parse("2016-05-02")));
        Assert.assertEquals(366, Dates.calcDay(Dates.parse("2016-05-01"), Dates.parse("2017-05-02")));
    }

    @Test
    public void testGetBeginOfMonth() {
        Assert.assertEquals("2017-08-01", Dates.format10(Dates.calcMonth(Dates.getBeginOfMonth(Dates.parse("2017-09-12")), -1)));
    }

    @Test
    public void testGetEndOfMonth() {
        Assert.assertEquals(Dates.getEndOfMonth(Dates.parse("2019-02-28")), Dates.parse("2019-02-28"));
        Assert.assertEquals(Dates.getEndOfMonth(Dates.parse("2019-02-01")), Dates.parse("2019-02-28"));
        Assert.assertEquals(Dates.getEndOfMonth(Dates.parse("2020-02-28")), Dates.parse("2020-02-29"));
        Assert.assertEquals(Dates.getEndOfMonth(Dates.parse("2020-02-01")), Dates.parse("2020-02-29"));
    }

    @Test
    public void testGetDayOfWeekString() {
        Assert.assertEquals(1, Dates.parseDayOfWeek("星期1"));
        Assert.assertEquals(1, Dates.parseDayOfWeek("星期一"));
        Assert.assertEquals(7, Dates.parseDayOfWeek("星期 7"));
        Assert.assertEquals(7, Dates.parseDayOfWeek("星期 七"));
        Assert.assertEquals(2, Dates.parseDayOfWeek("星期 二"));
        Assert.assertEquals(2, Dates.parseDayOfWeek("星期 2 "));
        Assert.assertEquals(1, Dates.parseDayOfWeek("mon"));
        Assert.assertEquals(2, Dates.parseDayOfWeek("tue"));
        Assert.assertEquals(3, Dates.parseDayOfWeek("wed"));
        Assert.assertEquals(4, Dates.parseDayOfWeek("thu"));
        Assert.assertEquals(5, Dates.parseDayOfWeek("fri"));
        Assert.assertEquals(6, Dates.parseDayOfWeek("sat"));
        Assert.assertEquals(7, Dates.parseDayOfWeek("sun"));
    }

    @Test
    public void testGetDayOfWeekDate() {
        Assert.assertEquals(4, Dates.getDayOfWeek(Dates.parse("20190829")));
        Assert.assertEquals(1, Dates.getDayOfWeek(Dates.parse("20190429")));
        Assert.assertEquals(2, Dates.getDayOfWeek(Dates.parse("20190430")));
        Assert.assertEquals(3, Dates.getDayOfWeek(Dates.parse("20190501")));
        Assert.assertEquals(4, Dates.getDayOfWeek(Dates.parse("20190502")));
        Assert.assertEquals(5, Dates.getDayOfWeek(Dates.parse("20190503")));
        Assert.assertEquals(6, Dates.getDayOfWeek(Dates.parse("20190504")));
        Assert.assertEquals(7, Dates.getDayOfWeek(Dates.parse("20190505")));
    }

    @Test
    public void testIsDate02() {
        Assert.assertTrue(Dates.testFormat08("20190228"));
        Assert.assertTrue(Dates.testFormat08("20190201"));
    }

    @Test
    public void testIsDateCharCharCharCharCharCharCharChar() {
        Assert.assertTrue(Dates.isDate('2', '0', '1', '0', '0', '1', '0', '1'));
        Assert.assertTrue(Dates.isDate('2', '0', '1', '0', '1', '2', '3', '1'));
        Assert.assertFalse(Dates.isDate('2', '0', '1', '0', '1', '2', '3', '2'));
        Assert.assertTrue(Dates.isDate('2', '0', '1', '6', '0', '2', '2', '9'));
        Assert.assertFalse(Dates.isDate('2', '0', '1', '7', '0', '2', '2', '9'));
    }

    @Test
    public void testIsDateIntIntInt() {
        Assert.assertTrue(Dates.isDate(2019, 1, 1));
        Assert.assertTrue(Dates.isDate(2019, 12, 31));
    }

    @Test
    public void testIs24TimeIntIntInt() {
        Assert.assertTrue(Dates.isTime(0, 0, 0, 0));
        Assert.assertTrue(Dates.isTime(23, 59, 59, 0));
        Assert.assertTrue(Dates.isTime(0, 0, 0, 0));
        Assert.assertFalse(Dates.isTime(25, 0, 0, 0));
        Assert.assertFalse(Dates.isTime(0, 61, 0, 0));
        Assert.assertFalse(Dates.isTime(0, 0, 60, 0));
        Assert.assertFalse(Dates.isTime(0, 0, 61, 0));
        Assert.assertTrue(Dates.isTime(9, 1, 59, 0));
        Assert.assertTrue(Dates.isTime(11, 1, 59, 0));
    }

    @Test
    public void testIs24TimeIntIntIntInt() {
        Assert.assertTrue(Dates.isTime(0, 0, 0, 0));
        Assert.assertTrue(Dates.isTime(23, 59, 59, 999));
        Assert.assertFalse(Dates.isTime(24, 0, 0, 0));
        Assert.assertFalse(Dates.isTime(23, 60, 59, 0));
        Assert.assertFalse(Dates.isTime(23, 59, 60, 0));
        Assert.assertFalse(Dates.isTime(23, 59, 59, 1000));
    }

    @Test
    public void testGetYear() {
        Date date = Dates.parse("2014-04-03");
        Assert.assertEquals(2014, Dates.getYear(date));
        Assert.assertEquals(4, Dates.getMonth(date));
        Assert.assertEquals(1, Dates.getMonth(Dates.parse("20190101")));
        Assert.assertEquals(2, Dates.getMonth(Dates.parse("20190201")));
        Assert.assertEquals(3, Dates.getMonth(Dates.parse("20190301")));
        Assert.assertEquals(4, Dates.getMonth(Dates.parse("20190401")));
        Assert.assertEquals(5, Dates.getMonth(Dates.parse("20190501")));
        Assert.assertEquals(6, Dates.getMonth(Dates.parse("20190601")));
        Assert.assertEquals(7, Dates.getMonth(Dates.parse("20190701")));
        Assert.assertEquals(8, Dates.getMonth(Dates.parse("20190801")));
        Assert.assertEquals(9, Dates.getMonth(Dates.parse("20190901")));
        Assert.assertEquals(10, Dates.getMonth(Dates.parse("20191001")));
        Assert.assertEquals(11, Dates.getMonth(Dates.parse("20191101")));
        Assert.assertEquals(12, Dates.getMonth(Dates.parse("20191201")));
        try {
            Dates.getMonth(Dates.parse("20191301"));
            throw new RuntimeException();
        } catch (Exception ignored) {
        }
        Assert.assertEquals(3, Dates.getDayOfMonth(date));
        Assert.assertEquals(4, Dates.getDayOfWeek(date));
        Assert.assertEquals(1, Dates.getDayOfWeek(Dates.parse("20190429")));
        Assert.assertEquals(2, Dates.getDayOfWeek(Dates.parse("20190430")));
        Assert.assertEquals(3, Dates.getDayOfWeek(Dates.parse("20190501")));
        Assert.assertEquals(4, Dates.getDayOfWeek(Dates.parse("20190502")));
        Assert.assertEquals(5, Dates.getDayOfWeek(Dates.parse("20190503")));
        Assert.assertEquals(6, Dates.getDayOfWeek(Dates.parse("20190504")));
        Assert.assertEquals(7, Dates.getDayOfWeek(Dates.parse("20190505")));
        Assert.assertEquals(93, Dates.getDayOfYear(date));
        Assert.assertEquals(1, Dates.getDayOfYear(Dates.parse("20190101")));
        Assert.assertEquals(365, Dates.getDayOfYear(Dates.parse("20191231")));
        Assert.assertEquals(366, Dates.getDayOfYear(Dates.parse("20201231")));
    }

    @Test
    public void testGetMonth() {
        Date date = Dates.parse("2014-04-03");
        Assert.assertEquals(2014, Dates.getYear(date));
        Assert.assertEquals(4, Dates.getMonth(date));
        Assert.assertEquals(1, Dates.getMonth(Dates.parse("20190101")));
        Assert.assertEquals(2, Dates.getMonth(Dates.parse("20190201")));
        Assert.assertEquals(3, Dates.getMonth(Dates.parse("20190301")));
        Assert.assertEquals(4, Dates.getMonth(Dates.parse("20190401")));
        Assert.assertEquals(5, Dates.getMonth(Dates.parse("20190501")));
        Assert.assertEquals(6, Dates.getMonth(Dates.parse("20190601")));
        Assert.assertEquals(7, Dates.getMonth(Dates.parse("20190701")));
        Assert.assertEquals(8, Dates.getMonth(Dates.parse("20190801")));
        Assert.assertEquals(9, Dates.getMonth(Dates.parse("20190901")));
        Assert.assertEquals(10, Dates.getMonth(Dates.parse("20191001")));
        Assert.assertEquals(11, Dates.getMonth(Dates.parse("20191101")));
        Assert.assertEquals(12, Dates.getMonth(Dates.parse("20191201")));
        try {
            Dates.getMonth(Dates.parse("20191301"));
            throw new RuntimeException();
        } catch (Exception ignored) {
        }
        Assert.assertEquals(3, Dates.getDayOfMonth(date));
        Assert.assertEquals(4, Dates.getDayOfWeek(date));
        Assert.assertEquals(1, Dates.getDayOfWeek(Dates.parse("20190429")));
        Assert.assertEquals(2, Dates.getDayOfWeek(Dates.parse("20190430")));
        Assert.assertEquals(3, Dates.getDayOfWeek(Dates.parse("20190501")));
        Assert.assertEquals(4, Dates.getDayOfWeek(Dates.parse("20190502")));
        Assert.assertEquals(5, Dates.getDayOfWeek(Dates.parse("20190503")));
        Assert.assertEquals(6, Dates.getDayOfWeek(Dates.parse("20190504")));
        Assert.assertEquals(7, Dates.getDayOfWeek(Dates.parse("20190505")));
        Assert.assertEquals(93, Dates.getDayOfYear(date));
        Assert.assertEquals(1, Dates.getDayOfYear(Dates.parse("20190101")));
        Assert.assertEquals(365, Dates.getDayOfYear(Dates.parse("20191231")));
        Assert.assertEquals(366, Dates.getDayOfYear(Dates.parse("20201231")));
    }

    @Test
    public void testGetMonthOfYear() {
        Assert.assertEquals(1, Dates.parseMonth("January"));
        Assert.assertEquals(1, Dates.parseMonth("Jan"));
        Assert.assertEquals(1, Dates.parseMonth("1月"));
        Assert.assertEquals(1, Dates.parseMonth("一月"));
        Assert.assertEquals(1, Dates.parseMonth(" 一 月  "));

        Assert.assertEquals(2, Dates.parseMonth("February"));
        Assert.assertEquals(2, Dates.parseMonth("Feb"));
        Assert.assertEquals(2, Dates.parseMonth("2月"));
        Assert.assertEquals(2, Dates.parseMonth("二月"));

        Assert.assertEquals(3, Dates.parseMonth("March"));
        Assert.assertEquals(3, Dates.parseMonth("mar"));
        Assert.assertEquals(3, Dates.parseMonth("3月"));
        Assert.assertEquals(3, Dates.parseMonth("三月"));

        Assert.assertEquals(4, Dates.parseMonth("April"));
        Assert.assertEquals(4, Dates.parseMonth("apR"));
        Assert.assertEquals(4, Dates.parseMonth("4月"));
        Assert.assertEquals(4, Dates.parseMonth("四月"));

        Assert.assertEquals(5, Dates.parseMonth("May"));
        Assert.assertEquals(5, Dates.parseMonth("May"));
        Assert.assertEquals(5, Dates.parseMonth("5月"));
        Assert.assertEquals(5, Dates.parseMonth("五月"));

        Assert.assertEquals(6, Dates.parseMonth("June"));
        Assert.assertEquals(6, Dates.parseMonth("Jun"));
        Assert.assertEquals(6, Dates.parseMonth("6月"));
        Assert.assertEquals(6, Dates.parseMonth("六月"));

        Assert.assertEquals(7, Dates.parseMonth("July"));
        Assert.assertEquals(7, Dates.parseMonth("Jul"));
        Assert.assertEquals(7, Dates.parseMonth("7月"));
        Assert.assertEquals(7, Dates.parseMonth("七月"));

        Assert.assertEquals(8, Dates.parseMonth("August"));
        Assert.assertEquals(8, Dates.parseMonth("Aug"));
        Assert.assertEquals(8, Dates.parseMonth("8月"));
        Assert.assertEquals(8, Dates.parseMonth("八月"));

        Assert.assertEquals(9, Dates.parseMonth("September"));
        Assert.assertEquals(9, Dates.parseMonth("Sep"));
        Assert.assertEquals(9, Dates.parseMonth("9月"));
        Assert.assertEquals(9, Dates.parseMonth("九月"));

        Assert.assertEquals(10, Dates.parseMonth("October"));
        Assert.assertEquals(10, Dates.parseMonth("Oct"));
        Assert.assertEquals(10, Dates.parseMonth("10月"));
        Assert.assertEquals(10, Dates.parseMonth("十月"));

        Assert.assertEquals(11, Dates.parseMonth("November"));
        Assert.assertEquals(11, Dates.parseMonth("Nov"));
        Assert.assertEquals(11, Dates.parseMonth("11月"));
        Assert.assertEquals(11, Dates.parseMonth("十一月"));

        Assert.assertEquals(12, Dates.parseMonth("December"));
        Assert.assertEquals(12, Dates.parseMonth("Dec"));
        Assert.assertEquals(12, Dates.parseMonth("12月"));
        Assert.assertEquals(12, Dates.parseMonth("十二月"));
    }

    @Test
    public void testGetMonthBetweenDay() {
        Assert.assertEquals(0, Dates.calcMonth(Dates.parse("2017-07-01"), Dates.parse("2017-07-01")));
        Assert.assertEquals(1, Dates.calcMonth(Dates.parse("2017-07-01"), Dates.parse("2017-08-01")));
        Assert.assertEquals(2, Dates.calcMonth(Dates.parse("2017-07-01"), Dates.parse("2017-09-01")));
        Assert.assertEquals(-1, Dates.calcMonth(Dates.parse("2017-10-01"), Dates.parse("2017-09-01")));
    }

    @Test
    public void testGetDayOfMonth() {
        Assert.assertEquals(1, Dates.getDayOfMonth(Dates.parse("2016-01-01")));
        Assert.assertEquals(29, Dates.getDayOfMonth(Dates.parse("2016-02-29")));
        Assert.assertEquals(31, Dates.getDayOfMonth(Dates.parse("2016-01-31")));
    }

    @Test
    public void testGetHour() {
        Assert.assertEquals(0, Dates.getHour(Dates.parse("2019/04/30 00:00:00")));
        Assert.assertEquals(1, Dates.getHour(Dates.parse("2019/04/30 01:00:00")));
        Assert.assertEquals(2, Dates.getHour(Dates.parse("2019/04/30 02:00:00")));
        Assert.assertEquals(3, Dates.getHour(Dates.parse("2019/04/30 03:00:00")));
        Assert.assertEquals(4, Dates.getHour(Dates.parse("2019/04/30 04:00:00")));
        Assert.assertEquals(5, Dates.getHour(Dates.parse("2019/04/30 05:00:00")));
        Assert.assertEquals(6, Dates.getHour(Dates.parse("2019/04/30 06:00:00")));
        Assert.assertEquals(7, Dates.getHour(Dates.parse("2019/04/30 07:00:00")));
        Assert.assertEquals(8, Dates.getHour(Dates.parse("2019/04/30 08:00:00")));
        Assert.assertEquals(9, Dates.getHour(Dates.parse("2019/04/30 09:00:00")));
        Assert.assertEquals(10, Dates.getHour(Dates.parse("2019/04/30 10:00:00")));
        Assert.assertEquals(11, Dates.getHour(Dates.parse("2019/04/30 11:00:00")));
        Assert.assertEquals(12, Dates.getHour(Dates.parse("2019/04/30 12:00:00")));
        Assert.assertEquals(13, Dates.getHour(Dates.parse("2019/04/30 13:00:00")));
        Assert.assertEquals(14, Dates.getHour(Dates.parse("2019/04/30 14:00:00")));
        Assert.assertEquals(15, Dates.getHour(Dates.parse("2019/04/30 15:00:00")));
        Assert.assertEquals(16, Dates.getHour(Dates.parse("2019/04/30 16:00:00")));
        Assert.assertEquals(17, Dates.getHour(Dates.parse("2019/04/30 17:00:00")));
        Assert.assertEquals(18, Dates.getHour(Dates.parse("2019/04/30 18:00:00")));
        Assert.assertEquals(19, Dates.getHour(Dates.parse("2019/04/30 19:00:00")));
        Assert.assertEquals(20, Dates.getHour(Dates.parse("2019/04/30 20:00:00")));
        Assert.assertEquals(21, Dates.getHour(Dates.parse("2019/04/30 21:00:00")));
        Assert.assertEquals(22, Dates.getHour(Dates.parse("2019/04/30 22:00:00")));
        Assert.assertEquals(23, Dates.getHour(Dates.parse("2019/04/30 23:00:00")));
    }

    @Test
    public void testGetMinute() {
        Assert.assertEquals(58, Dates.getMinute(Dates.parse("2018-05-17 15:58:59")));
        Assert.assertEquals(59, Dates.getMinute(Dates.parse("2018-05-17 15:59:59")));
        Assert.assertEquals(0, Dates.getMinute(Dates.parse("2018-05-17 15:00:59")));
    }

    @Test
    public void testGetSecond() {
        Assert.assertEquals(1, Dates.getSecond(Dates.parse("2018-05-17 15:58:01")));
        Assert.assertEquals(59, Dates.getSecond(Dates.parse("2018-05-17 15:59:59")));
        Assert.assertEquals(2, Dates.getSecond(Dates.parse("2018-05-17 15:00:02")));
    }

    @Test
    public void testGetMillisecond() {
        Assert.assertEquals(999, Dates.getMillisecond(Dates.parse("2018-05-17 15:00:02:999")));
        Assert.assertEquals(0, Dates.getMillisecond(Dates.parse("2018-05-17 15:00:02:000")));
    }

    @Test
    public void testGetDayOfYear() {
        Date date = Dates.parse("2014-04-03");
        Assert.assertEquals(93, Dates.getDayOfYear(date));
        Assert.assertEquals(1, Dates.getDayOfYear(Dates.parse("20190101")));
        Assert.assertEquals(365, Dates.getDayOfYear(Dates.parse("20191231")));
        Assert.assertEquals(366, Dates.getDayOfYear(Dates.parse("20201231")));
    }

    @Test
    public void testBetween() {
        Assert.assertTrue(Dates.between(Dates.parse("2019-10-01"), Dates.parse("2019-10-01"), Dates.parse("2019-10-01")));
        Assert.assertTrue(Dates.between(Dates.parse("2019-10-01"), Dates.parse("2019-10-01"), Dates.parse("2019-10-02")));
        Assert.assertTrue(Dates.between(Dates.parse("2019-10-01"), Dates.parse("2019-10-01"), Dates.parse("2019-10-10")));
    }

    @Test
    public void testIsBeginOfMonth() {
        Assert.assertTrue(Dates.isBeginOfMonth(Dates.parse("2020-01-01")));
        Assert.assertTrue(Dates.isBeginOfMonth(Dates.parse("2020-02-01")));
        Assert.assertTrue(Dates.isBeginOfMonth(Dates.parse("2020-12-01")));
    }

    @Test
    public void testIsEndOfMonth() {
        Assert.assertTrue(Dates.isEndOfMonth(Dates.parse("2020-12-31")));
        Assert.assertFalse(Dates.isEndOfMonth(Dates.parse("2020-02-28")));
        Assert.assertTrue(Dates.isEndOfMonth(Dates.parse("2020-02-29")));
        Assert.assertTrue(Dates.isEndOfMonth(Dates.parse("2016-02-29")));
    }

    @Test
    public void testIsWeekend() {
        Assert.assertTrue(Dates.isWeekend(Dates.parse("2019-04-27")));
        Assert.assertTrue(Dates.isWeekend(Dates.parse("2019-04-28")));
        Assert.assertFalse(Dates.isWeekend(Dates.parse("2019-04-29")));
    }

    @Test
    public void testIsMonthOfYear() {
        Assert.assertTrue(Dates.isMonth("Jan"));
        Assert.assertTrue(Dates.isMonth("feb"));
        Assert.assertTrue(Dates.isMonth("mar"));
        Assert.assertTrue(Dates.isMonth("apr"));
        Assert.assertTrue(Dates.isMonth("may"));
        Assert.assertTrue(Dates.isMonth("jun"));
        Assert.assertTrue(Dates.isMonth("jul"));
        Assert.assertTrue(Dates.isMonth("aug"));
        Assert.assertTrue(Dates.isMonth("Sep"));
        Assert.assertTrue(Dates.isMonth("Oct"));
        Assert.assertTrue(Dates.isMonth("Nov"));
        Assert.assertTrue(Dates.isMonth("dec"));

        Assert.assertTrue(Dates.isMonth("january"));
        Assert.assertTrue(Dates.isMonth("february"));
        Assert.assertTrue(Dates.isMonth("march"));
        Assert.assertTrue(Dates.isMonth("april"));
        Assert.assertTrue(Dates.isMonth("may"));
        Assert.assertTrue(Dates.isMonth("june"));
        Assert.assertTrue(Dates.isMonth("july"));
        Assert.assertTrue(Dates.isMonth("august"));
        Assert.assertTrue(Dates.isMonth("September"));
        Assert.assertTrue(Dates.isMonth("October"));
        Assert.assertTrue(Dates.isMonth("November"));
        Assert.assertTrue(Dates.isMonth("december"));
    }

    @Test
    public void testIsDayOfWeek() {
        Assert.assertTrue(Dates.isDayOfWeek("mon"));
        Assert.assertTrue(Dates.isDayOfWeek("Tue"));
        Assert.assertTrue(Dates.isDayOfWeek("wed"));
        Assert.assertTrue(Dates.isDayOfWeek("thu"));
        Assert.assertTrue(Dates.isDayOfWeek("sat"));
        Assert.assertTrue(Dates.isDayOfWeek("sun"));
        Assert.assertTrue(Dates.isDayOfWeek("fri"));

        Assert.assertTrue(Dates.isDayOfWeek("monday"));
        Assert.assertTrue(Dates.isDayOfWeek("Tuesday"));
        Assert.assertTrue(Dates.isDayOfWeek("wednesday"));
        Assert.assertTrue(Dates.isDayOfWeek("thursday"));
        Assert.assertTrue(Dates.isDayOfWeek("saturday"));
        Assert.assertTrue(Dates.isDayOfWeek("sunday"));
        Assert.assertTrue(Dates.isDayOfWeek("friday"));
    }

    @Test
    public void testCompare() {
        Assert.assertEquals(0, Dates.compare(null, null));
        Assert.assertEquals(-1, Dates.compare(null, Dates.parse("20191010")));
        Assert.assertEquals(1, Dates.compare(Dates.parse("20191010"), null));
        Assert.assertEquals(0, Dates.compare(Dates.parse("20191010"), Dates.parse("20191010")));
        Assert.assertEquals(1, Dates.compare(Dates.parse("20191010"), Dates.parse("20191001")));
        Assert.assertEquals(1, Dates.compare(Dates.parse("20191010"), Dates.parse("20181201")));
        Assert.assertEquals(-1, Dates.compare(Dates.parse("20171010"), Dates.parse("20181201")));
        Assert.assertEquals(-1, Dates.compare(Dates.parse("20171010"), Dates.parse("20180901")));
    }

    @Test
    public void testCompareMonth() {
        Assert.assertEquals(0, Dates.compareIgnoreDay(Dates.parse("2019-04-29"), Dates.parse("2019-04-29")));
        Assert.assertEquals(0, Dates.compareIgnoreDay(Dates.parse("2019-04-29"), Dates.parse("2019-04-28")));
        Assert.assertEquals(1, Dates.compareIgnoreDay(Dates.parse("2019-04-29"), Dates.parse("2019-03-28")));
        Assert.assertEquals(-1, Dates.compareIgnoreDay(Dates.parse("2019-04-29"), Dates.parse("2019-05-28")));
        Assert.assertEquals(-2, Dates.compareIgnoreDay(Dates.parse("2019-04-29"), Dates.parse("2019-06-28")));
    }

    @Test
    public void testMin() {
        Assert.assertEquals(Dates.min(null, Dates.parse("2019-01-01"), Dates.parse("2019-02-01"), Dates.parse("2019-12-01"), Dates.parse("2018-11-01")), Dates.parse("2018-11-01"));
        Assert.assertEquals(Dates.min(Dates.parse("2019-01-01"), Dates.parse("2019-02-01"), Dates.parse("2019-12-01"), Dates.parse("2019-11-01")), Dates.parse("2019-01-01"));
    }

    @Test
    public void testMax() {
        Assert.assertEquals(Dates.max(Dates.parse("2019-01-01"), Dates.parse("2019-02-01"), Dates.parse("2019-12-01"), Dates.parse("2019-11-01")), Dates.parse("2019-12-01"));
        Assert.assertEquals(Dates.max(null, Dates.parse("2019-01-01"), Dates.parse("2019-02-01"), Dates.parse("2019-12-01"), Dates.parse("2020-01-01")), Dates.parse("2020-01-01"));
    }

    @Test
    public void testIsLeapYearInt() {
        Assert.assertTrue(Dates.isLeapYear(1804));
        Assert.assertTrue(Dates.isLeapYear(1880));
        Assert.assertFalse(Dates.isLeapYear(1881));
        Assert.assertTrue(Dates.isLeapYear(1888));
        Assert.assertTrue(Dates.isLeapYear(2000));
        Assert.assertTrue(Dates.isLeapYear(2020));
        Assert.assertFalse(Dates.isLeapYear(2021));
    }

    @Test
    public void testGetCurrentTime03() {
        Assert.assertEquals(Dates.currentTimeStamp(), Dates.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    public void testTestSimpleDateFormatApply() {
        String[] ds = {"1990-12-08", "1995-12-15", "1998-04-11", "1998-04-29", "1998-09-23", "1999-02-01", "1999-02-17", "1999-03-12", "1999-05-04", "1999-05-20", "2000-03-26", "2000-07-19", "2001-03-28", "2001-07-18", "2001-07-20", "2001-09-06", "2001-09-26", "2001-11-02", "2002-01-28", "2002-06-07", "2002-06-13", "2002-06-14", "2002-06-15", "2002-06-22", "2002-06-25", "2002-06-26", "2002-07-05", "2002-07-06", "2002-07-09", "2002-07-20", "2002-08-06", "2002-09-06", "2002-09-07", "2002-09-10", "2002-09-18", "2002-09-19", "2002-09-24", "2002-09-27", "2002-10-08", "2002-10-26", "2002-10-29", "2002-10-31", "2002-11-07", "2003-01-14", "2003-01-31", "2003-05-20", "2003-05-29", "2003-06-20", "2003-07-20", "2003-07-31", "2003-08-19", "2003-08-20", "2003-09-20", "2003-10-20", "2003-11-20", "2003-12-20", "2004-01-20", "2004-02-20", "2004-03-20", "2004-04-20", "2004-05-20", "2004-06-20", "2004-07-20", "2004-08-20", "2004-09-20", "2004-10-20", "2004-11-20", "2004-12-20", "2005-01-20", "2005-02-20", "2005-03-20", "2005-04-20", "2005-05-20", "2005-06-20", "2005-07-20", "2005-08-20", "2005-09-20", "2005-10-20", "2005-11-20", "2005-12-20", "2006-01-20", "2006-02-20", "2006-03-20", "2006-04-20", "2006-05-20", "2006-06-20", "2006-07-20", "2006-08-20", "2006-09-20", "2006-10-20", "2006-11-07", "2006-11-08", "2006-11-16", "2006-11-20", "2006-11-21", "2006-11-28", "2006-12-05", "2006-12-13", "2006-12-14", "2006-12-15", "2006-12-20", "2006-12-28", "2007-01-05", "2007-01-08", "2007-01-09", "2007-01-12", "2007-01-15", "2007-01-20", "2007-01-31", "2007-02-01", "2007-02-20", "2007-03-01", "2007-03-02", "2007-03-05", "2007-03-08", "2007-03-13", "2007-03-14", "2007-03-20", "2007-04-03", "2007-04-04", "2007-04-05", "2007-04-10", "2007-04-13", "2007-04-14", "2007-04-20", "2007-04-26", "2007-04-27", "2007-05-09", "2007-05-20", "2007-05-23", "2007-06-04", "2007-06-07", "2007-06-11", "2007-06-18", "2007-06-20", "2007-07-16", "2007-07-20", "2007-07-25", "2007-08-06", "2007-08-14", "2007-08-20", "2007-08-23", "2007-09-10", "2007-09-19", "2007-09-20", "2007-10-20", "2007-11-20", "2007-11-28", "2007-11-29", "2007-12-06", "2007-12-08", "2007-12-09", "2007-12-11", "2007-12-12", "2007-12-13", "2007-12-14", "2007-12-15", "2007-12-16", "2007-12-18", "2007-12-19", "2007-12-20", "2007-12-21", "2007-12-22", "2007-12-23", "2007-12-24", "2007-12-26", "2007-12-27", "2007-12-28", "2008-01-04", "2008-01-05", "2008-01-07", "2008-01-08", "2008-01-09", "2008-01-10", "2008-01-11", "2008-01-12", "2008-01-13", "2008-01-14", "2008-01-15", "2008-01-17", "2008-01-18", "2008-01-19", "2008-01-20", "2008-01-21", "2008-01-22", "2008-01-23", "2008-01-24", "2008-01-25", "2008-01-26", "2008-01-27", "2008-01-28", "2008-01-29", "2008-01-30", "2008-02-01", "2008-02-04", "2008-02-05", "2008-02-06", "2008-02-07", "2008-02-11", "2008-02-12", "2008-02-13", "2008-02-20", "2008-02-25", "2008-02-26", "2008-02-29", "2008-03-01", "2008-03-03", "2008-03-08", "2008-03-18", "2008-03-19", "2008-03-20", "2008-03-21", "2008-03-22", "2008-03-23", "2008-03-26", "2008-03-27", "2008-03-28", "2008-03-29", "2008-04-01", "2008-04-03", "2008-04-04", "2008-04-05", "2008-04-06", "2008-04-07", "2008-04-08", "2008-04-09", "2008-04-10", "2008-04-11", "2008-04-12", "2008-04-13", "2008-04-14", "2008-04-15", "2008-04-16", "2008-04-17", "2008-04-18", "2008-04-19", "2008-04-20", "2008-04-21", "2008-04-22", "2008-04-23", "2008-04-24", "2008-04-25", "2008-04-26", "2008-04-27", "2008-04-28", "2008-04-30", "2008-05-01", "2008-05-09", "2008-05-17", "2008-05-20", "2008-06-01", "2008-06-04", "2008-06-06", "2008-06-10", "2008-06-11", "2008-06-13", "2008-06-20", "2008-06-23", "2008-07-02", "2008-07-03", "2008-07-04", "2008-07-05", "2008-07-11", "2008-07-12", "2008-07-14", "2008-07-16", "2008-07-18", "2008-07-19", "2008-07-20", "2008-07-22", "2008-07-23", "2008-07-24", "2008-07-26", "2008-07-30", "2008-08-01", "2008-08-02", "2008-08-06", "2008-08-09", "2008-08-20", "2008-08-21", "2008-08-30", "2008-09-20", "2008-09-29", "2008-09-30", "2008-10-09", "2008-10-11", "2008-10-17", "2008-10-20", "2008-10-21", "2008-10-25", "2008-11-01", "2008-11-05", "2008-11-06", "2008-11-07", "2008-11-08", "2008-11-10", "2008-11-12", "2008-11-14", "2008-11-15", "2008-11-16", "2008-11-17", "2008-11-18", "2008-11-19", "2008-11-20", "2008-11-24", "2008-11-25", "2008-11-26", "2008-11-27", "2008-11-28", "2008-11-29", "2008-12-01", "2008-12-05", "2008-12-06", "2008-12-10", "2008-12-12", "2008-12-15", "2008-12-20", "2008-12-28", "2008-12-30", "2009-01-02", "2009-01-03", "2009-01-04", "2009-01-05", "2009-01-06", "2009-01-07", "2009-01-08", "2009-01-09", "2009-01-10", "2009-01-11", "2009-01-12", "2009-01-13", "2009-01-14", "2009-01-15", "2009-01-16", "2009-01-17", "2009-01-19", "2009-01-20", "2009-01-21", "2009-01-22", "2009-01-23", "2009-01-24", "2009-01-25", "2009-01-26", "2009-01-27", "2009-01-28", "2009-01-30", "2009-01-31", "2009-02-01", "2009-02-06", "2009-02-11", "2009-02-12", "2009-02-13", "2009-02-20", "2009-02-27", "2009-02-28", "2009-03-01", "2009-03-02", "2009-03-03", "2009-03-05", "2009-03-06", "2009-03-07", "2009-03-08", "2009-03-10", "2009-03-11", "2009-03-12", "2009-03-13", "2009-03-15", "2009-03-16", "2009-03-18", "2009-03-19", "2009-03-20", "2009-03-21", "2009-03-22", "2009-03-24", "2009-03-25", "2009-03-26", "2009-03-27", "2009-03-28", "2009-03-31", "2009-04-01", "2009-04-02", "2009-04-03", "2009-04-04", "2009-04-05", "2009-04-06", "2009-04-07", "2009-04-08", "2009-04-10", "2009-04-11", "2009-04-12", "2009-04-13", "2009-04-14", "2009-04-15", "2009-04-16", "2009-04-18", "2009-04-19", "2009-04-20", "2009-04-22", "2009-04-23", "2009-04-24", "2009-04-25", "2009-04-26", "2009-04-28", "2009-04-29", "2009-05-04", "2009-05-05", "2009-05-06", "2009-05-07", "2009-05-08", "2009-05-12", "2009-05-13", "2009-05-20", "2009-05-21", "2009-05-22", "2009-05-27", "2009-05-28", "2009-06-01", "2009-06-04", "2009-06-15", "2009-06-17", "2009-06-20", "2009-07-01", "2009-07-02", "2009-07-04", "2009-07-14", "2009-07-15", "2009-07-16", "2009-07-20", "2009-07-24", "2009-07-25", "2009-07-28", "2009-07-30", "2009-07-31", "2009-08-06", "2009-08-07", "2009-08-11", "2009-08-12", "2009-08-13", "2009-08-20", "2009-08-21", "2009-08-23", "2009-08-30", "2009-09-01", "2009-09-02", "2009-09-09", "2009-09-17", "2009-09-19", "2009-09-20", "2009-09-24", "2009-09-25", "2009-09-28", "2009-09-29", "2009-09-30", "2009-10-09", "2009-10-12", "2009-10-13", "2009-10-14", "2009-10-18", "2009-10-20", "2009-10-21", "2009-10-22", "2009-10-23", "2009-10-26", "2009-11-01", "2009-11-05", "2009-11-07", "2009-11-08", "2009-11-10", "2009-11-14", "2009-11-15", "2009-11-16", "2009-11-17", "2009-11-18", "2009-11-19", "2009-11-20", "2009-11-23", "2009-11-24", "2009-11-25", "2009-11-28", "2009-11-29", "2009-12-01", "2009-12-02", "2009-12-04", "2009-12-06", "2009-12-10", "2009-12-11", "2009-12-15", "2009-12-20", "2009-12-26", "2009-12-30", "2010-01-01", "2010-01-02", "2010-01-03", "2010-01-04", "2010-01-05", "2010-01-06", "2010-01-07", "2010-01-08", "2010-01-09", "2010-01-10", "2010-01-11", "2010-01-12", "2010-01-13", "2010-01-14", "2010-01-15", "2010-01-16", "2010-01-17", "2010-01-18", "2010-01-19", "2010-01-20", "2010-01-21", "2010-01-22", "2010-01-23", "2010-01-24", "2010-01-25", "2010-01-26", "2010-01-27", "2010-01-28", "2010-01-29", "2010-01-30", "2010-01-31", "2010-02-01", "2010-02-03", "2010-02-04", "2010-02-09", "2010-02-10", "2010-02-11", "2010-02-12", "2010-02-13", "2010-02-14", "2010-02-15", "2010-02-16", "2010-02-17", "2010-02-18", "2010-02-19", "2010-02-20", "2010-02-21", "2010-02-22", "2010-02-23", "2010-02-24", "2010-02-25", "2010-02-26", "2010-02-27", "2010-02-28", "2010-03-01", "2010-03-02", "2010-03-03", "2010-03-05", "2010-03-07", "2010-03-08", "2010-03-09", "2010-03-10", "2010-03-11", "2010-03-12", "2010-03-13", "2010-03-14", "2010-03-15", "2010-03-16", "2010-03-18", "2010-03-19", "2010-03-20", "2010-03-21", "2010-03-22", "2010-03-23", "2010-03-24", "2010-03-25", "2010-03-26", "2010-03-27", "2010-03-28", "2010-03-29", "2010-03-30", "2010-04-01", "2010-04-02", "2010-04-03", "2010-04-04", "2010-04-05", "2010-04-07", "2010-04-08", "2010-04-09", "2010-04-10", "2010-04-11", "2010-04-12", "2010-04-13", "2010-04-14", "2010-04-15", "2010-04-16", "2010-04-17", "2010-04-18", "2010-04-19", "2010-04-20", "2010-04-21", "2010-04-22", "2010-04-23", "2010-04-24", "2010-04-25", "2010-04-26", "2010-04-27", "2010-04-28", "2010-04-30", "2010-05-07", "2010-05-09", "2010-05-10", "2010-05-11", "2010-05-12", "2010-05-13", "2010-05-14", "2010-05-16", "2010-05-18", "2010-05-20", "2010-05-30", "2010-06-01", "2010-06-11", "2010-06-12", "2010-06-17", "2010-06-20", "2010-06-22", "2010-06-28", "2010-06-30", "2010-07-01", "2010-07-04", "2010-07-07", "2010-07-08", "2010-07-09", "2010-07-14", "2010-07-15", "2010-07-19", "2010-07-20", "2010-07-21", "2010-07-23", "2010-07-28", "2010-07-29", "2010-08-01", "2010-08-02", "2010-08-06", "2010-08-09", "2010-08-10", "2010-08-13", "2010-08-16", "2010-08-17", "2010-08-18", "2010-08-19", "2010-08-20", "2010-08-21", "2010-08-25", "2010-08-26", "2010-08-27", "2010-08-28", "2010-08-29", "2010-08-30", "2010-08-31", "2010-09-06", "2010-09-08", "2010-09-10", "2010-09-15", "2010-09-17", "2010-09-20", "2010-09-23", "2010-09-24", "2010-09-26", "2010-10-09", "2010-10-11", "2010-10-15", "2010-10-17", "2010-10-18", "2010-10-19", "2010-10-20", "2010-10-21", "2010-10-22", "2010-10-23", "2010-10-26", "2010-10-27", "2010-10-29", "2010-10-30", "2010-11-01", "2010-11-02", "2010-11-03", "2010-11-04", "2010-11-05", "2010-11-06", "2010-11-07", "2010-11-08", "2010-11-09", "2010-11-11", "2010-11-13", "2010-11-14", "2010-11-15", "2010-11-16", "2010-11-17", "2010-11-19", "2010-11-20", "2010-11-24", "2010-11-25", "2010-11-27", "2010-11-28", "2010-11-29", "2010-11-30", "2010-12-01", "2010-12-02", "2010-12-04", "2010-12-06", "2010-12-09", "2010-12-12", "2010-12-15", "2010-12-20", "2010-12-23", "2010-12-24", "2010-12-25", "2010-12-27", "2011-01-01", "2011-01-02", "2011-01-03", "2011-01-04", "2011-01-05", "2011-01-06", "2011-01-07", "2011-01-08", "2011-01-09", "2011-01-10", "2011-01-11", "2011-01-12", "2011-01-13", "2011-01-14", "2011-01-15", "2011-01-16", "2011-01-17", "2011-01-18", "2011-01-19", "2011-01-20", "2011-01-21", "2011-01-22", "2011-01-23", "2011-01-24", "2011-01-25", "2011-01-26", "2011-01-27", "2011-01-28", "2011-01-29", "2011-01-30", "2011-02-01", "2011-02-02", "2011-02-03", "2011-02-04", "2011-02-05", "2011-02-06", "2011-02-07", "2011-02-08", "2011-02-18", "2011-02-19", "2011-02-20", "2011-02-23", "2011-02-24", "2011-02-28", "2011-03-01", "2011-03-02", "2011-03-03", "2011-03-04", "2011-03-05", "2011-03-06", "2011-03-07", "2011-03-08", "2011-03-09", "2011-03-10", "2011-03-11", "2011-03-12", "2011-03-14", "2011-03-15", "2011-03-16", "2011-03-17", "2011-03-18", "2011-03-20", "2011-03-21", "2011-03-22", "2011-03-24", "2011-03-25", "2011-03-26", "2011-03-27", "2011-03-28", "2011-03-29", "2011-03-31", "2011-04-01", "2011-04-02", "2011-04-04", "2011-04-05", "2011-04-06", "2011-04-07", "2011-04-08", "2011-04-09", "2011-04-10", "2011-04-11", "2011-04-12", "2011-04-13", "2011-04-14", "2011-04-15", "2011-04-16", "2011-04-17", "2011-04-18", "2011-04-20", "2011-04-21", "2011-04-22", "2011-04-23", "2011-04-24", "2011-04-25", "2011-04-26", "2011-04-27", "2011-04-28", "2011-04-29", "2011-04-30", "2011-05-01", "2011-05-05", "2011-05-06", "2011-05-09", "2011-05-10", "2011-05-11", "2011-05-12", "2011-05-15", "2011-05-16", "2011-05-18", "2011-05-20", "2011-05-22", "2011-05-23", "2011-05-25", "2011-05-26", "2011-05-28", "2011-05-30", "2011-05-31", "2011-06-01", "2011-06-02", "2011-06-03", "2011-06-06", "2011-06-07", "2011-06-10", "2011-06-11", "2011-06-12", "2011-06-15", "2011-06-16", "2011-06-18", "2011-06-20", "2011-06-24", "2011-06-27", "2011-06-28", "2011-06-29", "2011-06-30", "2011-07-01", "2011-07-04", "2011-07-15", "2011-07-20", "2011-07-28", "2011-08-01", "2011-08-04", "2011-08-08", "2011-08-15", "2011-08-16", "2011-08-19", "2011-08-20", "2011-08-25", "2011-08-27", "2011-08-29", "2011-09-01", "2011-09-05", "2011-09-08", "2011-09-09", "2011-09-12", "2011-09-15", "2011-09-17", "2011-09-18", "2011-09-20", "2011-09-24", "2011-09-28", "2011-10-01", "2011-10-03", "2011-10-04", "2011-10-05", "2011-10-08", "2011-10-09", "2011-10-10", "2011-10-12", "2011-10-15", "2011-10-20", "2011-10-22", "2011-10-23", "2011-10-24", "2011-10-25", "2011-10-26", "2011-10-29", "2011-10-30", "2011-11-01", "2011-11-02", "2011-11-03", "2011-11-04", "2011-11-05", "2011-11-09", "2011-11-10", "2011-11-11", "2011-11-12", "2011-11-13", "2011-11-15", "2011-11-16", "2011-11-18", "2011-11-19", "2011-11-20", "2011-11-21", "2011-11-25", "2011-11-26", "2011-11-27", "2011-11-28", "2011-11-29", "2011-11-30", "2011-12-01", "2011-12-02", "2011-12-05", "2011-12-10", "2011-12-12", "2011-12-15", "2011-12-20", "2011-12-23", "2011-12-24", "2011-12-25", "2011-12-30", "2012-01-01", "2012-01-03", "2012-01-04", "2012-01-05", "2012-01-06", "2012-01-07", "2012-01-08", "2012-01-09", "2012-01-10", "2012-01-11", "2012-01-12", "2012-01-13", "2012-01-14", "2012-01-15", "2012-01-16", "2012-01-17", "2012-01-18", "2012-01-19", "2012-01-20", "2012-01-21", "2012-01-23", "2012-01-24", "2012-01-25", "2012-01-26", "2012-01-27", "2012-01-28", "2012-01-31", "2012-02-01", "2012-02-02", "2012-02-04", "2012-02-05", "2012-02-06", "2012-02-07", "2012-02-08", "2012-02-09", "2012-02-10", "2012-02-12", "2012-02-13", "2012-02-14", "2012-02-15", "2012-02-16", "2012-02-17", "2012-02-18", "2012-02-19", "2012-02-20", "2012-02-21", "2012-02-22", "2012-02-23", "2012-02-24", "2012-02-25", "2012-02-26", "2012-02-27", "2012-02-28", "2012-02-29", "2012-03-01", "2012-03-02", "2012-03-03", "2012-03-04", "2012-03-05", "2012-03-07", "2012-03-08", "2012-03-09", "2012-03-10", "2012-03-11", "2012-03-12", "2012-03-13", "2012-03-14", "2012-03-15", "2012-03-16", "2012-03-17", "2012-03-18", "2012-03-19", "2012-03-20", "2012-03-21", "2012-03-22", "2012-03-23", "2012-03-24", "2012-03-25", "2012-03-26", "2012-03-27", "2012-03-28", "2012-03-29", "2012-03-30", "2012-03-31", "2012-04-01", "2012-04-02", "2012-04-03", "2012-04-05", "2012-04-06", "2012-04-07", "2012-04-08", "2012-04-09", "2012-04-10", "2012-04-11", "2012-04-12", "2012-04-13", "2012-04-14", "2012-04-15", "2012-04-16", "2012-04-17", "2012-04-18", "2012-04-19", "2012-04-20", "2012-04-22", "2012-04-23", "2012-04-24", "2012-04-25", "2012-04-26", "2012-04-27", "2012-04-28", "2012-04-29", "2012-04-30", "2012-05-01", "2012-05-04", "2012-05-05", "2012-05-06", "2012-05-07", "2012-05-08", "2012-05-09", "2012-05-10", "2012-05-11", "2012-05-12", "2012-05-13", "2012-05-14", "2012-05-15", "2012-05-16", "2012-05-17", "2012-05-18", "2012-05-19", "2012-05-20", "2012-05-24", "2012-05-25", "2012-05-26", "2012-05-29", "2012-05-30", "2012-06-01", "2012-06-02", "2012-06-03", "2012-06-07", "2012-06-10", "2012-06-12", "2012-06-14", "2012-06-15", "2012-06-16", "2012-06-17", "2012-06-20", "2012-06-22", "2012-06-23", "2012-06-24", "2012-06-25", "2012-06-28", "2012-06-29", "2012-07-01", "2012-07-02", "2012-07-04", "2012-07-06", "2012-07-07", "2012-07-10", "2012-07-11", "2012-07-13", "2012-07-14", "2012-07-15", "2012-07-16", "2012-07-20", "2012-07-25", "2012-07-28", "2012-08-01", "2012-08-04", "2012-08-08", "2012-08-10", "2012-08-15", "2012-08-16", "2012-08-20", "2012-08-21", "2012-08-22", "2012-08-23", "2012-08-24", "2012-08-25", "2012-08-28", "2012-08-31", "2012-09-01", "2012-09-04", "2012-09-08", "2012-09-09", "2012-09-10", "2012-09-12", "2012-09-13", "2012-09-15", "2012-09-17", "2012-09-19", "2012-09-20", "2012-09-22", "2012-09-24", "2012-09-27", "2012-10-01", "2012-10-10", "2012-10-11", "2012-10-13", "2012-10-15", "2012-10-18", "2012-10-19", "2012-10-20", "2012-10-21", "2012-10-22", "2012-10-23", "2012-10-24", "2012-10-25", "2012-10-26", "2012-10-28", "2012-10-29", "2012-10-30", "2012-11-02", "2012-11-04", "2012-11-05", "2012-11-07", "2012-11-08", "2012-11-09", "2012-11-10", "2012-11-11", "2012-11-13", "2012-11-14", "2012-11-15", "2012-11-16", "2012-11-17", "2012-11-18", "2012-11-19", "2012-11-20", "2012-11-21", "2012-11-22", "2012-11-24", "2012-11-25", "2012-11-26", "2012-11-28", "2012-11-29", "2012-11-30", "2012-12-01", "2012-12-02", "2012-12-03", "2012-12-04", "2012-12-05", "2012-12-06", "2012-12-10", "2012-12-11", "2012-12-12", "2012-12-15", "2012-12-16", "2012-12-17", "2012-12-18", "2012-12-20", "2012-12-21", "2012-12-22", "2012-12-24", "2012-12-25", "2013-01-01", "2013-01-04", "2013-01-05", "2013-01-06", "2013-01-07", "2013-01-08", "2013-01-09", "2013-01-10", "2013-01-11", "2013-01-12", "2013-01-13", "2013-01-14", "2013-01-15", "2013-01-16", "2013-01-17", "2013-01-18", "2013-01-19", "2013-01-20", "2013-01-21", "2013-01-22", "2013-01-23", "2013-01-24", "2013-01-25", "2013-01-26", "2013-01-27", "2013-01-28", "2013-01-29", "2013-01-30", "2013-01-31", "2013-02-01", "2013-02-03", "2013-02-04", "2013-02-05", "2013-02-06", "2013-02-07", "2013-02-08", "2013-02-09", "2013-02-10", "2013-02-11", "2013-02-13", "2013-02-15", "2013-02-16", "2013-02-17", "2013-02-18", "2013-02-20", "2013-02-21", "2013-02-22", "2013-02-23", "2013-02-25", "2013-02-26", "2013-02-27", "2013-02-28", "2013-03-01", "2013-03-04", "2013-03-05", "2013-03-06", "2013-03-07", "2013-03-08", "2013-03-09", "2013-03-10", "2013-03-11", "2013-03-12", "2013-03-13", "2013-03-14", "2013-03-15", "2013-03-16", "2013-03-17", "2013-03-18", "2013-03-19", "2013-03-20", "2013-03-21", "2013-03-22", "2013-03-23", "2013-03-24", "2013-03-25", "2013-03-26", "2013-03-27", "2013-03-28", "2013-03-29", "2013-03-30", "2013-03-31", "2013-04-01", "2013-04-02", "2013-04-03", "2013-04-04", "2013-04-05", "2013-04-06", "2013-04-07", "2013-04-08", "2013-04-09", "2013-04-10", "2013-04-11", "2013-04-12", "2013-04-13", "2013-04-14", "2013-04-15", "2013-04-16", "2013-04-17", "2013-04-18", "2013-04-19", "2013-04-20", "2013-04-21", "2013-04-22", "2013-04-23", "2013-04-24", "2013-04-25", "2013-04-26", "2013-04-27", "2013-04-28", "2013-04-29", "2013-04-30", "2013-05-01", "2013-05-02", "2013-05-04", "2013-05-05", "2013-05-06", "2013-05-07", "2013-05-08", "2013-05-09", "2013-05-10", "2013-05-11", "2013-05-13", "2013-05-14", "2013-05-15", "2013-05-16", "2013-05-20", "2013-05-21", "2013-05-22", "2013-05-24", "2013-05-25", "2013-05-27", "2013-05-28", "2013-05-29", "2013-05-30", "2013-05-31", "2013-06-01", "2013-06-02", "2013-06-03", "2013-06-04", "2013-06-05", "2013-06-06", "2013-06-07", "2013-06-09", "2013-06-10", "2013-06-11", "2013-06-12", "2013-06-15", "2013-06-19", "2013-06-20", "2013-06-22", "2013-06-24", "2013-06-25", "2013-06-26", "2013-06-27", "2013-06-28", "2013-07-01", "2013-07-02", "2013-07-03", "2013-07-04", "2013-07-08", "2013-07-09", "2013-07-10", "2013-07-13", "2013-07-15", "2013-07-16", "2013-07-20", "2013-07-23", "2013-07-24", "2013-07-26", "2013-07-27", "2013-07-28", "2013-08-06", "2013-08-07", "2013-08-09", "2013-08-10", "2013-08-19", "2013-08-20", "2013-08-21", "2013-08-23", "2013-08-26", "2013-08-28", "2013-08-29", "2013-08-31", "2013-09-01", "2013-09-03", "2013-09-04", "2013-09-08", "2013-09-09", "2013-09-10", "2013-09-12", "2013-09-13", "2013-09-16", "2013-09-19", "2013-09-20", "2013-09-21", "2013-09-22", "2013-09-23", "2013-09-26", "2013-09-27", "2013-10-08", "2013-10-09", "2013-10-10", "2013-10-15", "2013-10-17", "2013-10-18", "2013-10-19", "2013-10-20", "2013-10-21", "2013-10-22", "2013-10-23", "2013-10-24", "2013-10-25", "2013-10-26", "2013-10-28", "2013-10-29", "2013-10-30", "2013-11-01", "2013-11-02", "2013-11-03", "2013-11-04", "2013-11-05", "2013-11-06", "2013-11-07", "2013-11-08", "2013-11-10", "2013-11-11", "2013-11-12", "2013-11-13", "2013-11-14", "2013-11-15", "2013-11-17", "2013-11-18", "2013-11-19", "2013-11-20", "2013-11-21", "2013-11-22", "2013-11-25", "2013-11-27", "2013-11-28", "2013-11-29", "2013-11-30", "2013-12-01", "2013-12-02", "2013-12-03", "2013-12-04", "2013-12-05", "2013-12-06", "2013-12-08", "2013-12-10", "2013-12-13", "2013-12-15", "2013-12-16", "2013-12-20", "2013-12-25", "2014-01-01", "2014-01-02", "2014-01-03", "2014-01-06", "2014-01-07", "2014-01-08", "2014-01-09", "2014-01-10", "2014-01-13", "2014-01-14", "2014-01-15", "2014-01-16", "2014-01-17", "2014-01-20", "2014-01-21", "2014-01-22", "2014-01-23", "2014-01-24", "2014-01-26", "2014-01-27", "2014-01-28", "2014-01-29", "2014-01-30", "2014-02-01", "2014-02-05", "2014-02-07", "2014-02-10", "2014-02-12", "2014-02-13", "2014-02-14", "2014-02-16", "2014-02-17", "2014-02-18", "2014-02-20", "2014-02-21", "2014-02-24", "2014-02-25", "2014-02-27", "2014-02-28", "2014-03-01", "2014-03-03", "2014-03-04", "2014-03-05", "2014-03-06", "2014-03-07", "2014-03-10", "2014-03-11", "2014-03-12", "2014-03-13", "2014-03-14", "2014-03-15", "2014-03-17", "2014-03-18", "2014-03-19", "2014-03-20", "2014-03-21", "2014-03-22", "2014-03-23", "2014-03-24", "2014-03-25", "2014-03-26", "2014-03-27", "2014-03-28", "2014-03-29", "2014-03-30", "2014-03-31", "2014-04-01", "2014-04-02", "2014-04-04", "2014-04-05", "2014-04-07", "2014-04-08", "2014-04-09", "2014-04-10", "2014-04-11", "2014-04-14", "2014-04-15", "2014-04-16", "2014-04-17", "2014-04-18", "2014-04-19", "2014-04-20", "2014-04-21", "2014-04-22", "2014-04-23", "2014-04-24", "2014-04-25", "2014-04-26", "2014-04-27", "2014-04-28", "2014-04-29", "2014-04-30", "2014-05-02", "2014-05-04", "2014-05-05", "2014-05-06", "2014-05-07", "2014-05-08", "2014-05-09", "2014-05-10", "2014-05-12", "2014-05-13", "2014-05-14", "2014-05-15", "2014-05-16", "2014-05-19", "2014-05-20", "2014-05-21", "2014-05-22", "2014-05-23", "2014-05-25", "2014-05-26", "2014-05-27", "2014-05-28", "2014-05-30", "2014-05-31", "2014-06-02", "2014-06-03", "2014-06-04", "2014-06-05", "2014-06-06", "2014-06-09", "2014-06-10", "2014-06-11", "2014-06-12", "2014-06-13", "2014-06-14", "2014-06-16", "2014-06-17", "2014-06-18", "2014-06-19", "2014-06-20", "2014-06-21", "2014-06-23", "2014-06-24", "2014-06-25", "2014-06-26", "2014-06-27", "2014-06-30", "2014-07-01", "2014-07-02", "2014-07-04", "2014-07-07", "2014-07-10", "2014-07-11", "2014-07-13", "2014-07-14", "2014-07-15", "2014-07-16", "2014-07-20", "2014-07-21", "2014-07-22", "2014-07-23", "2014-07-24", "2014-07-26", "2014-07-27", "2014-07-28", "2014-07-29", "2014-07-30", "2014-07-31", "2014-08-01", "2014-08-04", "2014-08-05", "2014-08-07", "2014-08-08", "2014-08-09", "2014-08-10", "2014-08-11", "2014-08-12", "2014-08-13", "2014-08-14", "2014-08-15", "2014-08-18", "2014-08-19", "2014-08-20", "2014-08-21", "2014-08-22", "2014-08-25", "2014-08-27", "2014-08-28", "2014-08-29", "2014-09-01", "2014-09-02", "2014-09-03", "2014-09-04", "2014-09-05", "2014-09-07", "2014-09-08", "2014-09-09", "2014-09-10", "2014-09-11", "2014-09-12", "2014-09-13", "2014-09-14", "2014-09-15", "2014-09-16", "2014-09-17", "2014-09-19", "2014-09-20", "2014-09-21", "2014-09-22", "2014-09-23", "2014-09-25", "2014-09-26", "2014-09-27", "2014-09-28", "2014-09-29", "2014-10-01", "2014-10-03", "2014-10-04", "2014-10-05", "2014-10-07", "2014-10-08", "2014-10-09", "2014-10-10", "2014-10-11", "2014-10-12", "2014-10-13", "2014-10-14", "2014-10-15", "2014-10-16", "2014-10-17", "2014-10-18", "2014-10-19", "2014-10-20", "2014-10-21", "2014-10-22", "2014-10-23", "2014-10-24", "2014-10-25", "2014-10-26", "2014-10-27", "2014-10-28", "2014-10-29", "2014-10-30", "2014-10-31", "2014-11-01", "2014-11-02", "2014-11-03", "2014-11-04", "2014-11-05", "2014-11-06", "2014-11-07", "2014-11-08", "2014-11-09", "2014-11-10", "2014-11-11", "2014-11-12", "2014-11-13", "2014-11-14", "2014-11-15", "2014-11-16", "2014-11-17", "2014-11-18", "2014-11-19", "2014-11-20", "2014-11-21", "2014-11-22", "2014-11-23", "2014-11-24", "2014-11-25", "2014-11-26", "2014-11-27", "2014-11-28", "2014-11-29", "2014-11-30", "2014-12-01", "2014-12-02", "2014-12-03", "2014-12-04", "2014-12-05", "2014-12-06", "2014-12-07", "2014-12-08", "2014-12-09", "2014-12-10", "2014-12-11", "2014-12-12", "2014-12-13", "2014-12-14", "2014-12-15", "2014-12-16", "2014-12-17", "2014-12-18", "2014-12-19", "2014-12-20", "2014-12-21", "2014-12-22", "2014-12-23", "2014-12-24", "2014-12-25", "2014-12-26", "2014-12-27", "2014-12-28", "2014-12-29", "2014-12-30", "2014-12-31", "2015-01-01", "2015-01-02", "2015-01-03", "2015-01-04", "2015-01-05", "2015-01-06", "2015-01-07", "2015-01-08", "2015-01-09", "2015-01-10", "2015-01-11", "2015-01-12", "2015-01-13", "2015-01-14", "2015-01-15", "2015-01-16", "2015-01-17", "2015-01-18", "2015-01-19", "2015-01-20", "2015-01-21", "2015-01-22", "2015-01-23", "2015-01-24", "2015-01-25", "2015-01-26", "2015-01-27", "2015-01-28", "2015-01-29", "2015-01-30", "2015-01-31", "2015-02-01", "2015-02-02", "2015-02-03", "2015-02-04", "2015-02-05", "2015-02-06", "2015-02-07", "2015-02-08", "2015-02-09", "2015-02-10", "2015-02-11", "2015-02-12", "2015-02-13", "2015-02-14", "2015-02-15", "2015-02-16", "2015-02-17", "2015-02-18", "2015-02-19", "2015-02-20", "2015-02-21", "2015-02-22", "2015-02-23", "2015-02-24", "2015-02-25", "2015-02-26", "2015-02-27", "2015-02-28", "2015-03-01", "2015-03-02", "2015-03-03", "2015-03-04", "2015-03-05", "2015-03-06", "2015-03-07", "2015-03-08", "2015-03-09", "2015-03-10", "2015-03-11", "2015-03-12", "2015-03-13", "2015-03-14", "2015-03-15", "2015-03-16", "2015-03-17", "2015-03-18", "2015-03-19", "2015-03-20", "2015-03-21", "2015-03-22", "2015-03-23", "2015-03-24", "2015-03-25", "2015-03-26", "2015-03-27", "2015-03-28", "2015-03-29", "2015-03-30", "2015-03-31", "2015-04-01", "2015-04-02", "2015-04-03", "2015-04-04", "2015-04-05", "2015-04-06", "2015-04-07", "2015-04-08", "2015-04-09", "2015-04-10", "2015-04-11", "2015-04-12", "2015-04-13", "2015-04-14", "2015-04-15", "2015-04-16", "2015-04-17", "2015-04-18", "2015-04-19", "2015-04-20", "2015-04-21", "2015-04-22", "2015-04-23", "2015-04-24", "2015-04-25", "2015-04-26", "2015-04-27", "2015-04-28", "2015-04-29", "2015-04-30"};

        for (String d : ds) {
            if (Dates.testParse(d) != null) {
                Assert.assertTrue(Dates.match(d, "yyyy-MM-dd"));
            } else {
                Assert.assertFalse(Dates.match(d, "yyyy-MM-dd"));
            }
        }
    }

    @Test
    public void testToStringDate() {
        Date date = Dates.parse("2018-05-17 15:58:59");
        Assert.assertEquals("2018-05-17 15:58:59", StringUtils.toString(date));

        date = Dates.parse("2018-05-17 15:58:59:600");
        Assert.assertEquals("2018-05-17 15:58:59:600", StringUtils.toString(date));

        date = Dates.parse("2018-05-17 15:58");
        Assert.assertEquals("2018-05-17 15:58:00", Dates.format19(date));
    }

    @Test
    public void testformat10() {
        Assert.assertNull(Dates.format17(null));
        Assert.assertEquals("20191121012345006", Dates.format17(Dates.parse("2019-11-21 01:23:45:6")));
        Assert.assertEquals("20191121012345066", Dates.format17(Dates.parse("2019-11-21 01:23:45:66")));
        Assert.assertEquals("20191121012345669", Dates.format17(Dates.parse("2019-11-21 01:23:45:669")));
    }

    @Test
    public void testtestFormatObject() {
        Assert.assertNull(Dates.testParse(null));
        Assert.assertNull(Dates.testParse(""));
        Assert.assertNull(Dates.testParse("tst"));
        Assert.assertNotNull(Dates.testParse("2019-11-21 01:23:45:669"));
    }

    @Test
    public void testIsTimeCharArray() {
        Assert.assertTrue(Dates.isTime("123456".toCharArray()));

        Assert.assertFalse(Dates.isTime("123460".toCharArray()));
        Assert.assertFalse(Dates.isTime("A23456".toCharArray()));
        Assert.assertFalse(Dates.isTime("1A3456".toCharArray()));
        Assert.assertFalse(Dates.isTime("12a456".toCharArray()));
        Assert.assertFalse(Dates.isTime("127456".toCharArray()));
        Assert.assertFalse(Dates.isTime("126056".toCharArray()));

        Assert.assertFalse(Dates.isTime("243456".toCharArray()));
        Assert.assertFalse(Dates.isTime("253456".toCharArray()));
        Assert.assertFalse(Dates.isTime("2A3456".toCharArray()));

        Assert.assertFalse(Dates.isTime("123460".toCharArray()));
        Assert.assertFalse(Dates.isTime("1234-1".toCharArray()));
    }

    @Test
    public void testisStandTimeFormatCharArray() {
        Assert.assertTrue(Dates.isTime("12:34:56"));
        Assert.assertTrue(Dates.isTime("1:2:3"));
        Assert.assertTrue(Dates.isTime("1:20:3"));
        Assert.assertTrue(Dates.isTime("1:2:34"));
        Assert.assertTrue(Dates.isTime("12:34:56:789"));
        Assert.assertTrue(Dates.isTime("12:34:56:7"));
        Assert.assertTrue(Dates.isTime("12:34:56:78"));
    }

    @Test
    public void test100() {
        Assert.assertEquals(31, Dates.calcDay("2021-01-01", "2021-02-01"));
    }

    @Test
    public void test101() {
        List<String> list = Dates.getEndOfMonth("2021-01-01", "2022-02-01");
        Assert.assertEquals(13, list.size());
        Assert.assertEquals("2021-01-31", list.get(0));
        Assert.assertEquals("2022-01-31", list.get(list.size() - 1));
    }

    @Test
    public void test102() {
        List<String> list = Dates.tolist("2021-01-01", "2021-02-01");
        Assert.assertEquals(32, list.size());
        Assert.assertEquals("2021-01-01", list.get(0));
        Assert.assertEquals("2021-02-01", list.get(list.size() - 1));
    }

    @Test
    public void test103() {
        Assert.assertEquals("yyyyMMdd", Dates.pattern("19981225"));
        Assert.assertEquals("yyyy-MM-dd", Dates.pattern("1998-12-25"));
        Assert.assertEquals("MM/dd/yyyy", Dates.pattern("12/25/1998"));
        Assert.assertEquals("yyyyMMddHH", Dates.pattern("1998122500"));
        Assert.assertEquals("yyyyMMddHHmm", Dates.pattern("199812250000"));
        Assert.assertEquals("yyyyMMddHHmmss", Dates.pattern("19981225000000"));
        Assert.assertEquals("yyyyMMddHHmmssSSS", Dates.pattern("19981225000000000"));
        Assert.assertEquals("yyyy-MM-dd HH", Dates.pattern("1998-12-25 12"));
        Assert.assertEquals("yyyy-MM-dd HH:mm", Dates.pattern("1998-12-25 12:12"));
        Assert.assertEquals("yyyy-MM-dd HH:mm:ss", Dates.pattern("1998-12-25 12:12:12"));
        Assert.assertEquals("yyyy-MM-dd HH:mm:ss:SS", Dates.pattern("1998-12-25 12:12:12:99"));
        Assert.assertEquals("yyyy年MM月dd日", Dates.pattern("1998年12月25日"));
        Assert.assertEquals("yyyy年M月dd日", Dates.pattern("1998年1月25日"));
        Assert.assertEquals("dd MMM yyyy", Dates.pattern("31 dec 2017"));
        Assert.assertEquals("dd MMMM yyyy 'at' HH:mm:ss", Dates.pattern("31 december 2017 at 08:38:00"));
        Assert.assertEquals("E MMM dd HH:mm:ss zzz yyyy", Dates.pattern("Thu Mar 21 06:14:26 UTC 2019"));
        Assert.assertEquals("yyyy'年' MM'月' dd'日' E HH:mm:ss zzz", Dates.pattern("2019年 03月 21日 星期四 06:13:57 UTC"));
//        Assert.assertEquals("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Dates.pattern("Sun Oct 11 00:00:00 GMT+08:00 1998"));
    }

    @Test
    public void test104() {
        Assert.assertEquals("19981225", Dates.format08("1998-12-25"));
        Assert.assertEquals(Dates.format08(Dates.calcDay(new Date(), -1)), Dates.format08(-1));
        Assert.assertEquals(Dates.format10(Dates.calcDay(new Date(), -1)), Dates.format10(-1));
    }

    @Test
    public void testRestDay() throws IOException {
        // 加载用户根目录下的 holidays.xml 配置文件进行测试
        File home = NationalHolidays.HOLIDAY_CONFIG_DIR;
        FileUtils.createDirectory(home);
        FileUtils.write(new File(home, "holidays.xml"), CharsetName.UTF_8, false, ClassUtils.getClassLoader().getResourceAsStream("homeFile/holidays.xml"));

        // 加载用户自定义目录下的国家法定假日配置文件
        File userDefine = new File(FileUtils.joinPath(home.getAbsolutePath(), "userDefine"));
        FileUtils.delete(userDefine);
        FileUtils.createDirectory(userDefine);
        FileUtils.write(new File(userDefine, "holidays1.xml"), CharsetName.UTF_8, false, ClassUtils.getClassLoader().getResourceAsStream("user/holidays1.xml"));
        FileUtils.write(new File(userDefine, "holidays2.xml"), CharsetName.UTF_8, false, ClassUtils.getClassLoader().getResourceAsStream("user/holidays2.xml"));
        System.setProperty(NationalHolidays.PROPERTY_HOLIDAY, userDefine.getAbsolutePath());

        // 重新加载
        Dates.HOLIDAYS.reload();

        Assert.assertFalse(Dates.isRestDay("zh_CN", null));
        Assert.assertFalse(Dates.isRestDay("zh_CN", Dates.parse("2019-08-30")));
        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("20191001")));

        Assert.assertTrue(Dates.isWorkDay("zh_CN", Dates.parse("2019-08-30")));
        Assert.assertFalse(Dates.isWorkDay("zh_CN", Dates.parse("2019-08-31")));

        Assert.assertFalse(Dates.isRestDay("zh_CN", Dates.parse("2019-08-30")));
        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("20191001")));

        Assert.assertTrue(Dates.isWorkDay("zh_CN", Dates.parse("2019-08-30")));
        Assert.assertFalse(Dates.isWorkDay("zh_CN", Dates.parse("2019-08-31")));

        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("2019-10-01")));
        Assert.assertFalse(Dates.isWorkDay("zh_CN", Dates.parse("2019-08-31")));

        Assert.assertTrue(Dates.isWorkDay("zh_CN", Dates.parse("2017-02-04")));
        Assert.assertFalse(Dates.isWorkDay("zh_CN", Dates.parse("2017-02-05")));

        Assert.assertFalse(Dates.HOLIDAYS.get("zh_CN").getWork().isEmpty());
        Assert.assertFalse(Dates.HOLIDAYS.get("zh_CN").getRest().isEmpty());

        Assert.assertFalse(Dates.isRestDay("zh_CN", Dates.parse("2025-12-24")));
        Assert.assertFalse(Dates.isRestDay("zh_CN", Dates.parse("2025-12-25")));

        Assert.assertTrue(Dates.isRestDay("en_US", Dates.parse("2025-12-24")));
        Assert.assertTrue(Dates.isRestDay("en_US", Dates.parse("2025-12-25")));

        // 加载用户根目录下的 holidays.xml 配置文件进行测试
        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("2025-08-01")));
        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("2025-08-15")));

        // 加载用户自定义目录下的国家法定假日配置文件
        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("2025-04-18")));
        Assert.assertTrue(Dates.isRestDay("zh_CN", Dates.parse("2025-05-08")));
    }
}
