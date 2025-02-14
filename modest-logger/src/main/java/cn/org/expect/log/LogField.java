package cn.org.expect.log;

import cn.org.expect.log.internal.LogFieldAlign;

/**
 * 日志格式中的字段
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public interface LogField {

    /**
     * 对齐方式
     *
     * @param align 对齐方式
     */
    void setAlign(LogFieldAlign align);

    /**
     * 格式化为字符串
     *
     * @param event 日志事件
     * @return 字符串
     */
    String format(LogEvent event);
}
