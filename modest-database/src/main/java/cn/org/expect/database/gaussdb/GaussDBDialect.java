package cn.org.expect.database.gaussdb;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseTableColumnList;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.database.SQL;
import cn.org.expect.database.export.converter.AbstractConverter;
import cn.org.expect.database.export.converter.BlobConverter;
import cn.org.expect.database.export.converter.ByteArrayConverter;
import cn.org.expect.database.export.converter.DateConverter;
import cn.org.expect.database.export.converter.FloatConverter;
import cn.org.expect.database.export.converter.IntegerConverter;
import cn.org.expect.database.export.converter.LongConverter;
import cn.org.expect.database.export.converter.StringConverter;
import cn.org.expect.database.internal.AbstractDialect;
import cn.org.expect.database.internal.StandardDatabaseDDL;
import cn.org.expect.database.internal.StandardDatabaseProcedure;
import cn.org.expect.database.internal.StandardDatabaseURL;
import cn.org.expect.database.internal.StandardJdbcConverterMapper;
import cn.org.expect.io.ClobWriter;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 高斯数据库方言实现类。
 */
@EasyBean(value = "gaussdb")
public class GaussDBDialect extends AbstractDialect {

    /** 数据库中字段类型与卸载处理逻辑的映射关系 */
    protected StandardJdbcConverterMapper exp;

    /** 类型映射关系 */
    protected StandardJdbcConverterMapper map;

    public String getKeepAliveSQL() {
        return "select 1";
    }

    public String generateDeleteQuicklySQL(Connection connection, String catalog, String schema, String tableName) {
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException(tableName);
        } else {
            return "truncate table " + this.generateTableName(catalog, schema, tableName);
        }
    }

    public void setSchema(Connection connection, String schema) throws SQLException {
        JdbcDao.execute(connection, "set search_path to " + schema);
    }

    public String getSchema(Connection connection) throws SQLException {
        return StringUtils.trimBlank(JdbcDao.queryFirstRowFirstCol(connection, "select current_schema()"));
    }

    public String getCatalog(Connection connection) throws SQLException {
        return StringUtils.trimBlank(JdbcDao.queryFirstRowFirstCol(connection, "select current_database()"));
    }

    public String generateDropPrimaryDDL(DatabaseIndex index) {
        return "alter table " + index.getTableFullName() + " drop constraint " + index.getName();
    }

    public String generateDropIndexDDL(DatabaseIndex index) {
        return "drop index " + index.getFullName();
    }

    public List<DatabaseURL> parseJdbcUrl(String url) {
        Ensure.notNull(url);

        String[] prefix = StringUtils.split(url, "://");
        if (prefix.length != 2) {
            throw new IllegalArgumentException(url + " error!");
        }

        String[] protocol = StringUtils.split(prefix[0], ":");
        if (protocol.length != 2 || !"jdbc".equalsIgnoreCase(protocol[0])) {
            throw new IllegalArgumentException(url + " error!");
        }

        String databaseType = protocol[1];
        String hostAndDatabase = prefix[1];
        String params = null;
        int parameterIndex = hostAndDatabase.indexOf('?');
        if (parameterIndex != -1) {
            params = hostAndDatabase.substring(parameterIndex + 1);
            hostAndDatabase = hostAndDatabase.substring(0, parameterIndex);
        }

        String[] hostDatabase = StringUtils.split(hostAndDatabase, "/");
        if (hostDatabase.length != 2) {
            throw new IllegalArgumentException(url + " error!");
        }

        String[] hosts = StringUtils.removeBlank(StringUtils.split(hostDatabase[0], ","));
        if (hosts.length == 0) {
            throw new IllegalArgumentException(url + " error!");
        }

        List<DatabaseURL> list = new ArrayList<DatabaseURL>(hosts.length);
        for (String host : hosts) {
            StandardDatabaseURL obj = new StandardDatabaseURL(url);
            obj.setDatabaseType(databaseType);
            obj.setDatabaseName(hostDatabase[1]);
            this.parseHost(obj, host);
            this.parseParameters(obj, params);
            list.add(obj);
        }
        return list;
    }

    protected void parseHost(StandardDatabaseURL obj, String host) {
        String[] array = StringUtils.split(host, ":");
        if (array.length == 1) {
            obj.setHostname(array[0]);
            obj.setPort("5432");
        } else if (array.length == 2) {
            obj.setHostname(array[0]);
            obj.setPort(array[1]);
        } else {
            throw new IllegalArgumentException(host);
        }
    }

    protected void parseParameters(StandardDatabaseURL obj, String params) {
        if (StringUtils.isBlank(params)) {
            return;
        }

        String[] ps = StringUtils.split(params, '&');
        for (String str : ps) {
            String[] array = StringUtils.splitProperty(str);
            if (array != null) {
                obj.setAttribute(array[0], array[1]);
            }
        }
    }

    public DatabaseDDL generateDDL(Connection connection, DatabaseProcedure procedure) throws SQLException {
        StandardDatabaseDDL ddl = new StandardDatabaseDDL();
        JdbcQueryStatement dao = new JdbcQueryStatement(connection, "select pg_get_functiondef(p.oid) as ROUTINE_DEFINITION from pg_proc p join pg_namespace n on n.oid = p.pronamespace where p.proname=? and n.nspname=?");
        try {
            dao.setParameter(procedure.getName());
            dao.setParameter(procedure.getSchema());

            ResultSet resultSet = dao.query();
            if (resultSet.next()) {
                Clob value = resultSet.getClob("ROUTINE_DEFINITION");
                if (value == null) {
                    ddl.add(resultSet.getString("ROUTINE_DEFINITION"));
                } else {
                    ddl.add(new ClobWriter(value).toString());
                }
            }
            return ddl;
        } finally {
            dao.close();
        }
    }

    public List<DatabaseProcedure> getProcedures(Connection connection, String catalog, String schema, String procedureName) throws SQLException {
        schema = SQL.escapeQuote(this.parseIdentifier(schema));
        procedureName = SQL.escapeQuote(this.parseIdentifier(procedureName));

        String where = "";
        if (StringUtils.isNotBlank(schema)) {
            where += " and n.nspname='" + SQL.toIdentifier(schema) + "'";
        }
        if (StringUtils.isNotBlank(procedureName)) {
            if (procedureName.indexOf('%') != -1) {
                where += " and p.proname like '" + SQL.toIdentifier(procedureName) + "'";
            } else {
                where += " and p.proname = '" + SQL.toIdentifier(procedureName) + "'";
            }
        }

        List<DatabaseProcedure> list = new ArrayList<DatabaseProcedure>();
        String sql = "select current_database() as ROUTINE_CATALOG, n.nspname as ROUTINE_SCHEMA, p.proname as ROUTINE_NAME, l.lanname as EXTERNAL_LANGUAGE from pg_proc p join pg_namespace n on n.oid = p.pronamespace join pg_language l on l.oid = p.prolang where p.prokind in ('p', 'f') " + where;
        JdbcQueryStatement dao = new JdbcQueryStatement(connection, sql);
        try {
            ResultSet resultSet = dao.query();
            while (resultSet.next()) {
                StandardDatabaseProcedure obj = new StandardDatabaseProcedure();
                obj.setCatalog(StringUtils.rtrimBlank(resultSet.getString("ROUTINE_CATALOG")));
                obj.setSchema(StringUtils.rtrimBlank(resultSet.getString("ROUTINE_SCHEMA")));
                obj.setName(StringUtils.rtrimBlank(resultSet.getString("ROUTINE_NAME")));
                obj.setFullName(this.generateTableName(obj.getCatalog(), obj.getSchema(), obj.getName()));
                obj.setId(obj.getFullName());
                obj.setCreator("");
                obj.setCreatTime(null);
                obj.setLanguage(StringUtils.rtrimBlank(resultSet.getString("EXTERNAL_LANGUAGE")));
                list.add(obj);
            }
            return list;
        } finally {
            dao.close();
        }
    }

    public JdbcConverterMapper getObjectConverters() {
        if (this.exp == null) {
            this.exp = new StandardJdbcConverterMapper();
            this.exp.add("BOOL", cn.org.expect.database.export.converter.BooleanConverter.class);
            this.exp.add("BOOLEAN", cn.org.expect.database.export.converter.BooleanConverter.class);
            this.exp.add("CHAR", StringConverter.class);
            this.exp.add("CHARACTER", StringConverter.class);
            this.exp.add("VARCHAR", StringConverter.class);
            this.exp.add("CHARACTER VARYING", StringConverter.class);
            this.exp.add("TEXT", StringConverter.class);
            this.exp.add("BYTEA", ByteArrayConverter.class);
            this.exp.add("BLOB", BlobConverter.class);
            this.exp.add("CLOB", StringConverter.class);
            this.exp.add("SMALLINT", IntegerConverter.class);
            this.exp.add("INT2", IntegerConverter.class);
            this.exp.add("INTEGER", IntegerConverter.class);
            this.exp.add("INT", IntegerConverter.class);
            this.exp.add("INT4", IntegerConverter.class);
            this.exp.add("BIGINT", LongConverter.class);
            this.exp.add("INT8", LongConverter.class);
            this.exp.add("NUMERIC", cn.org.expect.database.export.converter.BigDecimalConverter.class);
            this.exp.add("DECIMAL", cn.org.expect.database.export.converter.BigDecimalConverter.class);
            this.exp.add("REAL", FloatConverter.class);
            this.exp.add("FLOAT4", FloatConverter.class);
            this.exp.add("DOUBLE PRECISION", cn.org.expect.database.export.converter.DoubleConverter.class);
            this.exp.add("FLOAT8", cn.org.expect.database.export.converter.DoubleConverter.class);
            this.exp.add("DATE", DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyy-MM-dd");
            this.exp.add("TIME", cn.org.expect.database.export.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "HH:mm:ss");
            this.exp.add("TIME WITHOUT TIME ZONE", cn.org.expect.database.export.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "HH:mm:ss");
            this.exp.add("TIMESTAMP", cn.org.expect.database.export.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
            this.exp.add("TIMESTAMP WITHOUT TIME ZONE", cn.org.expect.database.export.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
        }
        return this.exp;
    }

    public JdbcConverterMapper getStringConverters() {
        if (this.map == null) {
            this.map = new StandardJdbcConverterMapper();
            this.map.add("BOOL", cn.org.expect.database.load.converter.BooleanConverter.class);
            this.map.add("BOOLEAN", cn.org.expect.database.load.converter.BooleanConverter.class);
            this.map.add("CHAR", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("CHARACTER", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("VARCHAR", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("CHARACTER VARYING", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("TEXT", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("BYTEA", cn.org.expect.database.load.converter.ByteArrayConverter.class);
            this.map.add("BLOB", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("CLOB", cn.org.expect.database.load.converter.ClobConverter.class);
            this.map.add("SMALLINT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("INT2", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("INTEGER", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("INT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("INT4", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("BIGINT", cn.org.expect.database.load.converter.LongConverter.class);
            this.map.add("INT8", cn.org.expect.database.load.converter.LongConverter.class);
            this.map.add("NUMERIC", cn.org.expect.database.load.converter.BigDecimalConverter.class);
            this.map.add("DECIMAL", cn.org.expect.database.load.converter.BigDecimalConverter.class);
            this.map.add("REAL", cn.org.expect.database.load.converter.FloatConverter.class);
            this.map.add("FLOAT4", cn.org.expect.database.load.converter.FloatConverter.class);
            this.map.add("DOUBLE PRECISION", cn.org.expect.database.load.converter.DoubleConverter.class);
            this.map.add("FLOAT8", cn.org.expect.database.load.converter.DoubleConverter.class);
            this.map.add("DATE", cn.org.expect.database.load.converter.DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyy-MM-dd");
            this.map.add("TIME", cn.org.expect.database.load.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "HH:mm:ss");
            this.map.add("TIME WITHOUT TIME ZONE", cn.org.expect.database.load.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "HH:mm:ss");
            this.map.add("TIMESTAMP", cn.org.expect.database.load.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
            this.map.add("TIMESTAMP WITHOUT TIME ZONE", cn.org.expect.database.load.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
        }
        return this.map;
    }

    public boolean isOverLengthException(Throwable e) {
        return this.hasSqlState(e, "22001");
    }

    public boolean isRebuildTableException(Throwable e) {
        return this.hasSqlState(e, "42601");
    }

    public boolean isPrimaryRepeatException(Throwable e) {
        return this.hasSqlState(e, "23505");
    }

    public boolean isIndexExistsException(Throwable e) {
        return this.hasSqlState(e, "42P07");
    }

    public void reorgRunstatsIndexs(Connection connection, List<DatabaseIndex> indexs) throws SQLException {
    }

    public void openLoadMode(JdbcDao dao, String fullTableName) throws SQLException {
    }

    public void closeLoadMode(JdbcDao dao, String fullTableName) throws SQLException {
    }

    public void commitLoadData(JdbcDao dao, String fullTableName) throws SQLException {
        dao.commit();
    }

    public boolean expandLength(DatabaseTableColumn column, String value, String charsetName) {
        return false;
    }

    public void expandLength(Connection connection, DatabaseTableColumnList oldTableColumnList, List<DatabaseTableColumn> newTableColumnList) throws SQLException {
    }

    protected boolean hasSqlState(Throwable e, String sqlState) {
        if (e instanceof SQLException) {
            SQLException sqlExp = (SQLException) e;
            while (sqlExp != null) {
                if (sqlState.equals(sqlExp.getSQLState())) {
                    return true;
                }
                sqlExp = sqlExp.getNextException();
            }
        }

        Throwable cause = e.getCause();
        return cause != null && this.hasSqlState(cause, sqlState);
    }

    public boolean supportedMergeStatement() {
        return true;
    }

    public String generateMergeStatement(String tableName, List<DatabaseTableColumn> columns, List<String> mergeColumn) {
        CaseSensitivSet set = new CaseSensitivSet(mergeColumn);
        String sql = "INSERT INTO " + tableName + " (";
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            sql += it.next().getName();
            if (it.hasNext()) {
                sql += ", ";
            }
        }
        sql += ") VALUES (";
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            it.next();
            sql += "?";
            if (it.hasNext()) {
                sql += ", ";
            }
        }
        sql += ") ON CONFLICT (";
        for (Iterator<String> it = mergeColumn.iterator(); it.hasNext(); ) {
            sql += it.next();
            if (it.hasNext()) {
                sql += ", ";
            }
        }
        sql += ") DO UPDATE SET ";

        boolean comma = false;
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            String name = col.getName();
            if (set.contains(name)) {
                continue;
            }

            if (comma) {
                sql += ", ";
            }
            sql += name + "=excluded." + name;
            comma = true;
        }
        return sql;
    }
}
