package cn.org.expect.database.export.converter;

import cn.org.expect.util.StringUtils;

/**
 * 将字段的数据库文本表示按 UTF-8 编码后导出为十六进制字符串。
 */
public class TextConverter extends AbstractConverter {

    @Override
    public void init() throws Exception {
    }

    @Override
    public void execute() throws Exception {
        String text = this.resultSet.getString(this.column);
        this.array[this.column] = text == null ? "" : StringUtils.toHexString(text, "UTF-8");
    }
}
