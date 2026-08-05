package cn.org.expect.codegen.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private final List<ColumnDesign> columns;

    /**
     * 创建数据库表设计
     *
     * @param name 表名
     * @param description 表说明
     * @param sheetName 工作表名称
     * @param columns 字段列表
     */
    public TableDesign(String name, String description, String sheetName, List<ColumnDesign> columns) {
        this.name = name;
        this.description = description;
        this.sheetName = sheetName;
        this.columns = Collections.unmodifiableList(new ArrayList<ColumnDesign>(columns));
    }

    public String name() { return this.name; }
    public String description() { return this.description; }
    public String sheetName() { return this.sheetName; }
    public List<ColumnDesign> columns() { return this.columns; }
}
