package cn.org.expect.database.load;

import java.sql.SQLException;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseIndexList;
import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;

/**
 * 数据库表索引的处理逻辑
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-06-16
 */
public class IndexOperation {
    private final static Log log = LogFactory.getLog(IndexOperation.class);

    /** 数据库表信息 */
    private DatabaseTable table;

    /** true 表示需要重建索引 */
    private volatile boolean rebuild;

    /**
     * 初始化
     */
    public IndexOperation(DatabaseTable table) {
        this.table = Ensure.notNull(table);
    }

    /**
     * 删除索引与主键
     *
     * @param context 装数引擎上下文信息
     * @param dao     数据库操作接口
     * @throws SQLException 数据库错误
     */
    public void before(LoadEngineContext context, JdbcDao dao) throws SQLException {
        this.rebuild = false;
        IndexMode mode = context.getIndexMode();
        if (mode == IndexMode.REBUILD || mode == IndexMode.AUTOSELECT) {
            this.rebuild = true;

            // 删除表的主键
            DatabaseIndexList list = this.table.getPrimaryIndexs();
            for (DatabaseIndex index : list) {
                dao.dropPrimaryKey(index);
            }

            // 删除表的索引
            DatabaseIndexList indexs = this.table.getIndexs();
            for (DatabaseIndex index : indexs) {
                dao.dropIndex(index);
            }

            dao.commit();
        }
    }

    /**
     * 重建索引与主键，生成统计信息
     *
     * @param context 装数引擎上下文信息
     * @param dao     数据库操作接口
     * @throws SQLException 数据库错误
     */
    public void after(LoadEngineContext context, JdbcDao dao) throws SQLException {
        if (this.rebuild) {
            // 创建主键
            DatabaseIndexList list = this.table.getPrimaryIndexs();
            for (DatabaseIndex index : list) {
                DatabaseDDL ddl = dao.toDDL(index, true);
                for (String sql : ddl) {
                    dao.tryExecute(sql, 10, 200);
                }
            }

            // 创建索引
            DatabaseIndexList indexs = this.table.getIndexs();
            for (DatabaseIndex index : indexs) {
                DatabaseDDL ddl = dao.toDDL(index, false);
                for (String sql : ddl) {
                    dao.tryExecute(sql, 10, 200);
                }
            }

            dao.commit();
        }

        // 重组索引并生成索引统计信息
        if (context.isStatistics() || this.rebuild) { // 强制生成统计信息或重建索引时执行
            dao.getDialect().reorgRunstatsIndexs(dao.getConnection(), this.table.getIndexs());
            dao.commit();
        }
    }
}
