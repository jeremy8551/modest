package cn.org.expect.database.export.converter;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class FloatConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute() throws Exception {
        Float value = this.resultSet.getFloat(this.column);
        if (this.resultSet.wasNull()) {
            this.array[this.column] = "";
        } else {
            this.array[this.column] = value.toString();
        }
    }
}
