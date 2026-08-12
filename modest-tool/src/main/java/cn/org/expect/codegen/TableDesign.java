package cn.org.expect.codegen;

import java.util.Collections;
import java.util.List;

import cn.org.expect.util.StringUtils;

/**
 * 数据库表设计
 */
public class TableDesign {

    /** 表名 */
    private final String name;

    /** 表说明 */
    private final String description;

    /** 工作表名称 */
    private final String sheetName;

    /** 字段列表 */
    private final List<TableColumnDesign> columns;

    /**
     * 创建数据库表设计
     *
     * @param name        表名
     * @param description 表说明
     * @param sheetName   工作表名称
     * @param columns     字段列表
     */
    public TableDesign(String name, String description, String sheetName, List<TableColumnDesign> columns) {
        this.name = name;
        this.description = description;
        this.sheetName = sheetName;
        this.columns = Collections.unmodifiableList(columns);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<TableColumnDesign> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return TableDesign.class.getSimpleName() + "{" + //
            "name='" + name + '\'' + //
            ", description='" + description + '\'' + //
            ", sheetName='" + sheetName + '\'' + //
            ", columns=[\n" + StringUtils.join(this.columns, ",\n") + "\n]" + //
            '}' //
            ;
    }
}
