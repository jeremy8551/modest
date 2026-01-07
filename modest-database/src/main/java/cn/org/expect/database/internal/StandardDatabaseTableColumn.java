package cn.org.expect.database.internal;

import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.DatabaseType;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 数据库表字段信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-03-06
 */
public class StandardDatabaseTableColumn implements Cloneable, Comparable<DatabaseTableColumn>, DatabaseTableColumn {
    private final static Log log = LogFactory.getLog(StandardDatabaseTableColumn.class);

    private String name;

    private int sqlType;

    private String typeName;

    private int length;

    private String remarks;

    private String defaultValue;

    private int maxLength;

    private int position;

    private String isNullAble;

    private int digits;

    private int numPrecRadix;

    private String tableName;

    private String tableSchema;

    private String tableCatalog;

    private String tableFullName;

    private String autoIncrement;

    private DatabaseType type;

    public DatabaseType getType() {
        return type;
    }

    public String getTableFullName() {
        return tableFullName;
    }

    public void setTableFullName(String tableName) {
        this.tableFullName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNullAble() {
        return isNullAble;
    }

    public void setNullAble(String str) {
        this.isNullAble = str == null ? "" : str.toUpperCase();
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public String getFieldType() {
        return typeName;
    }

    public void setFieldType(String typeName) {
        this.typeName = typeName;
    }

    public int length() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getRemark() {
        return remarks;
    }

    public void setRemark(String remarks) {
        this.remarks = remarks;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefault() {
        return defaultValue;
    }

    public String getDefaultValue() {
        return this.type.toText(StringUtils.coalesce(this.getDefault(), ""));
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int length) {
        this.maxLength = length;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDigit() {
        return digits;
    }

    public void setDigit(int n) {
        this.digits = n;
    }

    public int getRadix() {
        return numPrecRadix;
    }

    public void setRadix(int n) {
        this.numPrecRadix = n;
    }

    public String getIncrement() {
        return autoIncrement;
    }

    public void setIncrement(String incr) {
        this.autoIncrement = incr;
    }

    public String toString() {
        return this.name + "[" + this.position + "]";
    }

    public int compareTo(DatabaseTableColumn column) {
        return this.compareTo(this, column);
    }

    /**
     * 比较字段信息是否一致
     *
     * @param column1 字段1
     * @param column2 字段2
     * @return 0表示字段相同 小于零表示字段1小于字段2 大于零表示字段1大于字段2
     */
    private int compareTo(DatabaseTableColumn column1, DatabaseTableColumn column2) {
        Ensure.notNull(column1);
        Ensure.notNull(column2);
        String name1 = column1.getTableFullName();
        String name2 = column2.getTableFullName();

        int nameCompare = column1.getName().compareTo(column2.getName());
        if (nameCompare != 0) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName());
            }
            return nameCompare;
        }

        if (column1.getSqlType() != column2.getSqlType()) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.getSqlType(), column2.getSqlType());
            }
            return column1.getSqlType() - column2.getSqlType();
        }

        int typeNameCompare = column1.getFieldType().compareTo(column2.getFieldType());
        if (typeNameCompare != 0) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.getFieldType(), column2.getFieldType());
            }
            return typeNameCompare;
        }

        if (column1.length() != column2.length()) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.length(), column2.length());
            }
            return column1.length() - column2.length();
        }

        if (column1.getMaxLength() != column2.getMaxLength()) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.getMaxLength(), column2.getMaxLength());
            }
            return column1.getMaxLength() - column2.getMaxLength();
        }

        if (column1.getPosition() != column2.getPosition()) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.getPosition(), column2.getPosition());
            }
            return column1.getPosition() - column2.getPosition();
        }

        if (column1.getDigit() != column2.getDigit()) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.getDigit(), column2.getDigit());
            }
            return column1.getDigit() - column2.getDigit();
        }

        if (column1.getRadix() != column2.getRadix()) {
            if (log.isDebugEnabled()) {
                log.debug("database.stdout.message026", name1, column1.getPosition(), column1.getName(), name2, column2.getPosition(), column2.getName(), column1.getRadix(), column2.getRadix());
            }
            return column1.getRadix() - column2.getRadix();
        }

        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DatabaseTableColumn) {
            DatabaseTableColumn c = (DatabaseTableColumn) obj;
            return this.name.equals(c.getName()) //
                && this.sqlType == c.getSqlType() //
                && this.typeName.equalsIgnoreCase(c.getFieldType()) //
                && this.length == c.length() //
//					&& this.isNullEnable == c.getNullEnable() //
                && this.remarks.equalsIgnoreCase(c.getRemark()) //
                && this.defaultValue.equals(c.getDefault()) //
                && this.maxLength == c.getMaxLength() //
                && this.position == c.getPosition() //
                && this.isNullAble.equalsIgnoreCase(c.getNullAble()) //
                && this.digits == c.getDigit() //
                && this.numPrecRadix == c.getRadix() //
                && this.tableName.equalsIgnoreCase(c.getTableName()) //
                && this.tableSchema.equalsIgnoreCase(c.getTableSchema()) //
                && this.tableCatalog.equalsIgnoreCase(c.getTableCatalog()) //
                && this.tableFullName.equalsIgnoreCase(c.getTableFullName()) //
                && this.autoIncrement.equalsIgnoreCase(c.getIncrement()) //
                ;
        } else {
            return false;
        }
    }

    public StandardDatabaseTableColumn clone() {
        StandardDatabaseTableColumn col = new StandardDatabaseTableColumn();
        col.name = this.name;
        col.sqlType = this.sqlType;
        col.typeName = this.typeName;
        col.length = this.length;
        col.remarks = this.remarks;
        col.defaultValue = this.defaultValue;
        col.maxLength = this.maxLength;
        col.position = this.position;
        col.isNullAble = this.isNullAble;
        col.digits = this.digits;
        col.numPrecRadix = this.numPrecRadix;
        col.tableName = this.tableName;
        col.tableSchema = this.tableSchema;
        col.tableCatalog = this.tableCatalog;
        col.tableFullName = this.tableFullName;
        col.autoIncrement = this.autoIncrement;
        col.type = this.type;
        return col;
    }
}
