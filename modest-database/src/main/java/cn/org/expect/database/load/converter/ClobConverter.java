package cn.org.expect.database.load.converter;

import java.sql.Types;
import javax.sql.rowset.serial.SerialClob;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class ClobConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute(String value) throws Exception {
        if (this.notNull && this.isBlank(value)) {
            this.statement.setNull(this.position, Types.CLOB);
        } else if (value.length() == 0) { // 空字符串表示空指针
            this.statement.setNull(this.position, Types.CLOB);
        } else {
            this.statement.setClob(this.position, new SerialClob(value.toCharArray()));
        }
    }
}
