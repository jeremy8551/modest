package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;

/**
 * %t：输出产生该日志事件的线程名。
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class ThreadName extends AbstractField {

    public String format(LogEvent event) {
        return this.format(Thread.currentThread().getName());
    }
}
