package cn.org.expect.database.load;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseException;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseIndexList;
import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseTableDDL;
import cn.org.expect.database.Jdbc;
import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.database.JdbcStringConverter;
import cn.org.expect.database.load.converter.AbstractConverter;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 数据库目标表，即文件中数据保存到数据库的表信息
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-06-17
 */
public class DestTable {
    private final static Log log = LogFactory.getLog(DestTable.class);

    /** 数据库操作接口 */
    private JdbcDao dao;

    /** 数据库表信息 */
    private DatabaseTable table;

    /** 数据库表的建表语句 */
    private DatabaseTableDDL ddl;

    /** 数据库批处理接口 */
    private PreparedStatement statement;

    /** 插入字段顺序集合 */
    private List<DatabaseTableColumn> tableColumns;

    /** 文件中字段读取顺序数组 */
    private int[] filePositions;

    /** 插入字段个数 */
    private int column;

    /** 实现的 merge into 语句功能 */
    private LoadMerge merge;

    /** 索引字段名集合 */
    private List<String> indexColumn;

    /** 用户映射 */
    private JdbcConverterMapper userMapper;

    /**
     * 初始化
     *
     * @param dao   数据库操作接口
     * @param table 数据库表信息
     */
    public DestTable(JdbcDao dao, DatabaseTable table) {
        this.dao = Ensure.notNull(dao);
        this.table = Ensure.notNull(table);
    }

    /**
     * 打开数据库连接
     *
     * @param context 装载引擎上下文信息
     * @throws SQLException 数据库错误
     */
    public void open(LoadEngineContext context) throws SQLException {
        LoadMode loadMode = context.getLoadMode();
        List<String> fileColumn = context.getFileColumn();
        List<String> tableColumn = context.getTableColumn(); // 查询字段名集合
        List<String> indexColumn = context.getIndexColumn();

        if (loadMode == LoadMode.MERGE) {
            this.indexColumn = this.toIndexColumns(indexColumn);
        }

        this.userMapper = context.getConverters();
        this.tableColumns = this.toTableFields(this.table, tableColumn); // 查询插入字段信息
        this.filePositions = this.toFilePositions(this.tableColumns.size(), fileColumn); // 计算文件中字段位置顺序
        this.column = Math.min(this.tableColumns.size(), this.filePositions.length); // 计算最小的字段个数
        this.tableColumns = new ArrayList<DatabaseTableColumn>(this.tableColumns.subList(0, this.column)); // 删除集合中多余的字段信息，因为SQL语句依赖字段集合
//		this.converters = this.createConverter(this.dao, this.tableColumns, this.userMapper); // 生成字段对应的类型转换器
        this.statement = this.createStatement(this.dao, this.tableColumns, this.indexColumn, loadMode); // 创建数据库批处理接口
    }

    /**
     * 检查索引字段 <br>
     * 如果未设置索引字段，则使用数据库表的主键或唯一索引作为索引字段
     *
     * @param indexColumn 索引字段名集合
     * @throws SQLException 数据库错误
     */
    private List<String> toIndexColumns(List<String> indexColumn) throws SQLException {
        if (indexColumn == null || indexColumn.isEmpty()) { // 如果未设置索引字段，则使用
            DatabaseIndexList list = this.table.getPrimaryIndexs();
            if (list.size() > 0) {
                DatabaseIndex index = list.get(0);
                indexColumn = new ArrayList<String>(index.getColumnNames());
            } else {
                DatabaseIndexList indexs = this.table.getIndexs();
                for (DatabaseIndex index : indexs) {
                    if (index.isUnique()) { // 唯一索引
                        indexColumn = new ArrayList<String>(index.getColumnNames());
                        break;
                    }
                }
            }
        }

        // 只有 merge 模式才需要索引字段
        if (indexColumn == null || indexColumn.isEmpty()) {
            throw new SQLException(ResourcesUtils.getMessage("load.standard.output.msg004"));
        } else {
            return indexColumn;
        }
    }

    /**
     * 生成文件中字段顺序数组
     *
     * @param column 默认值，如果未指定字段顺序，使用默认字段个数从1开始按顺序保存
     * @param fields 文件中字段顺序
     * @return 字段顺序数组
     */
    private int[] toFilePositions(int column, List<String> fields) {
        if (fields == null || fields.isEmpty()) { // 未指定时使用默认顺序
            int[] positions = new int[column];
            for (int i = 0; i < column; i++) {
                positions[i] = i + 1;
            }
            return positions;
        } else {
            int[] positions = new int[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                String str = fields.get(i);
                int position;
                if (StringUtils.isNumber(str) && (position = Integer.parseInt(str)) > 0) {
                    positions[i] = position;
                } else {
                    throw new IllegalArgumentException(ResourcesUtils.getMessage("load.standard.output.msg005", str));
                }
            }
            return positions;
        }
    }

    /**
     * 如果用户指定了数据库表中字段顺序，则优先按指定顺序返回数据库表字段顺序集合 <br>
     * 如果用户未指定数据库表中字段顺序，默认返回数据库表中字段顺序
     *
     * @param table       数据库表信息
     * @param tableColumn 数据库表中字段名的集合 或 字段位置的集合
     * @return 字段集合
     */
    private List<DatabaseTableColumn> toTableFields(DatabaseTable table, List<String> tableColumn) {
        List<DatabaseTableColumn> list = new ArrayList<DatabaseTableColumn>(20);
        if (tableColumn == null || tableColumn.isEmpty()) {
            list.addAll(table.getColumns());
        } else {
            // 将字段位置信息转为字段名
            for (int i = 0; i < tableColumn.size(); i++) {
                String name = tableColumn.get(i); //
                int position;
                if (StringUtils.isNumber(name) && (position = Integer.parseInt(name)) >= 1 && position <= table.columns()) {
                    DatabaseTableColumn col = table.getColumns().getColumn(position);
                    if (col == null) {
                        throw new IllegalArgumentException(ResourcesUtils.getMessage("load.standard.output.msg006", name));
                    }
                    list.add(col);
                } else {
                    String key = name.toUpperCase();
                    DatabaseTableColumn col = table.getColumns().getColumn(key);
                    if (col == null) {
                        throw new IllegalArgumentException(ResourcesUtils.getMessage("load.standard.output.msg006", name));
                    }
                    list.add(col);
                }
            }
        }
        return list;
    }

    /**
     * 生成数据库表字段对应的转换器
     *
     * @param dao        数据库操作接口
     * @param columns    数据库表的字段顺序集合
     * @param userDefine 用户自定义的转换器映射关系
     * @return 类型转换器数组
     * @throws SQLException 数据库错误
     */
    private JdbcStringConverter[] createConverter(JdbcDao dao, List<DatabaseTableColumn> columns, JdbcConverterMapper userDefine) throws SQLException {
        String[] javaClassNames = this.toJavaClassName(dao, columns); // JDBC 驱动提供的字段与JAVA类型的映射关系
        JdbcConverterMapper database = dao.getDialect().getStringConverters(); // 数据库默认映射关系
        int size = columns.size();
        JdbcStringConverter[] array = new JdbcStringConverter[size];
        for (int i = 0; i < size; i++) {
            DatabaseTableColumn column = columns.get(i);
            String fieldName = column.getName(); // 字段名
            String javaClassName = javaClassNames[i]; // java 类名

            // 查询用户自定义信息
            if (userDefine != null && userDefine.contains(fieldName)) {
                array[i] = userDefine.get(fieldName);
            } else if (userDefine != null && userDefine.contains(javaClassName)) {
                array[i] = userDefine.get(javaClassName);
            } else if (database.contains(column.getFieldType())) {
                array[i] = database.get(column.getFieldType());
            } else {
                throw new IllegalArgumentException(ResourcesUtils.getMessage("load.standard.output.msg007", fieldName, javaClassName));
            }
        }
        return array;
    }

    /**
     * 查询字段对应 java 类名
     *
     * @param dao     数据库操作接口
     * @param columns 字段插入顺序集合
     * @return Java类名数组
     * @throws SQLException 数据库错误
     */
    private String[] toJavaClassName(JdbcDao dao, List<DatabaseTableColumn> columns) throws SQLException {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String tableName = columns.get(0).getTableFullName();
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            sql.append(it.next().getName());
            if (it.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(" from ");
        sql.append(tableName);

        JdbcQueryStatement query = null;
        try {
            query = dao.query(sql.toString());
            return Jdbc.getColumnClassName(query.getResultSet());
        } catch (Throwable e) {
            if (dao.getDialect().isRebuildTableException(e)) {
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("load.standard.output.msg008", tableName));
                }

                DatabaseTableDDL ddl = this.getTableDDL();
                dao.dropTable(this.table); // 先删除数据库表
                dao.execute(ddl); // 执行数据库建表语句
                dao.commit();
                return this.toJavaClassName(dao, columns);
            } else {
                throw new DatabaseException(tableName, e);
            }
        } finally {
            IO.close(query);
        }
    }

    /**
     * 生成 JDBC 批处理程序接口
     *
     * @param dao         数据库操作接口
     * @param columns     字段插入顺序集合
     * @param indexColumn 索引字段名
     * @param mode        数据插入方式
     * @return 批处理接口
     * @throws SQLException 数据库错误
     */
    private PreparedStatement createStatement(JdbcDao dao, List<DatabaseTableColumn> columns, List<String> indexColumn, LoadMode mode) throws SQLException {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String fullTableName = columns.get(0).getTableFullName();
        String sql = "";
        if (mode == LoadMode.REPLACE || mode == LoadMode.INSERT) {
            sql = Jdbc.toInsertStatement(fullTableName, columns);
        } else if (mode == LoadMode.MERGE) {
            DatabaseDialect dialect = dao.getDialect();
            if (dialect.supportedMergeStatement()) {
                sql = dialect.toMergeStatement(fullTableName, columns, indexColumn);
            } else {
                this.merge = new LoadMerge(dao, this, indexColumn, columns);
                sql = Jdbc.toInsertStatement(this.merge.getTempTable().getFullName(), columns);
            }
        }

        // 打印 SQL 语句
        if (log.isDebugEnabled()) {
            log.debug(sql);
        }
        return dao.getConnection().prepareStatement(sql);
    }

    /**
     * 返回字段分别对应的类型转换器
     *
     * @return 类型转换器数组
     * @throws Exception 类型转换器初始化发生错误
     */
    public JdbcStringConverter[] getConverters() throws Exception {
        JdbcStringConverter[] converters = this.createConverter(this.dao, this.tableColumns, this.userMapper);

        for (int i = 0; i < converters.length; i++) { // 计算字段的类型转换器并执行初始化
            DatabaseTableColumn column = this.tableColumns.get(i); // 对应的字段信息
            String name = column.getName(); // 字段名
            String nullEnable = column.getNullAble(); // 字段不能为null
            int position = i + 1; // 字段在查询结果集中的位置

            // 初始化一个类型转换器
            JdbcStringConverter obj = converters[i];
            obj.setAttribute(AbstractConverter.STATEMENT, this.statement); // 保存数据库处理接口
            obj.setAttribute(AbstractConverter.POSITION, position); // 字段位置
            obj.setAttribute(AbstractConverter.COLUMNNAME, name); // 字段名
            obj.setAttribute(AbstractConverter.COLUMNSIZE, converters.length); // 加载字段个数
            obj.setAttribute(AbstractConverter.ISNOTNULL, "NO".equalsIgnoreCase(nullEnable)); // 字段是否可以为null
            obj.init();
        }
        return converters;
    }

    /**
     * 返回数据库表信息
     *
     * @return 数据库表信息
     */
    public DatabaseTable getTable() {
        return table;
    }

    /**
     * 返回数据库表的 DDL 信息
     *
     * @return 数据库表的 DDL 信息
     * @throws SQLException 数据库错误
     */
    public DatabaseTableDDL getTableDDL() throws SQLException {
        if (this.ddl == null) {
            this.ddl = this.dao.toDDL(this.table);
        }
        return ddl;
    }

    /**
     * 返回数据源中字段顺序
     *
     * @return 数据源中字段顺序
     */
    public int[] getFilePositions() {
        return this.filePositions;
    }

    /**
     * 返回 jdbc 插入接口
     *
     * @return jdbc 插入接口
     */
    public PreparedStatement getStatement() {
        return this.statement;
    }

    /**
     * 返回数据库表中字段集合，按插入先后顺序排序
     *
     * @return 数据库表中字段集合
     */
    public List<DatabaseTableColumn> getTableColumns() {
        return Collections.unmodifiableList(this.tableColumns);
    }

    /**
     * 返回插入字段个数
     *
     * @return 字段个数
     */
    public int getColumn() {
        return column;
    }

    /**
     * 释放资源
     *
     * @throws SQLException 数据库错误
     */
    public void close() throws SQLException {
        if (this.merge != null) {
            this.merge.removeTempTable();
        }
    }

}