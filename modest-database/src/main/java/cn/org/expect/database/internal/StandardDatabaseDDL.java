package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Collection;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 提供数据库 DDL 语句生成与执行的默认实现
 */
public class StandardDatabaseDDL extends ArrayList<String> implements DatabaseDDL {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化 StandardDatabaseDDL
     */
    public StandardDatabaseDDL() {
        super();
    }

    /**
     * 初始化 StandardDatabaseDDL
     */
    public StandardDatabaseDDL(Collection<? extends String> c) {
        super(c);
    }

    /**
     * 初始化 StandardDatabaseDDL
     */
    public StandardDatabaseDDL(int initialCapacity) {
        super(initialCapacity);
    }

    /** {@inheritDoc} */
    public DatabaseDDL clone() {
        return new StandardDatabaseDDL(this);
    }

    /** {@inheritDoc} */
    public String toString() {
        return StringUtils.join(this, String.valueOf(Settings.getLineSeparator()));
    }
}
