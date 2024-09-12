package cn.org.expect.log.apd.field;

import cn.org.expect.log.apd.LogField;
import cn.org.expect.log.apd.LogFieldAlign;

/**
 * 抽象类
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/11/23
 */
public abstract class AbstractField implements LogField {

    protected LogFieldAlign align;

    public void setAlign(LogFieldAlign align) {
        this.align = align;
    }

    public LogFieldAlign getAlign() {
        return align;
    }

    public String format(CharSequence msg) {
        return this.align == null ? msg.toString() : this.align.format(msg);
    }
}
