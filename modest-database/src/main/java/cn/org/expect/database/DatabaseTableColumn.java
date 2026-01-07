package cn.org.expect.database;

/**
 * 表示数据库表中某列的配置信息
 *
 * @author jeremy8551@gmail.com
 */
public interface DatabaseTableColumn extends Cloneable {

    /**
     * 表名称
     *
     * @return 表名称
     */
    String getTableName();

    /**
     * 表名称
     *
     * @param tableName 表名称
     */
    void setTableName(String tableName);

    /**
     * 表模式（可为 null）
     *
     * @return 表模式
     */
    String getTableSchema();

    /**
     * 表模式（可为 null）
     *
     * @param tableSchema 表模式
     */
    void setTableSchema(String tableSchema);

    /**
     * 编目信息（可为 null）
     *
     * @return 编目信息
     */
    String getTableCatalog();

    /**
     * 返回表全名
     *
     * @return 表全名
     */
    String getTableFullName();

    /**
     * 设置表全名
     *
     * @param tableName 表全名
     */
    void setTableFullName(String tableName);

    /**
     * 编目（可为 null）
     *
     * @param tableCatalog 编目
     */
    void setTableCatalog(String tableCatalog);

    /**
     * 列名称
     *
     * @param name 列名称
     */
    void setName(String name);

    /**
     * 列名称
     *
     * @return 列名称
     */
    String getName();

    /**
     * "NO" 表示明确不允许列使用 NULL 值，"YES" 表示可能允许列使用 NULL 值。空字符串表示没人知道是否允许使用 null 值。
     *
     * @return "NO" 表示明确不允许列使用 NULL 值，"YES" 表示可能允许列使用 NULL 值。空字符串表示没人知道是否允许使用 null 值。
     */
    String getNullAble();

    /**
     * "NO" 表示明确不允许列使用 NULL 值，"YES" 表示可能允许列使用 NULL 值。空字符串表示没人知道是否允许使用 null 值。
     *
     * @param str 字符串
     */
    void setNullAble(String str);

    /**
     * 来自 java.sql.Types 的 SQL 类型
     *
     * @return 来自 java.sql.Types 的 SQL 类型
     */
    int getSqlType();

    /**
     * 来自 {@linkplain java.sql.Types} 的 SQL 类型
     *
     * @param sqlType {@linkplain java.sql.Types}
     */
    void setSqlType(int sqlType);

    /**
     * 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的；如：char varchar int decimal 等
     *
     * @return 字段的类型名
     */
    String getFieldType();

    /**
     * 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
     *
     * @param typeName 类型名称
     */
    void setFieldType(String typeName);

    /**
     * 列的长度。对于 char 或 date 类型，列的大小是最大字符数，对于 numeric 和 decimal 类型，列的大小就是精度。
     *
     * @return 列的长度
     */
    int length();

    /**
     * 描述列的注释（可为 null）
     *
     * @return 列的注释
     */
    String getRemark();

    /**
     * 描述列的注释（可为 null）
     *
     * @param remarks 注释
     */
    void setRemark(String remarks);

    /**
     * 设置字段的默认值（可为 null）
     *
     * @param defaultValue 字段的默认值
     */
    void setDefault(String defaultValue);

    /**
     * 返回字段的默认值（可为 null），引号扩起来时表示字符串
     *
     * @return 字段的默认值
     */
    String getDefault();

    /**
     * 返回字段默认值的定义表达式, 如: 'text' 10
     *
     * @return 字段默认值的定义表达式
     */
    String getDefaultValue();

    /**
     * CHAR_OCTET_LENGTH 对于 char 类型，该长度是列中的最大字节数
     *
     * @return 最大字节数
     */
    int getMaxLength();

    /**
     * CHAR_OCTET_LENGTH 对于 char 类型，该长度是列中的最大字节数
     *
     * @param length 最大字节数
     */
    void setMaxLength(int length);

    /**
     * 表中的列的位置信息
     *
     * @return 位置信息，从1开始
     */
    int getPosition();

    /**
     * 表中的列的索引（从 1 开始）
     *
     * @param position 索引信息
     */
    void setPosition(int position);

    /**
     * 小数部分的位数
     *
     * @return 小数部分的位数
     */
    int getDigit();

    /**
     * 小数部分的位数
     *
     * @param n 小数部分的位数
     */
    void setDigit(int n);

    /**
     * 基数（通常为 10 或 2）
     *
     * @return 基数
     */
    int getRadix();

    /**
     * 比较
     *
     * @param column 数据库表列信息
     * @return 0表示字段相同 小于零表示字段1小于字段2 大于零表示字段1大于字段2
     */
    int compareTo(DatabaseTableColumn column);

    /**
     * 列的大小。对于 char 或 date 类型，列的大小是最大字符数，对于 numeric 和 decimal 类型，列的大小就是精度。
     *
     * @param length 列的大小
     */
    void setLength(int length);

    /**
     * 基数（通常为 10 或 2）
     *
     * @param n 基数
     */
    void setRadix(int n);

    /**
     * YES---如果列是自动递增的 <br>
     * NO ---如果列未自动递增 <br>
     * 空字符串---如果无法确定列是否自动递增 <br>
     *
     * @param incr 是否是自增字段
     */
    void setIncrement(String incr);

    /**
     * YES---如果列是自动递增的 <br>
     * NO ---如果列未自动递增 <br>
     * 空字符串---如果无法确定列是否自动递增 <br>
     *
     * @return 是否是自增字段
     */
    String getIncrement();

    /**
     * 返回一个副本
     *
     * @return 副本
     */
    DatabaseTableColumn clone();

    /**
     * 返回字段类型信息
     *
     * @return 字段类型
     */
    DatabaseType getType();
}
