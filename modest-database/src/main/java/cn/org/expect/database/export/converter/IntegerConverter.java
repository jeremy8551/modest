package cn.org.expect.database.export.converter;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class IntegerConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute() throws Exception {
        Integer value = this.resultSet.getInt(this.column);
        if (this.resultSet.wasNull()) {
            this.array[this.column] = "";
        } else {
            this.array[this.column] = value.toString();
        }
    }
}
