package cn.org.expect.script.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

import cn.org.expect.collection.ByteBuffer;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptFormatter;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.Dates;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;

/**
 * 类型转换器
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean("default")
public class ScriptFormatter extends UniversalScriptFormatter {
    private final static long serialVersionUID = 1L;
    private final static Log log = LogFactory.getLog(ScriptFormatter.class);

    public ScriptFormatter() {
    }

    public Object formatJdbcParameter(UniversalScriptSession session, UniversalScriptContext context, Object object) throws Exception {
        if (object == null) {
            return null;
        }

        if (object instanceof String //
            || object instanceof Integer //
            || object instanceof BigDecimal //
            || object instanceof Long //
            || object instanceof Double //
            || object instanceof Float //
        ) {
            return object;
        }

        if (object instanceof Date || object instanceof Clob) {
            return StringUtils.toString(object);
        }

        if (object.getClass().isArray()) {
            Class<?> clazz = object.getClass().getComponentType();
            if (byte.class.equals(clazz)) {
                byte[] array = (byte[]) object;
                return StringUtils.toString(array, context.getCharsetName());
            } else {
                return object;
            }
        }

        if (object instanceof InputStream) {
            InputStream in = (InputStream) object;
            return new ByteBuffer().append(in).toString(context.getCharsetName());
        }

        if (object instanceof Reader) {
            java.io.Reader in = (java.io.Reader) object;
            return IO.read(in, new StringBuilder()).toString();
        }

        if (object instanceof Blob) {
            Blob blob = (java.sql.Blob) object;
            File tempDir = session.getTempDir();
            File parent = FileUtils.createDirectory(tempDir, ScriptFormatter.class.getSimpleName(), Dates.format17());
            File file = FileUtils.createNewFile(parent, "ScriptConvert.blob");
            IO.write(blob.getBinaryStream(), new FileOutputStream(file), null);
            return file.getAbsolutePath();
        }

        if (object instanceof URL) {
            return ((URL) object).getPath();
        }

        if (object instanceof Ref) {
            return ((Ref) object).getObject();
        }

        return object;
    }

    public String toString(Object obj) {
        if (obj == null) {
            return "";
        }

        if (obj instanceof String) {
            return (String) obj;
        } else {
            return StringUtils.toString(obj);
        }
    }

    public StringBuffer format(Object obj, StringBuffer buf, FieldPosition pos) {
        buf.append(this.toString(obj));
        return buf;
    }

    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException();
    }
}
