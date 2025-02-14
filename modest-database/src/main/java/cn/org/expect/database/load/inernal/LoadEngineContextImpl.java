package cn.org.expect.database.load.inernal;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import cn.org.expect.database.JdbcConverterMapper;
import cn.org.expect.database.load.IndexMode;
import cn.org.expect.database.load.LoadEngineContext;
import cn.org.expect.database.load.LoadMode;
import cn.org.expect.printer.Progress;
import cn.org.expect.util.Attribute;
import cn.org.expect.util.IO;

/**
 * 装数引擎上下文信息
 *
 * @author jeremy8551@gmail.com
 */
public class LoadEngineContextImpl implements LoadEngineContext {

    private String name;
    private DataSource dataSource;
    private String datatype;
    private LoadMode mode;
    private String tableCatalog;
    private String tableName;
    private String tableSchema;
    private String errorTableName;
    private String errorTableSchema;
    private boolean statistics;
    private IndexMode indexMode;
    private Progress progress;
    private final List<String> files;
    private final List<String> dataColumn;
    private final List<String> fileColumn;
    private JdbcConverterMapper converters;
    private List<String> indexColumn;
    private boolean noRepeat;
    private long saveCount;
    private int readBuffer;
    private Attribute<String> attrs;

    /**
     * 初始化上下文信息
     */
    public LoadEngineContextImpl() {
        this.name = "";
        this.files = new ArrayList<String>();
        this.dataColumn = new ArrayList<String>();
        this.fileColumn = new ArrayList<String>();
        this.readBuffer = IO.FILE_BYTES_BUFFER_SIZE;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public Attribute<String> getAttributes() {
        return attrs;
    }

    public void setAttributes(Attribute<String> attrs) {
        this.attrs = attrs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReadBuffer() {
        return readBuffer;
    }

    public void setReadBuffer(int readBuffer) {
        this.readBuffer = readBuffer;
    }

    public long getSavecount() {
        return saveCount;
    }

    public void setSavecount(long savecount) {
        this.saveCount = savecount;
    }

    public LoadMode getLoadMode() {
        return mode;
    }

    public void setLoadMode(LoadMode mode) {
        this.mode = mode;
    }

    public void setFiles(List<String> list) {
        this.files.clear();
        this.files.addAll(list);
    }

    public List<String> getFiles() {
        return this.files;
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

    public void setTableSchema(String schema) {
        this.tableSchema = schema;
    }

    public List<String> getIndexColumn() {
        return indexColumn;
    }

    public void setIndexColumn(List<String> mergeColumn) {
        this.indexColumn = mergeColumn;
    }

    public List<String> getFileColumn() {
        return fileColumn;
    }

    public void setFileColumn(List<String> dataColumn) {
        this.fileColumn.clear();
        this.fileColumn.addAll(dataColumn);
    }

    public List<String> getTableColumn() {
        return this.dataColumn;
    }

    public void setTableColumn(List<String> colomns) {
        this.dataColumn.clear();
        this.dataColumn.addAll(colomns);
    }

    public void setErrorTableName(String errorTableName) {
        this.errorTableName = errorTableName;
    }

    public String getErrorTableName() {
        return errorTableName;
    }

    public String getErrorTableSchema() {
        return errorTableSchema;
    }

    public void setErrorTableSchema(String errorTableSchema) {
        this.errorTableSchema = errorTableSchema;
    }

    public IndexMode getIndexMode() {
        return indexMode;
    }

    public void setIndexMode(IndexMode indexMode) {
        this.indexMode = indexMode;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setFiletype(String type) {
        this.datatype = type;
    }

    public String getFiletype() {
        return datatype;
    }

    public void setStatistics(boolean value) {
        this.statistics = value;
    }

    public boolean isStatistics() {
        return this.statistics;
    }

    public JdbcConverterMapper getConverters() {
        return converters;
    }

    public void setConverters(JdbcConverterMapper obj) {
        this.converters = obj;
    }

    public boolean isNorepeat() {
        return noRepeat;
    }

    public void setNorepeat(boolean norepeat) {
        this.noRepeat = norepeat;
    }

    public String toString() {
        return "StandardLoaderContext [name=" + name + ", dataSource=" + dataSource + ", datatype=" + datatype + ", mode=" + mode + ", tableName=" + tableName + ", tableSchema=" + tableSchema + ", errorTableName=" + errorTableName + ", errorTableSchema=" + errorTableSchema + ", statistics=" + statistics + ", indexMode=" + indexMode + ", progress=" + progress + ", files=" + files + ", dataColumn=" + dataColumn + ", fileColumn=" + fileColumn + ", attributes=" + this.attrs + ", converters=" + converters + ", mergeColumn=" + indexColumn + "]";
    }
}
