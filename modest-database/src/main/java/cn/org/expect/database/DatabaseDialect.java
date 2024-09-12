package cn.org.expect.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 数据库方言接口
 *
 * @author jeremy8551@qq.com
 * @createtime 2012-03-06
 */
public interface DatabaseDialect {

    /**
     * 返回数据库的大版本号，实现数据库不同版本，对应不同的方言类
     *
     * @return 大版本号
     */
    String getDatabaseMajorVersion();

    /**
     * 返回数据库的小版本号，实现数据库不同版本，对应不同的方言类
     *
     * @return 小版本号
     */
    String getDatabaseMinorVersion();

    /**
     * 转为表全名
     *
     * @param catalog   类别名称（非必填）
     * @param schema    模式名（非必填）
     * @param tableName 表名（必填）
     * @return 表全名
     */
    String toTableName(String catalog, String schema, String tableName);

    /**
     * 转为索引名
     *
     * @param catalog   类别名称（非必填）
     * @param schema    模式名（非必填）
     * @param tableName 表名（必填）
     * @return 索引名
     */
    String toIndexName(String catalog, String schema, String tableName);

    /**
     * 将数据库表转为 DDL 语句
     *
     * @param connection 数据库连接
     * @param table      数据库表信息
     * @return 数据库DDL语句
     * @throws SQLException 数据库访问错误
     */
    DatabaseTableDDL toDDL(Connection connection, DatabaseTable table) throws SQLException;

    /**
     * 从数据库中查询索引的建表语句
     *
     * @param connection 数据库连接
     * @param index      索引信息
     * @param primary    true表示主键
     * @return 数据库DDL语句
     * @throws SQLException 数据库访问错误
     */
    DatabaseDDL toDDL(Connection connection, DatabaseIndex index, boolean primary) throws SQLException;

    /**
     * 从数据库中查询存储过程的 DDL 语句
     *
     * @param connection 数据库连接
     * @param procedure  存储过程信息
     * @return 返回DDL语句
     * @throws SQLException 数据库访问错误
     */
    DatabaseDDL toDDL(Connection connection, DatabaseProcedure procedure) throws SQLException;

    /**
     * 生成快速清空表的Sql语句 <br>
     * 如果数据库不支持快速清空表则返回delete语句
     *
     * @param connection 数据库连接
     * @param catalog    类别名称（非必填）
     * @param schema     模式名（非必填）
     * @param tableName  表名（必填）
     * @return SQL语句
     */
    String toDeleteQuicklySQL(Connection connection, String catalog, String schema, String tableName);

    /**
     * 删除数据库表的主键
     *
     * @param connection 数据库连接
     * @param index      主键信息
     * @return 删除主键语句
     * @throws SQLException 数据库访问错误
     */
    String dropPrimaryKey(Connection connection, DatabaseIndex index) throws SQLException;

    /**
     * 返回测试数据库连接是否还活着的 SQL 语句
     *
     * @return SQL语句
     */
    String getKeepAliveSQL();

    /**
     * 判断数据库是否支持修改当前连接的schema
     *
     * @return true表示支持修改
     */
    boolean supportSchema();

    /**
     * 设置数据库连接默认的SCHEMA
     *
     * @param connection 数据库连接
     * @param schema     表模式名
     * @throws SQLException 数据库访问错误
     */
    void setSchema(Connection connection, String schema) throws SQLException;

    /**
     * 返回数据库中使用的默认的模式名
     *
     * @param connection 数据库连接
     * @return 表模式
     * @throws SQLException 数据库访问错误
     */
    String getSchema(Connection connection) throws SQLException;

    /**
     * 返回数据库中使用的默认的编目名称
     *
     * @param connection 数据库连接
     * @return 编目
     * @throws SQLException 数据库访问错误
     */
    String getCatalog(Connection connection) throws SQLException;

    /**
     * 解析数据库 JDBC URL 字符串
     *
     * @param url JDBC的URL信息
     * @return URL集合
     */
    List<DatabaseURL> parseJdbcUrl(String url);

    /**
     * 如果数据系统支持Rownumber分页查询，则查询结果集的第一个记录的编号 <br>
     * 返回查询结果集第一记录的偏移位置 <br>
     *
     * @return 起始记录编号
     */
    int getRowNumberStarter();

    /**
     * 返回数据库关键字与保留字
     *
     * @param connection 数据库连接
     * @return 关键字集合
     * @throws SQLException 数据库访问错误
     */
    Set<String> getKeyword(Connection connection) throws SQLException;

    /**
     * 判断数据库中是否存在表信息
     *
     * @param connection 数据库连接
     * @param catalog    类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema     模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param tableName  表名（大小写敏感）
     * @return true表示存在表信息
     * @throws SQLException 数据库访问错误
     */
    boolean containsTable(Connection connection, String catalog, String schema, String tableName) throws SQLException;

    /**
     * 查询数据库存储过程信息
     *
     * @param connection    数据库连接
     * @param catalog       类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema        模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param procedureName 存储过程名
     * @return 存储过程集合
     * @throws SQLException 数据库访问错误
     */
    List<DatabaseProcedure> getProcedure(Connection connection, String catalog, String schema, String procedureName) throws SQLException;

    /**
     * 查询数据库存储过程信息 <br>
     * {@linkplain #getProcedure(Connection, String, String, String)} 函数只能返回唯一一个存储过程信息，如果存在多个存储过程信息则会抛出异常
     *
     * @param connection    数据库连接
     * @param catalog       类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema        模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param procedureName 存储过程名
     * @return 存储过程
     * @throws SQLException 数据库访问错误
     */
    DatabaseProcedure getProcedureForceOne(Connection connection, String catalog, String schema, String procedureName) throws SQLException;

    /**
     * 数据库表信息（包含主键、索引、列等信息）
     *
     * @param connection 数据库连接
     * @param catalog    类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema     模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param tableName  表名（大小写敏感）, 为null表示搜索schema下所有表信息
     * @return 数据库表集合
     * @throws SQLException 数据库访问错误
     */
    List<DatabaseTable> getTable(Connection connection, String catalog, String schema, String tableName) throws SQLException;

    /**
     * 返回 JDBC 类型与类型转换器（将字段值转为字符串）的映射关系
     *
     * @return 类型转换器
     */
    JdbcConverterMapper getObjectConverters();

    /**
     * 返回 JDBC 类型与类型转换器（将字符串转为 JDBC 类型）的映射关系
     *
     * @return 类型转换器
     */
    JdbcConverterMapper getStringConverters();

    /**
     * 判断异常 e 是否因为插入或更新的变量值超过数据库表字段长度错误
     *
     * @param e 异常信息
     * @return 返回true表示异常是字段超长错误 false表示异常不是字段超长错误
     */
    boolean isOverLengthException(Throwable e);

    /**
     * 判断异常 e 是否需要重新建表
     *
     * @param e 异常信息
     * @return 返回true表示异常是需要重建表 false表示异常不是需要重建表错误
     */
    boolean isRebuildTableException(Throwable e);

    /**
     * 判断异常 e 是否有主键冲突错误
     *
     * @param e 异常信息
     * @return 返回true表示主键冲突错误 false表示不是主键冲突错误
     */
    boolean isPrimaryRepeatException(Throwable e);

    /**
     * 判断异常 e 是因为要创建的索引已经存在导致报错
     *
     * @param e 异常信息
     * @return 返回true表示索引冲突错误 false表示不是索引冲突错误
     */
    boolean isIndexExistsException(Throwable e);

    /**
     * 重组索引、生成统计信息
     *
     * @param connection 数据库连接
     * @param indexs     数据库表索引集合
     * @throws SQLException 数据库访问错误
     */
    void reorgRunstatsIndexs(Connection connection, List<DatabaseIndex> indexs) throws SQLException;

    /**
     * 使用数据库命令立即关闭数据库连接（即使正在执行sql语句）, 如果数据库不支持默认使用 {@link Connection#close()} 关闭数据库连接
     *
     * @param connection 将要被关闭的数据库连接
     * @param attributes 数据库厂商定制信息
     * @return 返回true表示已终止数据库连接
     * @throws SQLException 数据库访问错误
     */
    boolean terminate(Connection connection, Properties attributes) throws SQLException;

    /**
     * 返回数据库连接中的厂商定制信息
     *
     * @param connection 数据库连接
     * @return 属性集合
     */
    Properties getAttributes(Connection connection);

    /**
     * 为了提高数据库表的插入性能所执行的操作，如：<br>
     * 降低事务隔离级别 <br>
     * 关闭数据库表上的事务日志 <br>
     * 数据的写入方式，如：简单追加写入，还是复杂算法写入 <br>
     * 使用表锁而非使用行锁 <br>
     *
     * @param dao      数据库连接
     * @param fullname 数据库表全名
     * @throws SQLException 数据库访问错误
     */
    void openLoadMode(JdbcDao dao, String fullname) throws SQLException;

    /**
     * 大批量插入数据完成
     *
     * @param dao      数据库连接
     * @param fullname 数据库表全名
     * @throws SQLException 数据库访问错误
     */
    void closeLoadMode(JdbcDao dao, String fullname) throws SQLException;

    /**
     * 大批量插入数据过程中提交事物
     *
     * @param dao      数据库连接
     * @param fullname 数据库表全名
     * @throws SQLException 数据库访问错误
     */
    void commitLoadData(JdbcDao dao, String fullname) throws SQLException;

    /**
     * 修改数据库表字段信息
     *
     * @param connection 数据库连接
     * @param oldcol     原有字段信息，为 null 时表示新增字段
     * @param newcol     变更后的字段信息，为 null 时表示删除原有字段
     * @return 返回数据库修改字段的ddl语句
     * @throws SQLException 数据库访问错误
     */
    List<String> alterTableColumn(Connection connection, DatabaseTableColumn oldcol, DatabaseTableColumn newcol) throws SQLException;

    /**
     * 支持 merge into 语句
     *
     * @return 返回true表示数据库支持merge语句 false表示不支持
     */
    boolean supportedMergeStatement();

    /**
     * 转为 merge into 语句
     *
     * @param tableName   目标表名
     * @param columns     插入字段集合
     * @param mergeColumn 表的主键或唯一索引字段名
     * @return merge语句
     */
    String toMergeStatement(String tableName, List<DatabaseTableColumn> columns, List<String> mergeColumn);

}