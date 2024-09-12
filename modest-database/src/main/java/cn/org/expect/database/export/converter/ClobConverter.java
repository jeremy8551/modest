package cn.org.expect.database.export.converter;

import java.io.Reader;
import java.sql.Clob;

import cn.org.expect.util.IO;

public class ClobConverter extends AbstractConverter {

    protected StringBuilder cache;

    protected char[] buffer;

    public void init() throws Exception {
        this.cache = new StringBuilder(this.contains("cacheSize") ? Integer.parseInt((String) this.getAttribute("cacheSize")) : 2048);
        this.buffer = new char[IO.READER_BUFFER_SIZE];
    }

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
                IO.read(in, this.cache, this.buffer);
                this.array[this.column] = this.cache.toString();
            }
        }
    }
}
