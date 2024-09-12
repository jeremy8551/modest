package cn.org.expect.database.load.converter;

import java.sql.Types;

public class ShortConverter extends AbstractConverter {

    public void init() throws Exception {
    }

    public void execute(String value) throws Exception {
        if (this.isBlank(value)) {
            this.statement.setNull(this.position, Types.SMALLINT);
        } else {
            this.statement.setShort(this.position, Short.parseShort(value));
        }
    }

}
