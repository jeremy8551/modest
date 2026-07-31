package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Collection;

import cn.org.expect.database.DatabaseSpace;
import cn.org.expect.database.DatabaseSpaceList;

/**
 * 提供集合视图及其边界访问能力
 */
public class StandardDatabaseSpaceList extends ArrayList<DatabaseSpace> implements DatabaseSpaceList {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化 StandardDatabaseSpaceList
     */
    public StandardDatabaseSpaceList() {
        super();
    }

    /**
     * 初始化 StandardDatabaseSpaceList
     */
    public StandardDatabaseSpaceList(Collection<? extends DatabaseSpace> c) {
        super(c);
    }

    /**
     * 初始化 StandardDatabaseSpaceList
     */
    public StandardDatabaseSpaceList(int initialCapacity) {
        super(initialCapacity);
    }

    /** {@inheritDoc} */
    public DatabaseSpaceList clone() {
        StandardDatabaseSpaceList list = new StandardDatabaseSpaceList(this.size());
        for (int i = 0; i < this.size(); i++) {
            DatabaseSpace obj = this.get(i);
            list.add(obj == null ? null : obj.clone());
        }
        return list;
    }
}
