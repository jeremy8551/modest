package cn.org.expect.database.load.converter;

import java.sql.Types;

import cn.org.expect.util.StringUtils;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class ByteArrayConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute(String value) throws Exception {
        if (this.isBlank(value)) {
            this.statement.setNull(this.position, Types.BIT);
        } else {
            this.statement.setBytes(this.position, StringUtils.parseHexString(value));
        }
    }
}
