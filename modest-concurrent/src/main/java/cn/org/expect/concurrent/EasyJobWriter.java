package cn.org.expect.concurrent;

/**
 * 错误信息记录器
 *
 * @author jeremy8551@qq.com
 * @createtime 2012-04-12
 */
public interface EasyJobWriter {

    /**
     * 保存异常错误信息
     *
     * @param name    任务名
     * @param message 错误提示信息
     * @param e       异常信息
     */
    void addError(String name, String message, Exception e);

    /**
     * 判断是否有错误信息
     *
     * @return 返回true表示存在错误 false表示没有错误
     */
    boolean hasError();

}
