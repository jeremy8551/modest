package cn.org.expect.database.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseTableColumnList;
import cn.org.expect.database.DatabaseURL;
import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.JdbcDao;

public class StandardDatabaseDialect extends AbstractDialect {

    public StandardDatabaseDialect() {
        super();
    }

    public void setSchema(Connection connection, String schema) {
        throw new UnsupportedOperationException();
    }

    public List<DatabaseURL> parseJdbcUrl(String url) {
        throw new UnsupportedOperationException();
    }

    public List<DatabaseProcedure> getProcedures(Connection connection, String catalog, String schema, String procedureName) {
        throw new UnsupportedOperationException();
    }

    public JdbcConverterMapper getObjectConverters() {
        throw new UnsupportedOperationException();
    }

    public boolean isOverLengthException(Throwable e) {
        throw new UnsupportedOperationException();
    }

    public boolean isRebuildTableException(Throwable e) {
        throw new UnsupportedOperationException();
    }

    public boolean isPrimaryRepeatException(Throwable e) {
        throw new UnsupportedOperationException();
    }

    public boolean isIndexExistsException(Throwable e) {
        throw new UnsupportedOperationException();
    }

    public void reorgRunstatsIndexs(Connection conn, List<DatabaseIndex> indexs) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void openLoadMode(JdbcDao conn, String fullTableName) throws SQLException {
    }

    public void closeLoadMode(JdbcDao conn, String fullTableName) throws SQLException {
    }

    public void commitLoadData(JdbcDao conn, String fullTableName) throws SQLException {
    }

    public boolean expandLength(final DatabaseTableColumn column, final String value, final String charsetName) {
        return false;
    }

    public void expandLength(final Connection conn, final DatabaseTableColumnList oldTableColumnList, final List<DatabaseTableColumn> newTableColumnList) throws SQLException {
    }

    public JdbcConverterMapper getStringConverters() {
        throw new UnsupportedOperationException();
    }

    public String getCatalog(Connection connection) throws SQLException {
        return null;
    }

    public DatabaseDDL generateDDL(Connection connection, DatabaseProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    public String getKeepAliveSQL() {
        throw new UnsupportedOperationException();
    }

    public boolean supportedMergeStatement() {
        return false;
    }

    public String generateMergeStatement(String tableName, List<DatabaseTableColumn> columns, List<String> mergeColumn) {
        throw new UnsupportedOperationException();
    }
}
