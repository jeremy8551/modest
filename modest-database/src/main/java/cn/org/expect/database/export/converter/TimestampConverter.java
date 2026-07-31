package cn.org.expect.database.export.converter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import cn.org.expect.util.StringUtils;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class TimestampConverter extends DateConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
        String pattern = StringUtils.coalesce((String) this.getAttribute(PARAM_TIMESTAMPFORMAT), "yyyy-MM-dd hh:mm:ss.SSSSSS");
        this.format = new SimpleDateFormat(pattern);
    }

    /** {@inheritDoc} */
    public void execute() throws Exception {
        Timestamp value = this.resultSet.getTimestamp(this.column);
        if (value == null) {
            this.array[this.column] = "";
        } else {
            this.array[this.column] = this.format.format(value);
        }
    }
}
