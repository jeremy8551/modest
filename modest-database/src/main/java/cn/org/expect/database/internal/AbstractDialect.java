package cn.org.expect.database.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.database.DatabaseDDL;
import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseException;
import cn.org.expect.database.DatabaseIndex;
import cn.org.expect.database.DatabaseIndexList;
import cn.org.expect.database.DatabaseProcedure;
import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseTableDDL;
import cn.org.expect.database.DatabaseType;
import cn.org.expect.database.DatabaseTypeFactory;
import cn.org.expect.database.DatabaseTypeSet;
import cn.org.expect.database.Jdbc;
import cn.org.expect.database.SQL;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.ObjectUtils;
import cn.org.expect.util.Property;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

public abstract class AbstractDialect implements DatabaseDialect {
    protected final static Log log = LogFactory.getLog(AbstractDialect.class);

    /** 关键字字符串集合 */
    protected Set<String> keyword;

    /**
     * 初始化
     */
    public AbstractDialect() {
        this.keyword = new CaseSensitivSet();
    }

    public String getDatabaseMajorVersion() {
        return "";
    }

    public String getDatabaseMinorVersion() {
        return "";
    }

    public String generateDropPrimaryDDL(DatabaseIndex index) {
        return "alter table " + index.getTableFullName() + " drop primary key ";
    }

    public String generateDropTable(DatabaseTable table) {
        return "drop table " + table.getFullName();
    }

    public String generateDropIndexDDL(DatabaseIndex index) {
        return "drop index " + index.getFullName() + " on " + index.getTableFullName();
    }

    public DatabaseTableDDL generateDDL(Connection connection, DatabaseTable table) throws SQLException {
        StringBuilder str = this.toDDL(table);

        // 建表语句
        StandardDatabaseTableDDL ddl = new StandardDatabaseTableDDL();
        ddl.setTable(str.toString());

        // 索引
        DatabaseIndexList indexs = table.getIndexs();
        for (DatabaseIndex index : indexs) {
            ddl.getIndex().addAll(this.generateDDL(connection, index, false));
        }

        // 主键
        DatabaseIndexList pks = table.getPrimaryIndexs();
        for (DatabaseIndex index : pks) {
            ddl.getPrimaryKey().addAll(this.generateDDL(connection, index, true));
        }
        return ddl;
    }

    /**
     * 生成数据库表的 DDL 语句
     *
     * @param table 数据库表信息
     * @return DDL 语句
     */
    protected StringBuilder toDDL(DatabaseTable table) {
        List<DatabaseTableColumn> columns = table.getColumns();

        StringBuilder buf = new StringBuilder();
        buf.append("create table ");
        buf.append(table.getFullName()).append(" (").append(Settings.getLineSeparator());
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            buf.append("    ");
            buf.append(col.getName()).append("  ");
            buf.append(col.getFieldType());

            // 带一个长度范围的字段类型
            if (col.getSqlType() == Types.CHAR //
                || col.getSqlType() == Types.VARCHAR //
                || col.getSqlType() == Types.LONGVARCHAR //
                || col.getSqlType() == Types.ARRAY //
                || col.getSqlType() == Types.BINARY //
                || col.getSqlType() == Types.LONGVARBINARY //
                || col.getSqlType() == Types.VARBINARY //
            ) {
                buf.append("(");
                buf.append(col.length());
                buf.append(")");
            }

            // 不带长度范围的字段类型
            else if (col.getSqlType() == Types.INTEGER //
                || col.getSqlType() == Types.BIGINT //
                || col.getSqlType() == Types.BLOB //
                || col.getSqlType() == Types.BOOLEAN //
                || col.getSqlType() == Types.CLOB //
                || col.getSqlType() == Types.DATE //
                || col.getSqlType() == Types.TIME //
                || col.getSqlType() == Types.TIMESTAMP //
                || col.getSqlType() == Types.DOUBLE //
                || col.getSqlType() == Types.FLOAT //
                || col.getSqlType() == Types.JAVA_OBJECT //
                || col.getSqlType() == Types.REAL //
                || col.getSqlType() == Types.SMALLINT //
                || col.getSqlType() == Types.TINYINT //
            ) {
            }

            // 带二个长度范围的字段类型
            else if (col.getSqlType() == Types.DECIMAL //
                || col.getSqlType() == Types.NUMERIC //
            ) {
                buf.append("(");
                buf.append(col.length());
                buf.append(", ");
                buf.append(col.getDigit());
                buf.append(")");
            } else {
                throw new UnsupportedOperationException(String.valueOf(col.getSqlType()));
            }

            if ("NO".equals(col.getNullAble())) {
                buf.append(" not null");
            }

            if (it.hasNext()) {
                buf.append(",");
            }

            String remarks = col.getRemark();
            if (StringUtils.isNotBlank(remarks)) {
                buf.append(" -- ").append(remarks);
            }
            buf.append(Settings.getLineSeparator());
        }
        buf.append(")");
        return buf;
    }

    public DatabaseDDL generateDDL(Connection connection, DatabaseIndex index, boolean primary) throws SQLException {
        StandardDatabaseDDL ddl = new StandardDatabaseDDL();
        if (primary) {
            StringBuilder buf = new StringBuilder();
            buf.append("ALTER TABLE ");
            buf.append(index.getTableFullName());
            buf.append(" ADD PRIMARY KEY(");
            List<String> indexColumnName = index.getColumnNames();
            for (int i = 0; i < indexColumnName.size(); i++) {
                String name = indexColumnName.get(i);
                buf.append(name);
                if (i != indexColumnName.size() - 1) {
                    buf.append(", ");
                }
            }
            buf.append(")");
            ddl.add(buf.toString());
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append("CREATE ");
            if (index.isUnique()) {
                buf.append("UNIQUE ");
            }
            buf.append("index ").append(index.getFullName()).append(" on ");
            buf.append(index.getTableFullName());
            buf.append("(");
            List<String> indexColumnName = index.getColumnNames();
            List<Integer> indexColumnSort = index.getDirections();
            for (int i = 0; i < indexColumnName.size(); i++) {
                String name = indexColumnName.get(i);
                Integer sort = indexColumnSort.get(i);
                buf.append(name);
                if (sort != null) {
                    if (DatabaseIndex.INDEX_ASC == sort) {
                        buf.append(" asc");
                    }
                    if (DatabaseIndex.INDEX_DESC == sort) {
                        buf.append(" desc");
                    }
                }
                if (i != indexColumnName.size() - 1) {
                    buf.append(", ");
                }
            }
            buf.append(")");
            ddl.add(buf.toString());
        }
        return ddl;
    }

    public String getSchema(Connection conn) throws SQLException {
        try {
            return (String) ClassUtils.executeMethod(conn, "getSchema", ObjectUtils.of());
        } catch (Exception e) {
            Property property = CollectionUtils.first(Jdbc.getSchemas(conn));
            return property == null ? null : property.getKey();
        }
    }

    public Set<String> getKeyword(Connection conn) throws SQLException {
        Set<String> keys = (conn == null) ? new CaseSensitivSet() : Jdbc.getSQLKeywords(conn);
        keys.addAll(this.keyword);
        return keys;
    }

    /**
     * 查询数据库表的索引或主键信息
     *
     * @param connection 数据库连接
     * @param catalog    类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema     模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param tableName  表名（大小写敏感）
     * @param name       索引名
     * @return 索引集合
     * @throws SQLException 数据库错误
     */
    public List<DatabaseIndex> getIndexs(Connection connection, String catalog, String schema, String tableName, String name) throws SQLException {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        tableName = this.parseIdentifier(tableName);
        name = this.parseIdentifier(name);

        Map<String, StandardDatabaseIndex> map = new HashMap<String, StandardDatabaseIndex>();
        ResultSet resultSet = null;
        try {
            List<DatabaseIndex> list = new ArrayList<DatabaseIndex>();
            resultSet = connection.getMetaData().getIndexInfo(catalog, schema, tableName, false, false);
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                short order = resultSet.getShort("ORDINAL_POSITION");
                String asc = resultSet.getString("ASC_OR_DESC");

                // 设置排序规则
                int sort = DatabaseIndex.INDEX_UNKNOWN;
                if ("A".equalsIgnoreCase(asc)) {
                    sort = DatabaseIndex.INDEX_ASC;
                }
                if ("D".equalsIgnoreCase(asc)) {
                    sort = DatabaseIndex.INDEX_DESC;
                }

                if (StringUtils.isBlank(indexName)) {
                    continue;
                }

                if (!StringUtils.isBlank(name) && !indexName.equalsIgnoreCase(name)) {
                    continue;
                }

                StandardDatabaseIndex index = map.get(indexName);
                if (index == null) {
                    index = new StandardDatabaseIndex();
                    index.setName(indexName);
                    index.setTableCatalog(StringUtils.trimBlank(resultSet.getString("TABLE_CAT")));
                    index.setTableName(StringUtils.trim(resultSet.getString("TABLE_NAME")));
                    index.setTableSchema(StringUtils.trim(resultSet.getString("TABLE_SCHEM")));
                    index.setTableFullName(this.generateTableName(index.getTableCatalog(), index.getTableSchema(), index.getTableName()));
                    index.setSchema(index.getTableSchema()); // 默认使用表模式作为索引模式，可在数据库方言类中修改这个值
                    index.setFullName(index.getName()); // 默认使用索引名所在全名，可在数据库方言累中修改这个值
                    index.setUnique(!resultSet.getBoolean("NON_UNIQUE"));
                    map.put(indexName, index);
                    list.add(index);
                }
                index.getPositions().add(Integer.valueOf((int) order)); // 设置字段在索引中的位置信息，从1开始
                index.getColumnNames().add(columnName); // 设置索引中的字段名集合
                index.getDirections().add(Integer.valueOf(sort)); // 设置索引字段的排序方式
            }

            return list;
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", this.generateTableName(catalog, schema, tableName), e);
        } finally {
            IO.closeQuietly(resultSet);
            map.clear();
        }
    }

    public List<DatabaseIndex> getIndexs(Connection connection, String catalog, String schema, String tableName) throws SQLException {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        tableName = this.parseIdentifier(tableName);

        Map<String, StandardDatabaseIndex> map = new HashMap<String, StandardDatabaseIndex>();
        ResultSet resultSet = null;
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            /** ---- 查询主键索引名称 ---- */
            HashSet<String> pksNames = new HashSet<String>(1);
            ResultSet pksRes = metaData.getPrimaryKeys(catalog, schema, tableName);
            try {
                while (pksRes.next()) {
                    String idxname = pksRes.getString("PK_NAME");
                    pksNames.add(idxname);
                }
            } finally {
                IO.closeQuietly(pksRes);
            }
            /** ---- 查询主键索引名称 ---- */

            List<DatabaseIndex> list = new ArrayList<DatabaseIndex>();
            resultSet = metaData.getIndexInfo(catalog, schema, tableName, false, false);
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                short order = resultSet.getShort("ORDINAL_POSITION");
                String asc = resultSet.getString("ASC_OR_DESC");

                int sort = DatabaseIndex.INDEX_UNKNOWN;
                if ("A".equalsIgnoreCase(asc)) {
                    sort = DatabaseIndex.INDEX_ASC;
                }
                if ("D".equalsIgnoreCase(asc)) {
                    sort = DatabaseIndex.INDEX_DESC;
                }

                if (StringUtils.isBlank(indexName)) {
                    continue;
                }

                if (pksNames.contains(indexName)) {// 不能为主键
                    continue;
                }

                StandardDatabaseIndex index = map.get(indexName);
                if (index == null) {
                    index = new StandardDatabaseIndex();
                    index.setName(indexName);
                    index.setTableCatalog(StringUtils.trimBlank(resultSet.getString("TABLE_CAT")));
                    index.setTableName(StringUtils.trimBlank(resultSet.getString("TABLE_NAME")));
                    index.setTableSchema(StringUtils.trimBlank(resultSet.getString("TABLE_SCHEM")));
                    index.setTableFullName(this.generateTableName(index.getTableCatalog(), index.getTableSchema(), index.getTableName()));
                    index.setSchema(index.getTableSchema()); // 默认使用表模式作为索引模式，可在数据库方言类中修改这个值
                    index.setFullName(index.getName()); // 默认使用索引名所在全名，可在数据库方言累中修改这个值
                    index.setUnique(!resultSet.getBoolean("NON_UNIQUE"));
                    map.put(indexName, index);
                    list.add(index);
                }
                index.getPositions().add(Integer.valueOf((int) order));
                index.getColumnNames().add(columnName);
                index.getDirections().add(Integer.valueOf(sort));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", this.generateIndexName(catalog, schema, tableName), e);
        } finally {
            IO.closeQuietly(resultSet);
            map.clear();
        }
    }

    /**
     * 查询表的主键信息
     *
     * @param connection 数据库连接
     * @param catalog    类别名称，因为存储在此数据库中，所以它必须匹配类别名称。该参数为 "" 则检索没有类别的描述，为 null 则表示该类别名称不应用于缩小搜索范围
     * @param schema     模式名称，因为存储在此数据库中，所以它必须匹配模式名称。该参数为 "" 则检索那些没有模式的描述，为 null 则表示该模式名称不应用于缩小搜索范围
     * @param tableName  表名（大小写敏感）
     * @return 索引集合
     */
    public List<DatabaseIndex> getPrimaryIndex(Connection connection, String catalog, String schema, String tableName) {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        tableName = this.parseIdentifier(tableName);

        Map<String, StandardDatabaseIndex> map = new HashMap<String, StandardDatabaseIndex>();
        ResultSet resultSet = null;
        try {
            List<DatabaseIndex> list = new ArrayList<DatabaseIndex>();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getPrimaryKeys(catalog, schema, tableName);
            while (resultSet.next()) {
                String indexName = resultSet.getString("PK_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                short order = resultSet.getShort("KEY_SEQ");

                if (StringUtils.isBlank(indexName)) {
                    continue;
                }

                StandardDatabaseIndex index = map.get(indexName);
                if (index == null) {
                    index = new StandardDatabaseIndex();
                    index.setName(indexName);
                    index.setTableCatalog(StringUtils.trim(resultSet.getString("TABLE_CAT")));
                    index.setTableSchema(StringUtils.trim(resultSet.getString("TABLE_SCHEM")));
                    index.setTableName(StringUtils.trim(resultSet.getString("TABLE_NAME")));
                    index.setTableFullName(this.generateTableName(index.getTableCatalog(), index.getTableSchema(), index.getTableName()));
                    index.setSchema(index.getTableSchema()); // 默认使用表模式作为索引模式，可在数据库方言类中修改这个值
                    index.setFullName(index.getName()); // 默认使用索引名所在全名，可在数据库方言累中修改这个值
                    index.setUnique(true);
                    map.put(indexName, index);
                    list.add(index);
                }
                index.getPositions().add(Integer.valueOf((int) order));
                index.getColumnNames().add(columnName);
                index.getDirections().add(Integer.valueOf(DatabaseIndex.INDEX_UNKNOWN));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", this.generateTableName(catalog, schema, tableName), e);
        } finally {
            IO.closeQuietly(resultSet);
            map.clear();
        }
    }

    protected DatabaseTypeFactory getDatabaseTypeFactory() throws SQLException {
        return new StandardDatabaseTypeFactory();
    }

    public DatabaseTypeSet getFieldInformation(Connection conn) {
        StandardDatabaseTypes map = new StandardDatabaseTypes();
        DatabaseMetaData metaData;
        ResultSet resultSet = null;
        try {
            metaData = conn.getMetaData();
            resultSet = metaData.getTypeInfo();
            while (resultSet.next()) {
                DatabaseType type = this.getDatabaseTypeFactory().newInstance(resultSet);
                map.put(type.getName(), type);
            }
            return map;
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", e.getLocalizedMessage(), e);
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    public List<DatabaseTable> getTable(Connection connection, String catalog, String schema, String tableName) throws SQLException {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        tableName = this.parseIdentifier(tableName);

        DatabaseTypeSet types = this.getFieldInformation(connection);
        ResultSet resultSet = null;
        try {
            List<DatabaseTable> list = new ArrayList<DatabaseTable>();
            resultSet = connection.getMetaData().getTables(catalog, schema, tableName, null);
            while (resultSet.next()) {
                StandardDatabaseTable table = new StandardDatabaseTable();
                table.setName(StringUtils.trim(resultSet.getString("TABLE_NAME")));
                table.setSchema(StringUtils.trim(resultSet.getString("TABLE_SCHEM")));
                table.setCatalog(StringUtils.trim(resultSet.getString("TABLE_CAT")));
                table.setType(StringUtils.trim(resultSet.getString("TABLE_TYPE")));
                table.setRemark(StringUtils.trim(resultSet.getString("REMARKS")));
                table.setFullName(this.generateTableName(table.getCatalog(), table.getSchema(), table.getName()));

                // 索引信息
                List<DatabaseIndex> indexs = this.getIndexs(connection, catalog, table.getSchema(), table.getName());

                // 主键信息
                List<DatabaseIndex> pks = this.getPrimaryIndex(connection, catalog, table.getSchema(), table.getName());

                Jdbc.removePrimaryKey(indexs, pks);
                table.setIndexs(indexs);
                table.setPrimaryIndexs(pks);

                // 列信息
                List<DatabaseTableColumn> columns = this.getTableColumns(connection, types, catalog, table.getSchema(), table.getName());
                table.setColumns(columns);
                list.add(table);
            }

            return list;
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", this.generateTableName(catalog, schema, tableName), e);
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    protected List<DatabaseTableColumn> getTableColumns(Connection connection, DatabaseTypeSet types, String catalog, String schema, String tableName) {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        tableName = this.parseIdentifier(tableName);

        ResultSet resultSet = null;
        try {
            List<DatabaseTableColumn> list = new ArrayList<DatabaseTableColumn>();
            resultSet = connection.getMetaData().getColumns(catalog, schema, tableName, null);

            while (resultSet.next()) {
                String COLUMN_NAME = resultSet.getString("COLUMN_NAME");
                int DATA_TYPE = resultSet.getInt("DATA_TYPE");
                String TYPE_NAME = resultSet.getString("TYPE_NAME");
                int COLUMN_SIZE = resultSet.getInt("COLUMN_SIZE");
                // int BUFFER_LENGTH = res.getInt("BUFFER_LENGTH");
                int DECIMAL_DIGITS = resultSet.getInt("DECIMAL_DIGITS");
                int NUM_PREC_RADIX = resultSet.getInt("NUM_PREC_RADIX");
//				int NULLABLE = resultSet.getInt("NULLABLE");
                String REMARKS = resultSet.getString("REMARKS");
                String COLUMN_DEF = resultSet.getString("COLUMN_DEF");
                // String SQL_DATA_TYPE = res.getString("SQL_DATA_TYPE");
                // String SQL_DATETIME_SUB = res.getString("SQL_DATETIME_SUB");
                int CHAR_OCTET_LENGTH = resultSet.getInt("CHAR_OCTET_LENGTH");
                int ORDINAL_POSITION = resultSet.getInt("ORDINAL_POSITION");
                String IS_NULLABLE = resultSet.getString("IS_NULLABLE");
                String IS_AUTOINCREMENT = resultSet.getString("IS_AUTOINCREMENT");
                // String SOURCE_DATA_TYPE = res.getString("SOURCE_DATA_TYPE");

                StandardDatabaseTableColumn column = new StandardDatabaseTableColumn();
                column.setName(COLUMN_NAME);
                column.setSqlType(DATA_TYPE);
                column.setFieldType(TYPE_NAME);
                column.setLength(COLUMN_SIZE);
                column.setRemark(REMARKS);
                column.setDefault(COLUMN_DEF);
                column.setMaxLength(CHAR_OCTET_LENGTH);
                column.setPosition(ORDINAL_POSITION);
                column.setNullAble(IS_NULLABLE);
                column.setDigit(DECIMAL_DIGITS);
                column.setRadix(NUM_PREC_RADIX);
                column.setTableCatalog(StringUtils.trimBlank(resultSet.getString("TABLE_CAT")));
                column.setTableSchema(StringUtils.trimBlank(resultSet.getString("TABLE_SCHEM")));
                column.setTableName(StringUtils.trimBlank(resultSet.getString("TABLE_NAME")));
                column.setTableFullName(this.generateTableName(column.getTableCatalog(), column.getTableSchema(), column.getTableName()));
                // col.setScopeCatlog(ST.trimBlank(res.getString("SCOPE_CATLOG")));
                // col.setScopeSchema(ST.trimBlank(res.getString("SCOPE_SCHEMA")));
                // col.setScopeTable(ST.trimBlank(res.getString("SCOPE_TABLE")));
                column.setIncrement(IS_AUTOINCREMENT);
                DatabaseType type = types.get(TYPE_NAME);
                if (type == null) {
                    throw new UnsupportedOperationException(TYPE_NAME);
                }
                list.add(column);
            }

            return list;
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", this.generateTableName(catalog, schema, tableName), e);
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    public String generateDeleteQuicklySQL(Connection connection, String catalog, String schema, String tableName) {
        return "delete from " + this.generateTableName(catalog, schema, tableName);
    }

    public String generateTableName(String catalog, String schema, String tableName) {
        return SQL.toTableName(schema, tableName);
    }

    public String generateIndexName(String catalog, String schema, String tableName) {
        return SQL.toTableName(schema, tableName);
    }

    public boolean containsTable(Connection connection, String catalog, String schema, String tableName) throws SQLException {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        tableName = this.parseIdentifier(tableName);

        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getTables(catalog, schema, tableName, null);
            return resultSet.next() && StringUtils.isNotBlank(resultSet.getString("TABLE_NAME"));
        } catch (SQLException e) {
            throw new DatabaseException("database.stdout.message004", this.generateTableName(catalog, schema, tableName), e);
        } finally {
            IO.closeQuietly(resultSet);
        }
    }

    public DatabaseProcedure getProcedureForceOne(Connection connection, String catalog, String schema, String procedureName) throws SQLException {
        catalog = this.parseIdentifier(catalog);
        schema = this.parseIdentifier(schema);
        procedureName = this.parseIdentifier(procedureName);

        List<DatabaseProcedure> list = this.getProcedures(connection, catalog, schema, procedureName);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() != 1) {
            throw new DatabaseException("database.stdout.message016", catalog, schema, procedureName, StringUtils.join(list, ", "));
        } else {
            return list.get(0);
        }
    }

    public boolean terminate(Connection conn, Properties p) throws SQLException {
        try {
            if (conn != null) {
                conn.close();
            }
            return true;
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(StringUtils.toString(p), e);
            }
            return false;
        }
    }

    public Properties getAttributes(Connection conn) {
        return new Properties();
    }

    public String parseIdentifier(String str) {
        if (str == null) {
            return str;
        }

        if (str.length() <= 1) {
            return str.toUpperCase();
        }

        int last = str.length() - 1;
        char f = str.charAt(0);
        char l = str.charAt(last);
        if ((f == '`' && l == '`') || (f == '\"' && l == '\"') || (f == '\'' && l == '\'')) {
            return str.substring(1, last);
        } else {
            return str.toUpperCase();
        }
    }
}
