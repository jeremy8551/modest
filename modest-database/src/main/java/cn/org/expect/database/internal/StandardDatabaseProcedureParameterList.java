package cn.org.expect.database.internal;

import java.util.ArrayList;
import java.util.Collection;

import cn.org.expect.database.DatabaseProcedureParameter;
import cn.org.expect.database.DatabaseProcedureParameterList;

/**
 * 提供集合视图及其边界访问能力
 */
public class StandardDatabaseProcedureParameterList extends ArrayList<DatabaseProcedureParameter> implements DatabaseProcedureParameterList {
    private final static long serialVersionUID = 1L;

    /**
     * 初始化 StandardDatabaseProcedureParameterList
     */
    public StandardDatabaseProcedureParameterList() {
        super();
    }

    /**
     * 初始化 StandardDatabaseProcedureParameterList
     */
    public StandardDatabaseProcedureParameterList(Collection<? extends DatabaseProcedureParameter> c) {
        super(c);
    }

    /**
     * 初始化 StandardDatabaseProcedureParameterList
     */
    public StandardDatabaseProcedureParameterList(int initialCapacity) {
        super(initialCapacity);
    }

    /** {@inheritDoc} */
    public DatabaseProcedureParameterList clone() {
        int size = this.size();
        StandardDatabaseProcedureParameterList list = new StandardDatabaseProcedureParameterList(size);
        for (int i = 0; i < size; i++) {
            list.add(list.get(i).clone());
        }
        return list;
    }
}
