package cn.org.expect.database.load.converter;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.sql.Types;

import cn.org.expect.util.StringUtils;

/**
 * 将 UTF-8 十六进制文本还原为字符串，由数据库按目标字段类型解析。
 */
public class TextConverter extends AbstractConverter {

    public void init() throws Exception {
    }

    public void execute(String value) throws Exception {
        if (value.length() == 0) {
            this.statement.setNull(this.position, Types.OTHER);
            return;
        }
        if (value.length() % 2 != 0 || !value.matches("[0-9a-fA-F]+")) {
            throw new IllegalArgumentException("文本字段必须使用完整的十六进制字节表示");
        }

        byte[] bytes = StringUtils.parseHexString(value);
        String text = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString();
        this.statement.setObject(this.position, text, Types.OTHER);
    }
}
