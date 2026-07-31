package cn.org.expect.database.mysql.expconv;

import java.io.InputStream;

import cn.org.expect.collection.ByteBuffer;
import cn.org.expect.database.export.converter.BlobConverter;

/**
 * 在数据库字段值与 Java 对象之间执行类型转换
 */
public class ByteArrayConverter extends BlobConverter {

    /** {@inheritDoc} */
    public void execute() throws Exception {
        InputStream in = this.resultSet.getBinaryStream(this.column);
        if (in == null) {
            this.array[this.column] = "";
        } else {
            try {
                ByteBuffer bytes = new ByteBuffer(9108, 128);
                String hexStr = bytes.append(in, null).toHexString();
                this.array[this.column] = hexStr;
            } finally {
                in.close();
            }
        }
    }
}
