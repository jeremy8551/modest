package cn.org.expect.script.internal;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptProgram;

/**
 * 数据库批量处理
 *
 * @author jeremy8551@gmail.com
 */
public class StatementMap extends MapTemplate<ScriptStatement> implements UniversalScriptProgram {

    public final static String key = "StatementMap";

    public static StatementMap get(UniversalScriptContext context) {
        StatementMap obj = context.getProgram(key, false);
        if (obj == null) {
            obj = new StatementMap();
            context.addProgram(key, obj, false);
        }
        return obj;
    }

    public ScriptProgramClone deepClone() {
        StatementMap obj = new StatementMap();
        obj.map.putAll(this.map);
        return new ScriptProgramClone(key, this.map);
    }

    public void close() {
        super.close();
    }
}
