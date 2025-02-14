package cn.org.expect.log.field;

import cn.org.expect.log.LogEvent;

/**
 * 字符常量信息
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/20
 */
public class ConstantField extends AbstractField {

    private String str;

    public ConstantField(String str) {
        this.str = str;
    }

    public String format(LogEvent event) {
        return this.str;
    }

    public String toString() {
        return "String[" + this.str + "]";
    }
}
