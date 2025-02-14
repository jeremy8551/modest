package cn.org.expect.database.load.serial;

import cn.org.expect.database.DatabaseTableDDL;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.load.DestTable;

public class RebuildTableExceptionProcessor {

    /**
     * 扫描数据文件并与目标表中字段类型进行比较，并自动扩容数据库表中字段长度
     *
     * @param dao    数据库接口
     * @param target 目标表
     * @throws Exception 重建数据库表发生错误
     */
    public void execute(JdbcDao dao, DestTable target) throws Exception {
        DatabaseTableDDL tableDDL = target.getTableDDL();
        dao.dropTable(target.getTable());
        dao.execute(tableDDL);
        dao.commit();
    }
}
