package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Collection;

import cn.org.expect.database.DatabaseSpace;
import cn.org.expect.database.DatabaseSpaceList;

public class StandardDatabaseSpaceList extends ArrayList<DatabaseSpace> implements DatabaseSpaceList {
    private final static long serialVersionUID = 1L;

    public StandardDatabaseSpaceList() {
        super();
    }

    public StandardDatabaseSpaceList(Collection<? extends DatabaseSpace> c) {
        super(c);
    }

    public StandardDatabaseSpaceList(int initialCapacity) {
        super(initialCapacity);
    }

    public DatabaseSpaceList clone() {
        StandardDatabaseSpaceList list = new StandardDatabaseSpaceList(this.size());
        for (int i = 0; i < this.size(); i++) {
            DatabaseSpace obj = this.get(i);
            list.add(obj == null ? null : obj.clone());
        }
        return list;
    }
}
