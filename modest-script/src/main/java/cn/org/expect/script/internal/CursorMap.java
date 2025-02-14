package cn.org.expect.script.internal;

import cn.org.expect.database.JdbcQueryStatement;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptProgram;

/**
 * 数据库游标
 *
 * @author jeremy8551@gmail.com
 */
public class CursorMap extends MapTemplate<JdbcQueryStatement> implements UniversalScriptProgram {

    public final static String key = "CursorMap";

    public static CursorMap get(UniversalScriptContext context, boolean... array) {
        boolean global = array.length != 0 && array[0];
        CursorMap obj = context.getProgram(key, global);
        if (obj == null) {
            obj = new CursorMap();
            context.addProgram(key, obj, global);
        }
        return obj;
    }

    public ScriptProgramClone deepClone() {
        CursorMap obj = new CursorMap();
        obj.map.putAll(this.map);
        return new ScriptProgramClone(key, this.map);
    }

    public void close() {
        super.close();
    }
}
