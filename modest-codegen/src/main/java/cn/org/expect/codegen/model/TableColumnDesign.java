package cn.org.expect.codegen.model;

/**
 * 数据库字段设计
 */
public class TableColumnDesign {

    /** 字段名 */
    private final String name;

    /** 数据库类型 */
    private final String sqlType;

    /** java类型 */
    private final String javaType;

    /** 字段说明 */
    private final String description;

    /** 是否为主键 */
    private final boolean primaryKey;

    /** 是否为唯一索引 */
    private final boolean uniqueIndex;

    /** 是否不允许为空 */
    private final boolean notNull;

    /** 默认值 */
    private final String defaultValue;

    /** 字段格式 */
    private final String format;

    /** 数据字典 */
    private final String dictionary;

    /** 虚拟字段表达式 */
    private final String virtualField;

    /** 备注 */
    private final String remark;

    /**
     * 创建数据库字段设计
     *
     * @param name         字段名
     * @param sqlType      数据库类型
     * @param javaType     java类型
     * @param description  字段说明
     * @param primaryKey   是否为主键
     * @param uniqueIndex  是否为唯一索引
     * @param notNull      是否不允许为空
     * @param defaultValue 默认值
     * @param format       字段格式
     * @param dictionary   数据字典
     * @param virtualField 虚拟字段表达式
     * @param remark       备注
     */
    public TableColumnDesign(String name, String sqlType, String javaType, String description, boolean primaryKey, boolean uniqueIndex, boolean notNull, String defaultValue, String format, String dictionary, String virtualField, String remark) {
        this.name = name;
        this.sqlType = sqlType;
        this.javaType = javaType;
        this.description = description;
        this.primaryKey = primaryKey;
        this.uniqueIndex = uniqueIndex;
        this.notNull = notNull;
        this.defaultValue = defaultValue;
        this.format = format;
        this.dictionary = dictionary;
        this.virtualField = virtualField;
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public String getSqlType() {
        return sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isUniqueIndex() {
        return uniqueIndex;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getFormat() {
        return format;
    }

    public String getDictionary() {
        return dictionary;
    }

    public String getVirtualField() {
        return virtualField;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return TableColumnDesign.class.getSimpleName() + "{" + //
            "name='" + name + '\'' + //
            ", sqlType='" + sqlType + '\'' + //
            ", description='" + description + '\'' + //
            ", primaryKey=" + primaryKey + //
            ", uniqueIndex=" + uniqueIndex + //
            ", notNull=" + notNull + //
            ", defaultValue='" + defaultValue + '\'' + //
            ", format='" + format + '\'' + //
            ", dictionary='" + dictionary + '\'' + //
            ", virtualField='" + virtualField + '\'' + //
            ", remark='" + remark + '\'' + //
            '}';
    }
}
