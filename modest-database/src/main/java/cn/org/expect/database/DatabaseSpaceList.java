package cn.org.expect.database;

import java.util.List;

public interface DatabaseSpaceList extends Cloneable, List<DatabaseSpace> {

    /**
     * 返回一个副本
     *
     * @return 副本
     */
    DatabaseSpaceList clone();
}
