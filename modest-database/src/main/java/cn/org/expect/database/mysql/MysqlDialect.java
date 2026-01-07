package cn.org.expect.database.mysql;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseTableColumnList;
import cn.org.expect.database.DatabaseTypeFactory;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.database.SQL;
import cn.org.expect.database.db2.expconv.StringConverter;
import cn.org.expect.database.export.converter.AbstractConverter;
import cn.org.expect.database.export.converter.BlobConverter;
import cn.org.expect.database.export.converter.ByteArrayConverter;
import cn.org.expect.database.export.converter.ClobConverter;
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
import cn.org.expect.util.StringUtils;

@EasyBean(value = "mysql")
public class MysqlDialect extends AbstractDialect {

    /** 数据库中字段类型与卸载处理逻辑的映射关系 */
    protected StandardJdbcConverterMapper exp;

    /** 类型映射关系 */
    protected StandardJdbcConverterMapper map;

    /**
     * 初始化
     */
    public MysqlDialect() {
        super();
    }

    public String generateDeleteQuicklySQL(Connection connection, String catalog, String schema, String tableName) {
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException(tableName);
        } else {
            return "truncate table " + this.generateTableName(catalog, schema, tableName);
        }
    }

    public String generateTableName(String catalog, String schema, String tableName) {
        return super.generateTableName(catalog, schema, tableName);
    }

    public boolean terminate(Connection conn, Properties p) throws SQLException {
        return super.terminate(conn, p);
    }

    public String getSchema(Connection conn) throws SQLException {
        String schema = StringUtils.trimBlank((String) JdbcDao.queryFirstRowFirstCol(conn, "SELECT SCHEMA()"));
        return schema == null ? schema : schema.toUpperCase();
    }

    public void setSchema(Connection conn, String schema) throws SQLException {
        JdbcDao.execute(conn, "USE " + schema);
    }

    protected DatabaseTypeFactory getDatabaseTypeFactory() {
        return new MysqlDatabaseTypeFactory();
    }

    public List<DatabaseURL> parseJdbcUrl(String url) {
        Ensure.notNull(url);

        List<DatabaseURL> list = new ArrayList<DatabaseURL>(1);
        String[] part = StringUtils.split(url, "/");
        if (part.length == 4) {
            String databaseType = "mysql";

            // 解析第一部分
            String[] part1 = StringUtils.split(part[0], ":");
            if (part1.length == 3) {
                databaseType = part1[1];
            } else {
                throw new IllegalArgumentException(url + " error!");
            }

            // 第二部分
            if (!part[1].equals("")) {
                throw new IllegalArgumentException(url + " error!");
            }

            // 解析第三部分
            String[] part3 = StringUtils.removeBlank(StringUtils.split(part[2], ","));
            if (part3.length == 0) {
                throw new IllegalArgumentException(url + " error!");
            }

            for (String str : part3) {
                String[] array = StringUtils.split(str, ":");

                StandardDatabaseURL obj = new StandardDatabaseURL(url);
                list.add(obj);

                obj.setDatabaseType(databaseType);
                if (array.length == 1) {
                    obj.setHostname(array[0]);
                    obj.setPort("3306");
                } else if (array.length == 2) {
                    obj.setHostname(array[0]);
                    obj.setPort(array[1]);
                } else {
                    throw new IllegalArgumentException(url);
                }
            }

            // 解析第四部分
            int p4 = part[3].indexOf('?');
            if (p4 == -1) {
                for (DatabaseURL cfg : list) {
                    ((StandardDatabaseURL) cfg).setDatabaseName(part[3]);
                }
            } else {
                for (DatabaseURL cfg : list) {
                    String dbname = part[3].substring(0, p4);
                    ((StandardDatabaseURL) cfg).setDatabaseName(dbname);

                    String params = part[3].substring(p4 + 1);
                    String[] ps = StringUtils.split(params, '&');
                    for (String str : ps) {
                        String[] array = StringUtils.splitProperty(str);
                        if (array != null) {
                            ((StandardDatabaseURL) cfg).setAttribute(array[0], array[1]);
                        }
                    }
                }
            }

            return list;
        } else {
            throw new IllegalArgumentException(url + " error!");
        }
    }

    public List<DatabaseProcedure> getProcedures(Connection connection, String catalog, String schema, String procedureName) throws SQLException {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        procedureName = this.parseIdentifier(procedureName);

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
        String sql = "select ROUTINE_CATALOG,ROUTINE_SCHEMA,ROUTINE_NAME,EXTERNAL_LANGUAGE from INFORMATION_SCHEMA.ROUTINES where ROUTINE_TYPE = 'PROCEDURE' " + where;
        JdbcQueryStatement dao = new JdbcQueryStatement(connection, sql);
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
        dao.close();
        return list;
    }

    public JdbcConverterMapper getObjectConverters() {
        if (this.exp == null) {
            this.exp = new StandardJdbcConverterMapper();
            this.exp.add("TINYINT", IntegerConverter.class);
            this.exp.add("SMALLINT", IntegerConverter.class);
            this.exp.add("MEDIUMINT", IntegerConverter.class);
            this.exp.add("INTEGER", IntegerConverter.class);
            this.exp.add("BIGINT", LongConverter.class);
            this.exp.add("DECIMAL", cn.org.expect.database.export.converter.BigDecimalConverter.class);
            this.exp.add("NUMERIC", cn.org.expect.database.export.converter.BigDecimalConverter.class);
            this.exp.add("FLOAT", FloatConverter.class);
            this.exp.add("DOUBLE", cn.org.expect.database.export.converter.DoubleConverter.class);
            this.exp.add("REAL", cn.org.expect.database.export.converter.DoubleConverter.class);
            this.exp.add("YEAR", DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyy");
            this.exp.add("DATE", DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyy-MM-dd");
            this.exp.add("TIME", cn.org.expect.database.export.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "hh:mm:ss");
            this.exp.add("DATETIME", cn.org.expect.database.export.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
            this.exp.add("TIMESTAMP", cn.org.expect.database.export.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
            this.exp.add("CHAR", StringConverter.class);
            this.exp.add("VARCHAR", StringConverter.class);
            this.exp.add("TINYTEXT", ClobConverter.class);
            this.exp.add("TEXT", ClobConverter.class);
            this.exp.add("MEDIUMTEXT", ClobConverter.class);
            this.exp.add("LONGTEXT", ClobConverter.class);
            this.exp.add("BINARY", ByteArrayConverter.class);
            this.exp.add("VARBINARY", ByteArrayConverter.class);
            this.exp.add("TINYBLOB", BlobConverter.class);
            this.exp.add("BLOB", BlobConverter.class);
            this.exp.add("MEDIUMBLOB", BlobConverter.class);
            this.exp.add("LONGBLOB", BlobConverter.class);
        }
        return this.exp;
    }

    public JdbcConverterMapper getStringConverters() {
        if (this.map == null) {
            this.map = new StandardJdbcConverterMapper();
            this.map.add("TINYINT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("SMALLINT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("MEDIUMINT", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("INTEGER", cn.org.expect.database.load.converter.IntegerConverter.class);
            this.map.add("BIGINT", cn.org.expect.database.load.converter.LongConverter.class);
            this.map.add("DECIMAL", cn.org.expect.database.load.converter.BigDecimalConverter.class);
            this.map.add("NUMERIC", cn.org.expect.database.load.converter.BigDecimalConverter.class);
            this.map.add("FLOAT", cn.org.expect.database.load.converter.FloatConverter.class);
            this.map.add("DOUBLE", cn.org.expect.database.load.converter.DoubleConverter.class);
            this.map.add("REAL", cn.org.expect.database.load.converter.DoubleConverter.class);
            this.map.add("YEAR", cn.org.expect.database.load.converter.DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyy");
            this.map.add("DATE", cn.org.expect.database.load.converter.DateConverter.class, AbstractConverter.PARAM_DATEFORMAT, "yyyy-MM-dd");
            this.map.add("TIME", cn.org.expect.database.load.converter.TimeConverter.class, AbstractConverter.PARAM_TIMEFORMAT, "hh:mm:ss");
            this.map.add("DATETIME", cn.org.expect.database.load.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
            this.map.add("TIMESTAMP", cn.org.expect.database.load.converter.TimestampConverter.class, AbstractConverter.PARAM_TIMESTAMPFORMAT, "yyyy-MM-dd HH:mm:ss");
            this.map.add("CHAR", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("VARCHAR", cn.org.expect.database.load.converter.StringConverter.class);
            this.map.add("TINYTEXT", cn.org.expect.database.load.converter.ClobConverter.class);
            this.map.add("TEXT", cn.org.expect.database.load.converter.ClobConverter.class);
            this.map.add("MEDIUMTEXT", cn.org.expect.database.load.converter.ClobConverter.class);
            this.map.add("LONGTEXT", cn.org.expect.database.load.converter.ClobConverter.class);
            this.map.add("BINARY", cn.org.expect.database.load.converter.ByteArrayConverter.class);
            this.map.add("VARBINARY", cn.org.expect.database.load.converter.ByteArrayConverter.class);
            this.map.add("TINYBLOB", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("BLOB", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("MEDIUMBLOB", cn.org.expect.database.load.converter.BlobConverter.class);
            this.map.add("LONGBLOB", cn.org.expect.database.load.converter.BlobConverter.class);
        }
        return this.map;
    }

    public boolean isOverLengthException(Throwable e) {
        if (e instanceof SQLException) {
            SQLException sqlExp = (SQLException) e;
            while (sqlExp != null) {
                if (sqlExp.getErrorCode() == 1406) {
                    return true;
                }

                sqlExp = sqlExp.getNextException();
            }
        }

        Throwable cause = e.getCause();
        if (cause == null) {
            return false;
        } else {
            return this.isOverLengthException(cause);
        }
    }

    public boolean isRebuildTableException(Throwable e) {
        if (e instanceof SQLException) {
            SQLException sqlExp = (SQLException) e;
            while (sqlExp != null) {
                if (sqlExp.getErrorCode() == 1064) {
                    return true;
                }

                sqlExp = sqlExp.getNextException();
            }
        }

        Throwable cause = e.getCause();
        if (cause == null) {
            return false;
        } else {
            return this.isRebuildTableException(cause);
        }
    }

    public boolean isPrimaryRepeatException(Throwable e) {
        if (e instanceof SQLException) {
            SQLException sqlExp = (SQLException) e;
            while (sqlExp != null) {
                if (sqlExp.getErrorCode() == 1062) {
                    return true;
                }

                sqlExp = sqlExp.getNextException();
            }
        }

        Throwable cause = e.getCause();
        if (cause == null) {
            return false;
        } else {
            return this.isPrimaryRepeatException(cause);
        }
    }

    public boolean isIndexExistsException(Throwable e) {
        if (e instanceof SQLException) {
            SQLException sqlExp = (SQLException) e;
            while (sqlExp != null) {
                if (sqlExp.getErrorCode() == 1061) {
                    return true;
                }

                sqlExp = sqlExp.getNextException();
            }
        }

        Throwable cause = e.getCause();
        if (cause == null) {
            return false;
        } else {
            return this.isIndexExistsException(cause);
        }
    }

    public void reorgRunstatsIndexs(Connection conn, List<DatabaseIndex> indexs) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void openLoadMode(JdbcDao conn, String fullTableName) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void closeLoadMode(JdbcDao conn, String fullTableName) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void commitLoadData(JdbcDao conn, String fullTableName) throws SQLException {
        // TODO Auto-generated method stub
    }

    public boolean expandLength(final DatabaseTableColumn column, final String value, final String charsetName) {
        return false;
    }

    public void expandLength(final Connection conn, final DatabaseTableColumnList oldTableColumnList, final List<DatabaseTableColumn> newTableColumnList) throws SQLException {
    }

    public String getCatalog(Connection connection) throws SQLException {
        return connection.getCatalog();
    }

    public DatabaseDDL generateDDL(Connection connection, DatabaseProcedure procedure) throws SQLException {
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

    public String getKeepAliveSQL() {
        return "select 1 from dual";
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
        sql += ")";
        sql += " ON DUPLICATE KEY UPDATE ";

        boolean comma = false; // true表示添加逗号
        // 拼写 字段=值 表达式
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            String name = col.getName();
            if (set.contains(name)) {
                continue;
            }

            if (comma) {
                sql += ", ";
            }
            sql += name + "=VALUES(" + name + ")";
            comma = true;
        }
        return sql;
    }

    public List<DatabaseTable> getTable(Connection connection, String catalog, String schema, String tableName) throws SQLException {
        return super.getTable(connection, catalog, schema, tableName);
    }
}
