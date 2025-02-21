package cn.org.expect.database.internal;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseTableDDL;
import cn.org.expect.util.Settings;

public class StandardDatabaseTableDDL implements DatabaseTableDDL {

    private String table;
    private StandardDatabaseDDL comment;
    private StandardDatabaseDDL index;
    private StandardDatabaseDDL primarykey;

    /**
     * 初始化
     */
    public StandardDatabaseTableDDL() {
        this.comment = new StandardDatabaseDDL();
        this.index = new StandardDatabaseDDL();
        this.primarykey = new StandardDatabaseDDL();
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return this.table;
    }

    public DatabaseDDL getComment() {
        return this.comment;
    }

    public DatabaseDDL getIndex() {
        return this.index;
    }

    public DatabaseDDL getPrimaryKey() {
        return this.primarykey;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.table).append(';').append(Settings.getLineSeparator());

        for (String ddl : this.primarykey) {
            buf.append(ddl).append(';').append(Settings.getLineSeparator());
        }

        for (String ddl : this.index) {
            buf.append(ddl).append(';').append(Settings.getLineSeparator());
        }

        for (String ddl : this.comment) {
            buf.append(ddl).append(';').append(Settings.getLineSeparator());
        }

        return buf.toString();
    }
}
