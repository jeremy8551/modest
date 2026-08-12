package cn.org.expect.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;

/**
 * 工作表内容处理器
 *
 * @author jeremy8551@gmail.com
 */
public class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    private static final Set<String> REQUIRED_HEADERS = new HashSet<String>(Arrays.asList("表名", "字段名", "类型", "说明"));

    private static final int MAX_HEADER_SCAN_ROWS = 30;

    private final String sheetName;
    private final Map<Integer, String> rowValues = new HashMap<Integer, String>();
    private final Map<String, Integer> headers = new HashMap<String, Integer>();
    private final List<TableColumnDesign> columns = new ArrayList<TableColumnDesign>();
    private String tableName = "";
    private String tableDescription;
    private boolean headerFound;
    private int rowNumber = -1;

    public SheetHandler(String sheetName) {
        this.sheetName = sheetName;
        this.tableDescription = sheetName;
    }

    public void startRow(int rowNum) {
        this.rowNumber = rowNum;
        this.rowValues.clear();
    }

    public void endRow() {
        if (!this.headerFound) {
            if (this.rowNumber >= MAX_HEADER_SCAN_ROWS) {
                throw new DatabaseDesignParser.StopSheetException();
            }

            Map<String, Integer> candidateHeaders = new HashMap<String, Integer>();
            for (Map.Entry<Integer, String> entry : this.rowValues.entrySet()) {
                candidateHeaders.put(StringUtils.trimBlank(entry.getValue()), entry.getKey());
            }

            if (candidateHeaders.keySet().containsAll(REQUIRED_HEADERS)) {
                this.headers.putAll(candidateHeaders);
                this.headerFound = true;
            }
            return;
        }

        String columnName = this.value("字段名");
        if (columnName.length() == 0) {
            if (!this.columns.isEmpty()) {
                throw new DatabaseDesignParser.StopSheetException();
            }
            return;
        }

        this.tableName = StringUtils.coalesce(this.tableName, this.value("表名"));
        this.tableDescription = StringUtils.coalesce(this.value("表说明"), this.tableDescription);

        String sqlType = this.value("类型");
        Ensure.notBlank(sqlType, "工作表 {} 的“{}”类型不能为空", this.sheetName, columnName);

        this.columns.add( //
            new TableColumnDesign( //
                columnName,  //
                sqlType,  //
                DBTypeMapper.mapping(sqlType), //
                this.value("说明"),  //
                this.isYes("主键"),  //
                this.isYes("唯一索引"),  //
                this.isYes("业务字段"),  //
                this.isYes("not null"),  //
                this.value("默认值"),  //
                this.value("格式"),  //
                this.value("数据字典"),  //
                this.value("虚拟字段"),  //
                this.value("备注") //
            ) //
        );
    }

    public void cell(String cellReference, String formattedValue) {
        int columnIndex = new CellReference(cellReference).getCol();
        this.rowValues.put(Integer.valueOf(columnIndex), formattedValue == null ? "" : formattedValue.trim());
    }

    public void headerFooter(String text, boolean isHeader, String tagName) {
    }

    public TableDesign toTableDesign() {
        if (!this.headerFound) {
            return null;
        }

        if (this.tableName.length() == 0 || this.columns.isEmpty()) {
            throw new IllegalArgumentException("工作表 " + this.sheetName + " 缺少有效表名或字段");
        }
        return new TableDesign(this.tableName, this.tableDescription, this.sheetName, this.columns);
    }

    private boolean isYes(String header) {
        return "是".equalsIgnoreCase(this.value(header));
    }

    private String value(String header) {
        Integer index = this.headers.get(header);
        String value = index == null ? null : this.rowValues.get(index);
        return value == null ? "" : value;
    }
}
