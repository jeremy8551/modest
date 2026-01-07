package cn.org.expect.database.load.converter;

import java.sql.Types;

import cn.org.expect.util.StringUtils;

public class ByteArrayConverter extends AbstractConverter {

    public void init() throws Exception {
    }

    public void execute(String value) throws Exception {
        if (this.isBlank(value)) {
            this.statement.setNull(this.position, Types.BIT);
        } else {
            this.statement.setBytes(this.position, StringUtils.parseHexString(value));
        }
    }
}
