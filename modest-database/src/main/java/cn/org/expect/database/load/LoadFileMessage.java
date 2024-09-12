package cn.org.expect.database.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.concurrent.EasyJobMessage;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.util.Attribute;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 装数功能的消息文件
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-02-18
 */
public class LoadFileMessage extends EasyJobMessage {

    /**
     * 初始化
     *
     * @param context 装数引擎上下文信息
     * @param file    表格型数据文件
     * @throws IOException 解析消息文件发生错误
     */
    public LoadFileMessage(LoadEngineContext context, TextTableFile file) throws IOException {
        super(tofile(context, file), file.getCharsetName());
    }

    /**
     * 返回消息文件
     *
     * @param context 装数引擎的上下文信息
     * @param file    待加载数据文件
     * @return 消息文件
     */
    private static File tofile(LoadEngineContext context, TextTableFile file) {
        File parent = file.getFile().getParentFile(); // 消息文件默认与数据文件在相同目录
        Attribute<String> attributes = context.getAttributes();
        if (attributes.contains("message")) {
            String msgfilepath = attributes.getAttribute("message");
            File msgfile = new File(msgfilepath);
            if (msgfilepath.endsWith("/") || msgfilepath.endsWith("\\")) {
                parent = msgfile;
            } else {
                return msgfile;
            }
        }

        String name = file.getFile().getName();
        String filename = FileUtils.changeFilenameExt(name, "msg");
        return new File(parent, filename);
    }

    /**
     * 设置启动时间
     */
    public void setStartTime(Date date) {
        if (date == null) {
            this.setAttribute("startTime", "");
        } else {
            this.setAttribute("startTime", Dates.format21(date));
        }
    }

    /**
     * 返回启动时间
     *
     * @return 启动时间
     */
    public Date getStartTime() {
        String date = this.getAttribute("startTime");
        return Dates.testParse(date);
    }

    /**
     * 设置数据库表归属的编目信息
     *
     * @param catalog 编目
     */
    public void setTableCatalog(String catalog) {
        this.setAttribute("tableCatalog", catalog);
    }

    /**
     * 返回数据库表归属的编目信息
     */
    public String getTableCatalog() {
        return this.getAttribute("tableCatalog");
    }

    /**
     * 设置数据库表的模式名
     *
     * @param schema 模式
     */
    public void setTableSchema(String schema) {
        this.setAttribute("tableSchema", schema);
    }

    /**
     * 返回数据库表的模式名
     *
     * @return 模式
     */
    public String getTableSchema() {
        return this.getAttribute("tableSchema");
    }

    /**
     * 设置数据库表的名
     *
     * @param name 表名
     */
    public void setTableName(String name) {
        this.setAttribute("tableName", name);
    }

    /**
     * 返回数据库表名
     *
     * @return 表名
     */
    public String getTableName() {
        return this.getAttribute("tableName");
    }

    /**
     * 设置数据库装载模式
     *
     * @param mode 装载模式
     */
    public void setLoadMode(LoadMode mode) {
        this.setAttribute("loadMode", mode == null ? "" : mode.getName());
    }

    /**
     * 返回数据装载模式
     *
     * @return 装载模式
     */
    public LoadMode getLoadMode() {
        String str = this.getAttribute("loadMode");
        return LoadMode.valueof(str);
    }

    /**
     * 设置数据库表的字段集合
     *
     * @param columns 表字段集合
     */
    public void setTableColumns(List<DatabaseTableColumn> columns) {
        StringBuilder buf = new StringBuilder();
        for (Iterator<DatabaseTableColumn> it = columns.iterator(); it.hasNext(); ) {
            DatabaseTableColumn col = it.next();
            buf.append(StringUtils.toCase(col.getName(), false, null));
            if (it.hasNext()) {
                buf.append(", ");
            }
        }
        this.setAttribute("tableColumns", buf.toString());
    }

    /**
     * 设置数据文件的字符集
     *
     * @param charsetName 字符集
     */
    public void setCharsetName(String charsetName) {
        this.setAttribute("charset", StringUtils.toCase(charsetName, false, null));
    }

    /**
     * 返回数据文件的字符集
     *
     * @return 字符集
     */
    public String getCharsetName() {
        return this.getAttribute("charset");
    }

    /**
     * 设置文件中字段个数
     *
     * @param column 字段个数
     */
    public void setColumn(int column) {
        this.setAttribute("column", String.valueOf(column));
    }

    /**
     * 返回文件中字段个数
     *
     * @return 字段个数
     */
    public String getColumn() {
        return this.getAttribute("column");
    }

    /**
     * 设置数据文件的绝对路径
     *
     * @param file 数据文件
     */
    public void setFile(TextTableFile file) {
        this.setAttribute("filePath", file == null ? "" : file.getAbsolutePath());
    }

    /**
     * 返回数据文件的绝对路径
     *
     * @return 文件绝对路径
     */
    public String getFile() {
        return this.getAttribute("filePath");
    }

    /**
     * 设置数据文件最近修改时间
     *
     * @param time 修改时间
     */
    public void setFileModified(long time) {
        String date = Dates.format21(new Date(time));
        this.setAttribute("fileModified", date);
    }

    /**
     * 返回数据文件最近修改时间
     *
     * @return 修改时间
     */
    public Date getFileModified() {
        String str = this.getAttribute("fileModified");
        return Dates.testParse(str);
    }

    /**
     * 设置文件类型
     *
     * @param type 文件类型
     */
    public void setFileType(String type) {
        this.setAttribute("fileType", StringUtils.toCase(type, false, null));
    }

    /**
     * 返回文件类型
     *
     * @return 文件类型
     */
    public String getFileType() {
        String str = this.getAttribute("fileType");
        return str == null ? "" : str;
    }

    /**
     * 设置文件字段顺序
     *
     * @param positions 位置信息数组
     */
    public void setFileColumns(int[] positions) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < positions.length; ) {
            int val = positions[i];
            buf.append(val);
            if (++i < positions.length) {
                buf.append(", ");
            }
        }
        this.setAttribute("fileColumn", buf.toString());
    }

    /**
     * 返回文件中装载数据的范围集合
     *
     * @return 装载数据的范围集合
     * @throws IOException 解析表达式错误
     */
    public List<LoadFileRange> getFileFailRanage() throws IOException {
        String str = this.getAttribute("fileRange");
        List<LoadFileRange> list = LoadFileRange.parseString(str);
        List<LoadFileRange> result = new ArrayList<LoadFileRange>();
        for (LoadFileRange obj : list) {
            int status = obj.getStatus();
            if (status == 1) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * 设置未装载数据范围
     *
     * @param ranges 未装载数据范围
     */
    public void setFileRange(List<LoadFileRange> ranges) {
        String str = LoadFileRange.toString(ranges);
        this.setAttribute("fileRange", str);
    }

    /**
     * 返回已读取行数
     *
     * @return 已读取行数
     */
    public long getReadRows() {
        String str = this.getAttribute("readRow");
        return StringUtils.isLong(str) ? Long.parseLong(str) : null;
    }

    /**
     * 保存已读取行数
     *
     * @param line 已读取行数
     */
    public void setReadRows(long line) {
        this.setAttribute("readRow", String.valueOf(line));
    }

    /**
     * 返回已提交到数据库表的行数
     *
     * @return 已提交到数据库表的行数
     */
    public long getCommitRows() {
        String str = this.getAttribute("commitRow");
        return StringUtils.isLong(str) ? Long.parseLong(str) : null;
    }

    /**
     * 保存已提交到数据库表的行数
     *
     * @param line 已提交到数据库表的行数
     */
    public void setCommitRows(long line) {
        this.setAttribute("commitRow", String.valueOf(line));
    }

    /**
     * 返回装载数据错误行数
     *
     * @return 装载数据错误行数
     */
    public long getErrorRows() {
        String str = this.getAttribute("errorRow");
        return StringUtils.isLong(str) ? Long.parseLong(str) : null;
    }

    /**
     * 保存装载数据错误行数
     *
     * @param line 装载数据错误行数
     */
    public void setErrorRows(long line) {
        this.setAttribute("errorRow", String.valueOf(line));
    }

    /**
     * 返回已忽略数据行数
     *
     * @return 已忽略数据行数
     */
    public long getSkipRows() {
        String str = this.getAttribute("skipRow");
        return StringUtils.isLong(str) ? Long.parseLong(str) : null;
    }

    /**
     * 保存已忽略数据行数
     *
     * @param line 已忽略数据行数
     */
    public void setSkipRows(long line) {
        this.setAttribute("skipRow", String.valueOf(line));
    }

    /**
     * 返回已删除数据行数
     *
     * @return 已删除数据行数
     */
    public long getDeleteRows() {
        String str = this.getAttribute("deleteRow");
        return StringUtils.isLong(str) ? Long.parseLong(str) : null;
    }

    /**
     * 保存已删除数据行数
     *
     * @param line 已删除数据行数
     */
    public void setDeleteRows(long line) {
        this.setAttribute("deleteRow", String.valueOf(line));
    }

    /**
     * 保存加载数据所用时间，单位：秒
     */
    public void setEndTime(Date date) {
        if (date == null) {
            this.setAttribute("endTime", "");
        } else {
            this.setAttribute("endTime", Dates.format21(date));
        }
    }

    /**
     * 返回终止时间
     *
     * @return 终止时间
     */
    public Date getEndTime() {
        String date = this.getAttribute("endTime");
        return Dates.testParse(date);
    }

}
