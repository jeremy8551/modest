package cn.org.expect.database.h2;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseIndexList;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.database.SQL;
import cn.org.expect.database.db2.expconv.ByteArrayConverter;
import cn.org.expect.database.db2.expconv.StringConverter;
import cn.org.expect.database.export.converter.AbstractConverter;
import cn.org.expect.database.export.converter.DateConverter;
import cn.org.expect.database.export.converter.FloatConverter;
import cn.org.expect.database.export.converter.IntegerConverter;
import cn.org.expect.database.export.converter.LongConverter;
import cn.org.expect.database.internal.AbstractDialect;
import cn.org.expect.database.internal.StandardDatabaseDDL;
import cn.org.expect.database.internal.StandardDatabaseProcedure;
import cn.org.expect.database.internal.StandardDatabaseURL;
import cn.org.expect.database.internal.StandardJdbcConverterMapper;
import cn.org.expect.io.ClobWriter;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

@EasyBean(value = "h2")
public class H2Dialect extends AbstractDialect {

    public String getKeepAliveSQL() {
        return "select 1 from DUAL";
    }

    public void setSchema(Connection connection, String schema) throws SQLException {
        connection.setSchema(schema);
    }

    public String getCatalog(Connection connection) throws SQLException {
        return connection.getCatalog();
    }

    /**
     * h2数据库URL格式：http://www.h2database.com/html/features.html#database_url
     *
     * @param url JDBC的URL信息
     * @return 数据库URL信息
     */
    public List<DatabaseURL> parseJdbcUrl(String url) {
        StandardDatabaseURL obj = new StandardDatabaseURL(url);

        String str = url;
        int indexOf = str.indexOf(';');
        if (indexOf != -1) {
            String properties = str.substring(indexOf);
            String[] propertys = StringUtils.split(properties, ';');
            for (String keyValue : propertys) {
                if (StringUtils.isNotBlank(keyValue)) {
                    String[] property = StringUtils.splitProperty(keyValue);
                    obj.setAttribute(property[0], property[1]);
                }
            }
        }

        String[] array = StringUtils.split(indexOf == -1 ? str : str.substring(0, indexOf), ":");
        Ensure.isTrue(array.length >= 4, url);
        Ensure.equals("jdbc", array[0]); // jdbc
        Ensure.equals("h2", array[1]); // jdbc

        obj.setDatabaseType("h2");

        // 嵌入模式
        if (array[2].equalsIgnoreCase("file")) {
            String name = FileUtils.getFilename(array[3]);
            obj.setDatabaseName(name);
            obj.setHostname("127.0.0.1");
            obj.setPort("9092");
        }

        // 服务模式
        else if (array[2].equalsIgnoreCase("tcp")) {
            String name = FileUtils.getFilename(str);
            if (name.indexOf(':') == -1) {
                obj.setDatabaseName(name);
            } else {
                String[] names = StringUtils.split(name, ':');
                obj.setDatabaseName(names[1]);
            }

            String[] split = StringUtils.split(str, "//");
            Ensure.isTrue(split.length == 2, str);
            String part = StringUtils.split(split[1], '/')[0];
            String[] hostPort = StringUtils.split(part, ':');
            Ensure.isTrue(hostPort.length <= 2, url);
            if (hostPort.length == 2) {
                obj.setHostname(hostPort[0]);
                obj.setPort(hostPort[1]);
            } else {
                obj.setHostname(part);
                obj.setPort("9092");
            }
        }

        // 内存模式
        else if (array[2].equalsIgnoreCase("mem")) {
            obj.setDatabaseName(array[3]);
            obj.setHostname("127.0.0.1");
            obj.setPort("9092");
        }

        // 未知 URL
        else {
            throw new UnsupportedOperationException(array[3] + " in " + url);
        }

        List<DatabaseURL> list = new ArrayList<DatabaseURL>(1);
        list.add(obj);
        return list;
    }

    public DatabaseDDL toDDL(Connection connection, DatabaseProcedure procedure) throws SQLException {
        StandardDatabaseDDL ddl = new StandardDatabaseDDL();
        JdbcQueryStatement dao = new JdbcQueryStatement(connection, "select ROUTINE_DEFINITION from INFORMATION_SCHEMA.ROUTINES where ROUTINE_NAME=? and ROUTINE_SCHEMA=? ");
        try { // 从数据库系统表中查询储存过程的源代码信息
            dao.setParameter(procedure.getName());
            dao.setParameter(procedure.getSchema());

            ResultSet resultSet = dao.query();
            if (resultSet.next()) {
                Clob value = resultSet.getClob("ROUTINE_DEFINITION");
                ddl.add(new ClobWriter(value).toString());
            }
            return ddl;
        } finally {
            dao.close();
        }
    }

    public List<DatabaseProcedure> getProcedure(Connection connection, String catalog, String schema, String procedureName) throws SQLException {
        schema = SQL.escapeQuote(schema);
        procedureName = SQL.escapeQuote(procedureName);

        String where = "";
        if (StringUtils.isNotBlank(schema)) {
            where += " and ROUTINE_SCHEMA='" + SQL.toIdentifier(schema) + "'";
        }
        if (StringUtils.isNotBlank(procedureName)) {
            if (procedureName.indexOf('%') != -1) {
                where += " and ROUTINE_NAME like '" + SQL.toIdentifier(procedureName) + "'";
            } else {
                where += " and ROUTINE_NAME = '" + SQL.toIdentifier(procedureName) + "'";
            }
        }

        List<DatabaseProcedure> list = new ArrayList<DatabaseProcedure>();
        String sql = "select * from INFORMATION_SCHEMA.ROUTINES where 1=1 " + where;
        JdbcQueryStatement dao = new JdbcQueryStatement(connection, sql);
        ResultSet resultSet = dao.query();
        while (resultSet.next()) {
            StandardDatabaseProcedure obj = new StandardDatabaseProcedure();
            obj.setCatalog(StringUtils.rtrimBlank(resultSet.getString("ROUTINE_CATALOG")));
            obj.setSchema(StringUtils.rtrimBlank(resultSet.getString("ROUTINE_SCHEMA")));
            obj.setName(StringUtils.rtrimBlank(resultSet.getString("ROUTINE_NAME")));
            obj.setFullName(this.toTableName(obj.getCatalog(), obj.getSchema(), obj.getName()));
            obj.setId(obj.getFullName());
            obj.setCreator("");
            obj.setCreatTime(null);
            obj.setLanguage(StringUtils.rtrimBlank(resultSet.getString("EXTERNAL_LANGUAGE")));
            list.add(obj);
        }
        dao.close();
        return list;
    }

    /** 数据库中字段类型与卸载处理逻辑的映射关系 */
    protected StandardJdbcConverterMapper exp;

    /** 类型映射关系 */
    protected StandardJdbcConverterMapper map;

    public JdbcConverterMapper getObjectConverters() {
        if (this.exp == null) {
            this.exp = new StandardJdbcConverterMapper();
            this.exp.add("TINYINT", IntegerConverter.class);
            this.exp.add("BIGINT", LongConverter.class);
            this.exp.add("BINARY VARYING", ByteArrayConverter.class);
            this.exp.add("BINARY", ByteArrayConverter.class);
            this.exp.add("UUID", StringConverter.class);
            this.exp.add("CHARACTER", StringConverter.class);
            this.exp.add("CHARACTER VARYING", StringConverter.class);
            this.exp.add("VARCHAR_IGNORECASE", StringConverter.class);
            this.exp.add("DECIMAL", cn.org.expect.database.export.converter.BigDecimalConverter.class);
            this.exp.add("NUMERIC", cn.org.expect.database.export.converter.BigDecimalConverter.class);
            this.exp.add("DECFLOAT", FloatConverter.class);
            this.exp.add("INTEGER", IntegerConverter.class);
            this.exp.add("SMALLINT", IntegerConverter.class);
            this.exp.add("REAL", FloatConverter.class);
            this.exp.add("DOUBLE PRECISION", cn.org.expect.database.export.converter.DoubleConverter.class);
            this.exp.add("BOOLEAN", cn.org.expect.database.export.converter.BooleanConverter.class);
            this.exp.add("DATE", DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyyMMdd");
            this.exp.add("TIME", cn.org.expect.database.export.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "hh.mm.ss");
            this.exp.add("TIMESTAMP", cn.org.expect.database.export.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd-HH.mm.ss.SSSSSS");
            this.exp.add("BINARY LARGE OBJECT", ByteArrayConverter.class);
            this.exp.add("CHARACTER LARGE OBJECT", StringConverter.class);
        }
        return this.exp;
    }

    public JdbcConverterMapper getStringConverters() {
        if (this.map == null) {
            this.map = new StandardJdbcConverterMapper();
            this.map.add("TINYINT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("BIGINT", cn.org.expect.database.load.converter.LongConverter.class);
            this.map.add("BINARY VARYING", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("BINARY", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("UUID", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("CHARACTER", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("CHARACTER VARYING", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("VARCHAR_IGNORECASE", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("DECIMAL", cn.org.expect.database.load.converter.BigDecimalConverter.class);
            this.map.add("NUMERIC", cn.org.expect.database.load.converter.BigDecimalConverter.class);
            this.map.add("DECFLOAT", cn.org.expect.database.load.converter.FloatConverter.class);
            this.map.add("INTEGER", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("SMALLINT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("REAL", cn.org.expect.database.load.converter.FloatConverter.class);
            this.map.add("DOUBLE PRECISION", cn.org.expect.database.load.converter.DoubleConverter.class);
            this.map.add("BOOLEAN", cn.org.expect.database.load.converter.BooleanConverter.class);
            this.map.add("DATE", cn.org.expect.database.load.converter.DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyyMMdd");
            this.map.add("TIME", cn.org.expect.database.load.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "hh.mm.ss");
            this.map.add("TIMESTAMP", cn.org.expect.database.load.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd-HH.mm.ss.SSSSSS");
            this.map.add("BINARY LARGE OBJECT", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("CHARACTER LARGE OBJECT", cn.org.expect.database.load.converter.ClobConverter.class);
        }
        return this.map;
    }

    public boolean isOverLengthException(Throwable e) {
        return false;
    }

    public boolean isRebuildTableException(Throwable e) {
        return false;
    }

    public boolean isPrimaryRepeatException(Throwable e) {
        return (e instanceof SQLException) && (((SQLException) e).getErrorCode() == 23505 || ((SQLException) e).getSQLState().equals("23505"));
    }

    public boolean isIndexExistsException(Throwable e) {
        return false;
    }

    public void reorgRunstatsIndexs(Connection connection, List<DatabaseIndex> indexs) throws SQLException {
    }

    public void openLoadMode(JdbcDao dao, String fullname) throws SQLException {
    }

    public void closeLoadMode(JdbcDao dao, String fullname) throws SQLException {
    }

    public void commitLoadData(JdbcDao dao, String fullname) throws SQLException {
    }

    public boolean supportedMergeStatement() {
        return false;
    }

    public String toMergeStatement(String tableName, List<DatabaseTableColumn> columns, List<String> mergeColumn) {
        throw new UnsupportedOperationException();
    }

    public String toTableName(String catalog, String schema, String tableName) {
        return tableName;
    }

    /**
     * H2数据库在创建索引时，会默认创建一个按字段顺序反向的唯一索引 <br>
     * 比如: <br>
     * 创建 ALTER TABLE BHCP_FINISH ADD PRIMARY KEY(FILE_DATA, TASK_NAME) 会默认创建索引: <br>
     * CREATE UNIQUE index PRIMARY_KEY_7 on BHCP_FINISH(TASK_NAME asc, FILE_DATA asc); <br>
     *
     * @param connection 数据库连接
     * @param catalog    类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema     模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param tableName  表名（大小写敏感）, 为null表示搜索schema下所有表信息
     * @return 数据库表信息集合
     * @throws SQLException 数据库发生错误
     */
    public List<DatabaseTable> getTable(Connection connection, String catalog, String schema, String tableName) throws SQLException {
        List<DatabaseTable> list = super.getTable(connection, catalog, schema, tableName);
        for (DatabaseTable table : list) {
            DatabaseIndexList primaryIndexs = table.getPrimaryIndexs();
            DatabaseIndexList indexs = table.getIndexs();
            for (DatabaseIndex pk : primaryIndexs) {
                Iterator<DatabaseIndex> it = indexs.iterator();
                while (it.hasNext()) {
                    DatabaseIndex index = it.next();
                    if (pk.equalsColumnName(index, true, true)) {
                        it.remove();
                    }
                }
            }
        }
        return list;
    }
}
