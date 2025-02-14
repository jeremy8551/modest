package cn.org.expect.database;

import java.util.List;

public interface DatabaseDDL extends Cloneable, List<String> {

    /**
     * 返回一个副本
     */
    DatabaseDDL clone();
}
