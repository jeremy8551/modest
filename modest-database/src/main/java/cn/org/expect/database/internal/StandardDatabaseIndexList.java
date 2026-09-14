package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Collection;

import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseIndexList;
import cn.org.expect.util.Ensure;

/**
 * 提供集合视图及其边界访问能力
 */
public class StandardDatabaseIndexList extends ArrayList<DatabaseIndex> implements DatabaseIndexList {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化 StandardDatabaseIndexList
     */
    public StandardDatabaseIndexList() {
        super();
    }

    /**
     * 初始化 StandardDatabaseIndexList
     */
    public StandardDatabaseIndexList(Collection<? extends DatabaseIndex> c) {
        super(c);
    }

    /**
     * 初始化 StandardDatabaseIndexList
     */
    public StandardDatabaseIndexList(int initialCapacity) {
        super(initialCapacity);
    }

    /** {@inheritDoc} */
    public DatabaseIndexList clone() {
        StandardDatabaseIndexList list = new StandardDatabaseIndexList(this.size());
        for (int i = 0; i < this.size(); i++) {
            DatabaseIndex obj = this.get(i);
            list.add(obj == null ? null : obj.clone());
        }
        return list;
    }

    /** {@inheritDoc} */
    public boolean contains(DatabaseIndex index, boolean ignoreIndexName, boolean ignoreIndexSort) {
        Ensure.notNull(index);

        for (DatabaseIndex idx : this) {
            if (idx.equals(index, ignoreIndexName, ignoreIndexSort)) {
                return true;
            }
        }
        return false;
    }
}
