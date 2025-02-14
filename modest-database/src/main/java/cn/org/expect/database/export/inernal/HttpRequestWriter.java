package cn.org.expect.database.export.inernal;

import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.org.expect.database.export.ExtractMessage;
import cn.org.expect.database.export.ExtractWriter;
import cn.org.expect.io.TableLine;
import cn.org.expect.io.TableWriter;
import cn.org.expect.io.TextTable;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ServletUtils;

@EasyBean(value = "http", description = "卸载数据到用户浏览器\nhttp://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse）")
public class HttpRequestWriter implements ExtractWriter {

    /** HTTP 响应信息输出接口 */
    private TableWriter writer;

    /** 下载后的文件名 */
    private String filename;

    /** 文件行数 */
    private long lineNumber;

    private ExtractMessage message;

    /**
     * 初始化
     *
     * @param request  HttpServletRequest 请求
     * @param response HttpServletResponse 响应
     * @param filename 下载后的文件名
     * @param table    文本表格
     * @param message  消息信息
     * @throws IOException 卸载数据发生错误
     */
    public HttpRequestWriter(Object request, Object response, String filename, TextTable table, ExtractMessage message) throws IOException {
        Ensure.notNull(request);
        Ensure.notNull(response);

        this.filename = filename;
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.reset();
        res.setContentType("APPLICATION/OCTET-STREAM");
        res.setHeader("Content-Disposition", "attachment; filename=\"" + ServletUtils.encodeFilename(req, filename) + "\"");

        ServletOutputStream out = res.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, table.getCharsetName());
        this.writer = table.getWriter(writer, 0);
        this.message = message;
        this.message.setTarget("http://download/" + this.filename);
    }

    public void write(TableLine line) throws IOException {
        this.lineNumber++;
        this.writer.addLine(line);
    }

    public boolean rewrite() throws IOException {
        return false;
    }

    public void flush() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
        }
    }

    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;

            this.message.setRows(this.lineNumber);
            this.message.setBytes(0);
        }
    }
}
