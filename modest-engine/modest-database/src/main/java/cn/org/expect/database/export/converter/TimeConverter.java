package cn.org.expect.database.export.converter;

import java.sql.Time;
import java.text.SimpleDateFormat;

import cn.org.expect.util.StringUtils;

public class TimeConverter extends DateConverter {

    public void init() throws Exception {
        String pattern = StringUtils.defaultString((String) this.getAttribute(PARAM_TIMEFORMAT), "hh:mm:ss");
        this.format = new SimpleDateFormat(pattern);
    }

    public void execute() throws Exception {
        Time value = this.resultSet.getTime(this.column);
        if (value == null) {
            this.array[this.column] = "";
        } else {
            this.array[this.column] = this.format.format(value);
        }
    }

}
