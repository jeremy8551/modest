package cn.org.expect.codegen.model;

/**
 * 数据库字段设计
 */
public class ColumnDesign {

    /** 字段名 */
    private final String name;

    /** 数据库类型 */
    private final String sqlType;

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

    /** 数据字典 */
    private final String dictionary;

    /** 虚拟字段表达式 */
    private final String virtualField;

    /** 备注 */
    private final String remark;

    /**
     * 创建数据库字段设计
     *
     * @param name 字段名
     * @param sqlType 数据库类型
     * @param description 字段说明
     * @param primaryKey 是否为主键
     * @param uniqueIndex 是否为唯一索引
     * @param notNull 是否不允许为空
     * @param defaultValue 默认值
     * @param dictionary 数据字典
     * @param virtualField 虚拟字段表达式
     * @param remark 备注
     */
    public ColumnDesign(String name, String sqlType, String description, boolean primaryKey, boolean uniqueIndex,
                        boolean notNull, String defaultValue, String dictionary, String virtualField, String remark) {
        this.name = name;
        this.sqlType = sqlType;
        this.description = description;
        this.primaryKey = primaryKey;
        this.uniqueIndex = uniqueIndex;
        this.notNull = notNull;
        this.defaultValue = defaultValue;
        this.dictionary = dictionary;
        this.virtualField = virtualField;
        this.remark = remark;
    }

    public String name() { return this.name; }
    public String sqlType() { return this.sqlType; }
    public String description() { return this.description; }
    public boolean primaryKey() { return this.primaryKey; }
    public boolean uniqueIndex() { return this.uniqueIndex; }
    public boolean notNull() { return this.notNull; }
    public String defaultValue() { return this.defaultValue; }
    public String dictionary() { return this.dictionary; }
    public String virtualField() { return this.virtualField; }
    public String remark() { return this.remark; }
}
