package cn.org.expect.database.export.converter;

import java.io.InputStream;
import java.sql.Blob;

import cn.org.expect.collection.ByteBuffer;

public class BlobConverter extends AbstractConverter {

    public void init() throws Exception {
    }

    public void execute() throws Exception {
        Blob value = this.resultSet.getBlob(this.column);
        if (value == null) {
            this.array[this.column] = "";
        } else {
            InputStream in = value.getBinaryStream();
            if (in != null) {
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

}
