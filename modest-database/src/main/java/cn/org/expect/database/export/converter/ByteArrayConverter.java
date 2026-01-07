package cn.org.expect.database.export.converter;

import java.io.InputStream;

import cn.org.expect.collection.ByteBuffer;

public class ByteArrayConverter extends BlobConverter {

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
