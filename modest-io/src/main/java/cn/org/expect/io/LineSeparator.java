package cn.org.expect.io;

/**
 * 回车换行符
 *
 * @author jeremy8551@qq.com
 * @createtime 2020-01-31
 */
public interface LineSeparator {

    /**
     * 返回文本输入流当前行的行末分隔符或文件默认的行间分隔符
     *
     * @return 行间分隔符
     */
    String getLineSeparator();

}
