package cn.org.expect.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 按列名流式读取数据库设计工作簿
 */
public class DatabaseDesignParser {

    /**
     * 读取数据库表设计
     *
     * @param workbookFile 工作簿文件
     * @return 数据库表设计列表
     * @throws IOException 工作簿读取失败时抛出
     */
    public List<TableDesign> execute(File workbookFile) throws Exception {
        OPCPackage packageFile = OPCPackage.open(workbookFile, PackageAccess.READ);
        try {
            XSSFReader reader = new XSSFReader(packageFile);
            StylesTable styles = reader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(packageFile);
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
            List<TableDesign> tables = new ArrayList<TableDesign>();
            while (sheets.hasNext()) {
                InputStream in = sheets.next();
                try {
                    SheetHandler handler = new SheetHandler(sheets.getSheetName());

                    XMLReader parser = XMLReaderFactory.createXMLReader();
                    parser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, handler, new DataFormatter(), false));
                    try {
                        parser.parse(new InputSource(in));
                    } catch (StopSheetException ignored) {
                        // 找到数据区后的第一个空字段行即可停止，避免解析仅含残余格式的百万行工作表
                    }

                    TableDesign table = handler.toTableDesign();
                    if (table != null) {
                        tables.add(table);
                    }
                } finally {
                    in.close();
                }
            }
            return tables;
        } finally {
            packageFile.close();
        }
    }

    /** 用于提前结束工作表解析的内部异常 */
    public static final class StopSheetException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }
}
