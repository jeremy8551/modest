package cn.org.expect.log;

/**
 * 日志接口
 *
 * @author jeremy8551@gmail.com
 * @createtime 2012-06-28
 */
public interface Log extends Logger {

    /**
     * 返回日志所属类的名字
     *
     * @return 字符串
     */
    String getName();
}
