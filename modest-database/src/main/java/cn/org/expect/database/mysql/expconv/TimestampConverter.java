package cn.org.expect.database.mysql.expconv;

import java.sql.Timestamp;

/**
 * 时间格式
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-04-01
 */
public class TimestampConverter extends cn.org.expect.database.export.converter.TimestampConverter {

    public void execute() throws Exception {
        Timestamp value = this.resultSet.getTimestamp(this.column);
        if (value == null) {
            this.array[this.column] = "";
        } else {
            String str = this.format.format(value);
            StringBuilder buf = new StringBuilder(str.length() + 2);
            buf.append('"');
            buf.append(str);
            buf.append('"');
            this.array[this.column] = buf.toString();
        }
    }
}
