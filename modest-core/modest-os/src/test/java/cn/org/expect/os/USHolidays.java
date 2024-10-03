package cn.org.expect.os;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cn.org.expect.cn.NationalHoliday;

// 不要配置 EasyBean 注解
public class USHolidays implements NationalHoliday {

    private Set<Date> rest = new HashSet<Date>();

    private Set<Date> work = new HashSet<Date>();

    public USHolidays() {
        super();
        this.init();
    }

    private void init() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            this.work.add(format.parse("2021-12-24"));
            this.work.add(format.parse("2021-12-25"));
            this.work.add(format.parse("2021-12-26"));
            this.work.add(format.parse("2021-12-27"));
            this.work.add(format.parse("2021-12-28"));
        } catch (ParseException e) {
            throw new RuntimeException("Format String!", e);
        }
    }

    public Set<Date> getRestDays() {
        return rest;
    }

    public Set<Date> getWorkDays() {
        return work;
    }

    public boolean isRestDay(Date date) {
        return false;
    }

    public boolean isWorkDay(Date date) {
        return false;
    }
}
