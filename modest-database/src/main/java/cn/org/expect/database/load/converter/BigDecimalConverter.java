package cn.org.expect.database.load.converter;

import java.math.BigDecimal;
import java.sql.Types;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class BigDecimalConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute(String value) throws Exception {
        if (this.isBlank(value)) {
            this.statement.setNull(this.position, Types.DECIMAL);
        } else {
            this.statement.setBigDecimal(this.position, new BigDecimal(value));
        }
    }
}
