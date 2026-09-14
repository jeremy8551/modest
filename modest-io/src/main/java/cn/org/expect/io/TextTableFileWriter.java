package cn.org.expect.io;

/**
 * 将处理结果写入目标输出
 */
public interface TextTableFileWriter extends TableWriter {

    TextTableFile getTable();
}
