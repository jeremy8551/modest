package cn.org.expect.database.db2.expconv;

import cn.org.expect.database.db2.format.DB2FloatFormat;
import cn.org.expect.database.export.converter.AbstractConverter;

public class RealConverter extends AbstractConverter {

    /** 格式化工具 */
    private DB2FloatFormat format;

    public void init() throws Exception {
        this.format = new DB2FloatFormat();
    }

    public void execute() throws Exception {
        Float value = this.resultSet.getFloat(this.column);
        if (this.resultSet.wasNull()) {
            this.array[this.column] = "";
        } else {
            this.array[this.column] = this.format.format(value).toString();
        }
    }
}
