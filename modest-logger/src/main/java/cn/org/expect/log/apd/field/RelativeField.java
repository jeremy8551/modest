package cn.org.expect.log.apd.field;

import cn.org.expect.log.apd.LogEvent;

/**
 * %r：输出自应用程序启动到输出该log信息耗费的毫秒数。
 * r是 relative 的缩写
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/20
 */
public class RelativeField extends AbstractField {

    public String format(LogEvent event) {
        return this.format(String.valueOf(System.currentTimeMillis() - event.getContext().getStartMillis()));
    }
}
