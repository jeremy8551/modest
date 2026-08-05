package cn.org.expect.codegen.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.codegen.model.ColumnDesign;
import cn.org.expect.codegen.model.TableDesign;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 按列名流式读取数据库设计工作簿
 */
public class DatabaseDesignReader {

    private static final Set<String> REQUIRED_HEADERS = new HashSet<String>(Arrays.asList(new String[]{"表名", "字段名", "类型", "说明"}));
    private static final int MAX_HEADER_SCAN_ROWS = 30;

    /**
     * 读取数据库表设计
     *
     * @param workbookFile 工作簿文件
     * @return 数据库表设计列表
     * @throws IOException 工作簿读取失败时抛出
     */
    public List<TableDesign> read(File workbookFile) throws IOException {
        OPCPackage packageFile = null;
        try {
            packageFile = OPCPackage.open(workbookFile, PackageAccess.READ);
            XSSFReader reader = new XSSFReader(packageFile);
            StylesTable styles = reader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(packageFile);
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
            List<TableDesign> tables = new ArrayList<TableDesign>();
            while (sheets.hasNext()) {
                InputStream sheetStream = sheets.next();
                try {
                    SheetHandler handler = new SheetHandler(sheets.getSheetName());
                    this.parseSheet(styles, strings, sheetStream, handler);
                    TableDesign table = handler.toTableDesign();
                    if (table != null) {
                        tables.add(table);
                    }
                } finally {
                    sheetStream.close();
                }
            }
            return tables;
        } catch (SAXException exception) {
            throw new IOException("解析数据库设计工作簿失败", exception);
        } catch (IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException("读取数据库设计工作簿失败", exception);
        } finally {
            if (packageFile != null) {
                packageFile.close();
            }
        }
    }

    /**
     * 解析单个工作表
     *
     * @param styles      样式表
     * @param strings     共享字符串表
     * @param sheetStream 工作表输入流
     * @param handler     工作表处理器
     * @throws SAXException XML 解析失败时抛出
     * @throws IOException  读取失败时抛出
     */
    private void parseSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetStream, SheetHandler handler) throws SAXException, IOException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, handler, new DataFormatter(), false));
        try {
            parser.parse(new InputSource(sheetStream));
        } catch (StopSheetException ignored) {
            // 找到数据区后的第一个空字段行即可停止，避免解析仅含残余格式的百万行工作表
        }
    }

    /** 工作表内容处理器 */
    private static final class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private final String sheetName;
        private final Map<Integer, String> rowValues = new HashMap<Integer, String>();
        private final Map<String, Integer> headers = new HashMap<String, Integer>();
        private final List<ColumnDesign> columns = new ArrayList<ColumnDesign>();
        private String tableName = "";
        private String tableDescription;
        private boolean headerFound;
        private int rowNumber = -1;

        private SheetHandler(String sheetName) {
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
                    throw new StopSheetException();
                }
                Map<String, Integer> candidateHeaders = new HashMap<String, Integer>();
                for (Map.Entry<Integer, String> entry : this.rowValues.entrySet()) {
                    candidateHeaders.put(entry.getValue().trim(), entry.getKey());
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
                    throw new StopSheetException();
                }
                return;
            }
            this.tableName = firstNonBlank(this.tableName, this.value("表名"));
            this.tableDescription = firstNonBlank(this.value("表说明"), this.tableDescription);
            String sqlType = this.value("类型");
            if (sqlType.length() == 0) {
                throw new IllegalArgumentException("工作表 " + this.sheetName + " 的“" + columnName + "”类型不能为空");
            }
            this.columns.add(new ColumnDesign(columnName, sqlType, this.value("说明"), this.isYes("主键"), this.isYes("唯一索引"), this.isYes("not null"), this.value("默认值"), this.value("数据字典"), this.value("虚拟字段"), this.value("备注")));
        }

        public void cell(String cellReference, String formattedValue) {
            int columnIndex = new CellReference(cellReference).getCol();
            this.rowValues.put(Integer.valueOf(columnIndex), formattedValue == null ? "" : formattedValue.trim());
        }

        public void headerFooter(String text, boolean isHeader, String tagName) {
        }

        private TableDesign toTableDesign() {
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

        private static String firstNonBlank(String preferred, String fallback) {
            return preferred == null || preferred.trim().length() == 0 ? fallback : preferred;
        }
    }

    /** 用于提前结束工作表解析的内部异常 */
    private static final class StopSheetException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }
}
