package cn.org.expect.database.load;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.Jdbc;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.internal.StandardDatabaseIndex;
import cn.org.expect.database.internal.StandardDatabaseTable;
import cn.org.expect.expression.DefaultAnalysis;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * 当数据库不支持 merge into 语句时，通过 update 与 insert 语句方式实现 merge into 语句功能 <br>
 * 实现方式： <br>
 * 1. 新建一个临时表，表结构与目标表一致 <br>
 * 2. 将数据批量插入并保存到临时表中 <br>
 * 3. 在临时表上建立索引 <br>
 * 4. 将临时表中与目标表中索引相同的数据更新到目标表中 <br>
 * 5. 将临时表中的增量数据保存到目标表中 <br>
 * 6. 删除临时表及其数据与索引 <br>
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-09-02
 */
public class LoadMerge {

    /** 数据库操作接口 */
    private JdbcDao dao;

    /** 目标表 */
    private DestTable target;

    /** 索引字段 */
    private List<String> indexColumn;

    /** 目标表字段集合 */
    private List<DatabaseTableColumn> columns;

    /** 更新语句 */
    private String updateSQL;

    /** 插入语句 */
    private String insertSQL;

    /** 临时表上的索引信息 */
    private StandardDatabaseIndex index;

    /** 数据库表信息 */
    private DatabaseTable table;

    /** 临时表信息 */
    private StandardDatabaseTable tempTable;

    /**
     * 初始化
     *
     * @param dao         数据库操作接口
     * @param target      目标数据库表信息
     * @param indexColumn 索引字段集合
     * @param columns     字段集合
     * @throws SQLException 数据库错误
     */
    public LoadMerge(JdbcDao dao, DestTable target, List<String> indexColumn, List<DatabaseTableColumn> columns) throws SQLException {
        this.dao = dao;
        this.target = target;
        this.indexColumn = indexColumn;
        this.columns = columns;

        this.table = target.getTable();
        this.tempTable = this.createTemp();
        this.index = this.toIndex();
        this.updateSQL = this.toUpdateSQL();
        this.insertSQL = this.toInsertSQL();
    }

    /**
     * 返回临时表信息
     *
     * @return 数据库表信息
     */
    public DatabaseTable getTempTable() {
        return this.tempTable;
    }

    /**
     * 删除临时表
     *
     * @throws SQLException 数据库错误
     */
    public void removeTempTable() throws SQLException {
        DatabaseDialect dialect = this.dao.getDialect();
        DatabaseDDL ddl = dialect.toDDL(this.dao.getConnection(), this.index, false);
        this.dao.execute(ddl);

        // 重组索引
        List<DatabaseIndex> list = new ArrayList<DatabaseIndex>();
        list.add(this.index);
        dialect.reorgRunstatsIndexs(this.dao.getConnection(), list);

        // 更新已有数据与保存新增数据
        this.dao.executeUpdate(this.updateSQL);
        this.dao.executeUpdate(this.insertSQL);

        // 删除数据库表信息
        this.dao.dropTable(this.tempTable);
    }

    /**
     * 创建临时表
     *
     * @return 数据库表信息
     * @throws SQLException 数据库错误
     */
    private StandardDatabaseTable createTemp() throws SQLException {
        String newTableName = Jdbc.getTableNameNoRepeat(this.dao.getConnection(), this.dao.getDialect(), this.table.getCatalog(), this.table.getSchema(), this.table.getName());

        StandardDatabaseTable newtable = new StandardDatabaseTable(this.target.getTable());
        newtable.setName(newTableName);
        newtable.setFullName(this.dao.getDialect().toTableName(newtable.getCatalog(), newtable.getSchema(), newTableName));

        String tableDDL = this.target.getTableDDL().getTable();
        DefaultAnalysis analysis = new DefaultAnalysis();
        int[] indexs = analysis.indexOf(tableDDL, new String[]{"create", "table"}, 0);
        if (indexs == null) {
            throw new SQLException(tableDDL);
        }

        int begin = analysis.indexOf(tableDDL, this.table.getName(), indexs[1], 0, 0);
        if (begin == -1) {
            throw new SQLException(tableDDL);
        }

        String newTableDDL = StringUtils.replace(tableDDL, begin, this.table.getName().length(), newTableName);
        this.dao.execute(newTableDDL); // 建立临时表
        return newtable;
    }

    /**
     * 生成临时表索引信息
     *
     * @return 索引信息
     */
    private StandardDatabaseIndex toIndex() {
        DatabaseDialect dialect = this.dao.getDialect();

        // 索引字段的位置信息
        List<Integer> positions = new ArrayList<Integer>(this.indexColumn.size());
        for (int i = 1; i <= this.indexColumn.size(); i++) {
            positions.add(new Integer(i));
        }

        // 索引字段的排序规则
        ArrayList<Integer> sorts = ArrayUtils.asList(new Integer[this.indexColumn.size()]);
        Collections.fill(sorts, DatabaseIndex.INDEX_ASC);

        // 创建索引
        StandardDatabaseIndex index = new StandardDatabaseIndex();
        index.setName(this.tempTable.getName() + "IDX");
        index.setSchema(this.tempTable.getSchema());
        index.setTableCatalog(this.tempTable.getCatalog());
        index.setTableSchema(this.tempTable.getSchema());
        index.setTableName(this.tempTable.getName());
        index.setUnique(false);
        index.setTableFullName(dialect.toTableName(index.getTableCatalog(), index.getTableSchema(), index.getTableName()));
        index.setFullName(dialect.toIndexName(index.getTableCatalog(), index.getSchema(), index.getName()));
        index.setColumnNames(this.indexColumn);
        index.setPositions(positions);
        index.setSort(sorts);
        return index;
    }

    /**
     * 生成 update 更新语句
     *
     * @return SQL语句
     */
    private String toUpdateSQL() {
        String tableName = this.table.getFullName();
        String sql = "update " + tableName + " a set (" + Settings.LINE_SEPARATOR;
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            sql += "    " + col.getName() + (it.hasNext() ? "," : "") + Settings.LINE_SEPARATOR;
        }

        sql += ") = ( select " + Settings.LINE_SEPARATOR;
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            sql += "    " + col.getName() + (it.hasNext() ? "," : "") + Settings.LINE_SEPARATOR;
        }

        String newtableName = this.tempTable.getFullName();
        sql += " from " + newtableName + " b where " + Settings.LINE_SEPARATOR;
        boolean value = false;
        List<String> indexColumn = this.indexColumn;
        for (Iterator<String> it = indexColumn.iterator(); it.hasNext(); ) {
            String name = it.next();
            if (value) {
                sql += " and ";
            }
            sql += "a." + name + " = b." + name + Settings.LINE_SEPARATOR;
            value = true;
        }
        sql += ") where exists (" + Settings.LINE_SEPARATOR;
        sql += "select 1 from " + newtableName + " b where " + Settings.LINE_SEPARATOR;

        value = false;
        for (Iterator<String> it = indexColumn.iterator(); it.hasNext(); ) {
            String name = it.next();
            if (value) {
                sql += " and ";
            }
            sql += "a." + name + " = b." + name + Settings.LINE_SEPARATOR;
            value = true;
        }
        sql += ")";
        return sql;
    }

    /**
     * 生成 insert into 语句
     *
     * @return insert into 语句
     */
    private String toInsertSQL() {
        String tableName = this.table.getFullName();
        String sql = "insert into " + tableName + " (" + Settings.LINE_SEPARATOR;
        for (Iterator<DatabaseTableColumn> it = this.columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            sql += "    " + col.getName() + (it.hasNext() ? "," : "") + Settings.LINE_SEPARATOR;
        }

        sql += ") select " + Settings.LINE_SEPARATOR;
        for (Iterator<DatabaseTableColumn> it = this.columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            sql += "    " + col.getName() + (it.hasNext() ? "," : "") + Settings.LINE_SEPARATOR;
        }

        String newtableName = this.tempTable.getFullName();
        sql += " from " + newtableName + " a " + Settings.LINE_SEPARATOR;
        sql += " where not exists (" + Settings.LINE_SEPARATOR;
        sql += "select 1 from " + tableName + " b where " + Settings.LINE_SEPARATOR;

        boolean value = false;
        List<String> indexColumn = this.indexColumn;
        for (Iterator<String> it = indexColumn.iterator(); it.hasNext(); ) {
            String name = it.next();
            if (value) {
                sql += " and ";
            }
            sql += "a." + name + " = b." + name + Settings.LINE_SEPARATOR;
            value = true;
        }
        sql += ")";
        return sql;
    }
}
