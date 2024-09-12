package cn.org.expect.database.export;

import java.io.File;
import java.io.IOException;

import cn.org.expect.concurrent.EasyJobMessage;
import cn.org.expect.database.SQL;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 卸数功能的消息对象
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-02-18
 */
public class ExtractMessage extends EasyJobMessage {

    /**
     * 初始化
     *
     * @param logfile     日志文件
     * @param charsetName 日志文件字符集
     */
    public ExtractMessage(File logfile, String charsetName) throws IOException {
        super(logfile, charsetName);
    }

    /**
     * 设置数据保存位置信息
     *
     * @param filepath 位置信息
     */
    public void setTarget(String filepath) {
        this.setAttribute("target", filepath);
    }

    /**
     * 返回数据保存位置信息
     *
     * @return 位置信息
     */
    public String getTarget() {
        return this.getAttribute("target");
    }

    /**
     * 保存数据的字符集
     *
     * @param charsetName 字符集
     */
    public void setEncoding(String charsetName) {
        this.setAttribute("codepage", charsetName);
    }

    /**
     * 返回文件字符集
     *
     * @return 字符集
     */
    public String getEncoding() {
        return this.getAttribute("codepage");
    }

    /**
     * 字段个数
     *
     * @param column 字段个数
     */
    public void setColumn(int column) {
        this.setAttribute("column", String.valueOf(column));
    }

    /**
     * 返回字段个数
     *
     * @return 字段个数
     */
    public String getColumn() {
        return this.getAttribute("column");
    }

    /**
     * 行间分隔符
     *
     * @param str 行间分隔符
     */
    public void setLineSeparator(String str) {
        this.setAttribute("rowdel", StringUtils.escapeLineSeparator(str));
    }

    /**
     * 返回行间分隔符
     *
     * @return 行间分隔符
     */
    public String getLineSeparator() {
        return this.getAttribute("rowdel");
    }

    /**
     * 设置数据源信息
     *
     * @param str 数据源信息
     */
    public void setSource(String str) {
        this.setAttribute("source", SQL.removeAnnotation(str, null, null));
    }

    /**
     * 返回数据源信息
     *
     * @return 数据源信息
     */
    public String getSource() {
        return this.getAttribute("source");
    }

    /**
     * 保存字符串限定符
     *
     * @param str 字符串限定符
     */
    public void setCharDelimiter(String str) {
        this.setAttribute("chardel", String.valueOf(str));
    }

    /**
     * 返回字符串限定符
     *
     * @return 字符串限定符
     */
    public String getCharDelimiter() {
        return this.getAttribute("chardel");
    }

    /**
     * 保存字段分隔符
     *
     * @param str 字段分隔符
     */
    public void setDelimiter(String str) {
        this.setAttribute("coldel", str);
    }

    /**
     * 返回字段分隔符
     *
     * @return 字段分隔符
     */
    public String getDelimiter() {
        return this.getAttribute("coldel");
    }

    /**
     * 返回数据文件的字节数
     *
     * @return 数据文件的字节数
     */
    public String getBytes() {
        return this.getAttribute("bytes");
    }

    /**
     * 保存数据文件的字节数
     *
     * @param length 数据文件的字节数
     */
    public void setBytes(long length) {
        this.setAttribute("bytes", String.valueOf(length));
    }

    /**
     * 返回数据文件的行数
     *
     * @return 数据文件的行数
     */
    public String getRows() {
        return this.getAttribute("rows");
    }

    /**
     * 保存数据文件的行数
     *
     * @param line 数据文件的行数
     */
    public void setRows(long line) {
        this.setAttribute("rows", String.valueOf(line));
    }

    /**
     * 保存卸数用时时间
     *
     * @param time 卸数用时时间
     */
    public void setTime(String time) {
        this.setAttribute("usetime", time);
    }

    /**
     * 返回卸数用时时间
     *
     * @return 卸数用时时间
     */
    public String getTime() {
        return this.getAttribute("usetime");
    }

    public String toString() {
        CharTable ct = new CharTable();
        ct.addTitle("", CharTable.ALIGN_RIGHT);
        ct.addTitle("", CharTable.ALIGN_LEFT);
        ct.addTitle("", CharTable.ALIGN_LEFT);

        String[] titles = StringUtils.split(ResourcesUtils.getMessage("extract.standard.output.msg007"), ',');
        String[] values = {this.getStart(), //
                this.getEncoding(), //
                this.getColumn(), //
                this.getRows(), //
                this.getBytes(), //
                StringUtils.escapeLineSeparator(this.getLineSeparator()), //
                this.getDelimiter(), //
                this.getCharDelimiter(), //
                StringUtils.escapeLineSeparator(this.getSource()), //
                this.getTarget(), //
                this.getTime(), //
                this.getFinish() //
        };

        Ensure.isTrue(titles.length == values.length, titles.length + " != " + values.length);
        for (int i = 0; i < titles.length; i++) {
            ct.addCell(titles[i]);
            ct.addCell("=");
            ct.addCell(values[i]);
        }

        return new StringBuilder().append(ct.toString(CharTable.Style.simple)).append(FileUtils.lineSeparator).toString();
    }

}
