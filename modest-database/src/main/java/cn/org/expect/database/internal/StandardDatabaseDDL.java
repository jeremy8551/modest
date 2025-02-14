package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Collection;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

public class StandardDatabaseDDL extends ArrayList<String> implements DatabaseDDL {
    private final static long serialVersionUID = 1L;

    public StandardDatabaseDDL() {
        super();
    }

    public StandardDatabaseDDL(Collection<? extends String> c) {
        super(c);
    }

    public StandardDatabaseDDL(int initialCapacity) {
        super(initialCapacity);
    }

    public DatabaseDDL clone() {
        return new StandardDatabaseDDL(this);
    }

    public String toString() {
        return StringUtils.join(this, String.valueOf(Settings.LINE_SEPARATOR));
    }
}
