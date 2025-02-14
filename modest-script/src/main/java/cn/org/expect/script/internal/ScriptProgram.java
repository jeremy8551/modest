package cn.org.expect.script.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.org.expect.script.UniversalScriptProgram;
import cn.org.expect.util.IO;

public class ScriptProgram extends HashMap<String, Object> implements Map<String, Object> {
    private final static long serialVersionUID = 1L;

    public ScriptProgram() {
        super();
    }

    /**
     * 从输入参数中复制
     *
     * @param program 数据或程序集合
     */
    public void addAll(ScriptProgram program) {
        Set<Entry<String, Object>> set = program.entrySet();
        for (Entry<String, Object> e : set) {
            Object value = e.getValue();

            // 如果实现了接口则需要深度复制
            if (value instanceof UniversalScriptProgram) {
                UniversalScriptProgram scriptProgram = (UniversalScriptProgram) value;
                ScriptProgramClone map = scriptProgram.deepClone();
                this.put(map.getKey(), map.getValue());
            }
        }
    }

    /**
     * 释放所有资源
     */
    public void close() {
        Set<Entry<String, Object>> entrySet = this.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            Object obj = entry.getValue();
            if (obj instanceof UniversalScriptProgram) {
                IO.close(obj); // 自动关闭用户扩展的程序
            }
        }
        this.clear();
    }
}
