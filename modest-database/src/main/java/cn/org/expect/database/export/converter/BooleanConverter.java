package cn.org.expect.database.export.converter;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class BooleanConverter extends AbstractConverter {

    /** {@inheritDoc} */
    public void init() throws Exception {
    }

    /** {@inheritDoc} */
    public void execute() throws Exception {
        boolean value = this.resultSet.getBoolean(this.column);
        this.array[this.column] = value ? "true" : "false";
    }
}
