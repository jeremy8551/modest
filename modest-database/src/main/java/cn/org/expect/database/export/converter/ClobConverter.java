package cn.org.expect.database.export.converter;

import java.io.Reader;
import java.sql.Clob;

import cn.org.expect.util.IO;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class ClobConverter extends AbstractConverter {

    protected StringBuilder cache;

    /** {@inheritDoc} */
    public void init() throws Exception {
        this.cache = new StringBuilder(this.contains("cacheSize") ? Integer.parseInt((String) this.getAttribute("cacheSize")) : 2048);
    }

    /** {@inheritDoc} */
    public void execute() throws Exception {
        Clob value = this.resultSet.getClob(this.column);
        if (value == null) {
            this.array[this.column] = "";
        } else {
            Reader in = value.getCharacterStream();
            if (in == null) {
                this.array[this.column] = "";
            } else {
                this.cache.setLength(0);
                IO.read(in, this.cache);
                this.array[this.column] = this.cache.toString();
            }
        }
    }
}
