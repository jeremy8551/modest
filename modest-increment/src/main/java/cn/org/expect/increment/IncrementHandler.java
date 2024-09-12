package cn.org.expect.increment;

import java.io.Closeable;
import java.io.IOException;

import cn.org.expect.io.TextTableLine;

/**
 * 增量数据处理接口
 *
 * @author jeremy8551@qq.com
 */
public interface IncrementHandler extends Closeable {

    /**
     * 处理新增记录
     *
     * @param line 新记录输入流
     * @throws IOException 处理新增记录错误
     */
    void handleCreateRecord(TextTableLine line) throws IOException;

    /**
     * 处理变更记录
     *
     * @param newLine  新记录输入流
     * @param oldLine  旧记录输入流
     * @param position 新记录中第一个与老记录不同的字段位置，从 1 开始
     * @throws IOException 处理变更记录错误
     */
    void handleUpdateRecord(TextTableLine newLine, TextTableLine oldLine, int position) throws IOException;

    /**
     * 处理删除记录
     *
     * @param line 旧记录输入流
     * @throws IOException 处理删除记录错误
     */
    void handleDeleteRecord(TextTableLine line) throws IOException;

}
