package cn.org.expect.database.load.converter;

import java.sql.Types;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class FloatConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute(String value) throws Exception {
        if (this.isBlank(value)) {
            this.statement.setNull(this.position, Types.FLOAT);
        } else {
            this.statement.setFloat(this.position, new Float(value));
        }
    }
}
