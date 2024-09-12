package cn.org.expect.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public interface TableWriter extends Flushable, Closeable {

    /**
     * 添加一行记录
     *
     * @param line 记录内容
     * @throws IOException 添加发生错误
     */
    void addLine(String line) throws IOException;

    /**
     * 添加一行记录，从输入流中读取列内容并添加到一个新记录中
     *
     * @param line 列输入流
     * @throws IOException 添加发生错误
     */
    void addLine(TableLine line) throws IOException;

    /**
     * 返回表格
     *
     * @return 表格
     */
    Table getTable();

}
