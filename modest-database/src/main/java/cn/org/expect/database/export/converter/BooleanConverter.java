package cn.org.expect.database.export.converter;

public class BooleanConverter extends AbstractConverter {

    public void init() throws Exception {
    }

    public void execute() throws Exception {
        boolean value = this.resultSet.getBoolean(this.column);
        this.array[this.column] = value ? "true" : "false";
    }
}
