package cn.org.expect.script.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptSessionFactory;
import cn.org.expect.util.Ensure;

/**
 * 接口实现类
 */
@EasyBean(value = "default", description = "脚本引擎会话工厂")
public class SessionFactory implements UniversalScriptSessionFactory {

    /** 用户会话编号与用户会话信息的映射关系 */
    private final LinkedHashMap<String, UniversalScriptSession> map;

    /**
     * 初始化
     */
    public SessionFactory() {
        this.map = new LinkedHashMap<String, UniversalScriptSession>();
    }

    public synchronized UniversalScriptSession build(UniversalScriptEngine engine) {
        ScriptSession session = new ScriptSession(engine.getId(), this);
        this.map.put(session.getId(), session);
        return session;
    }

    public synchronized UniversalScriptSession remove(String sessionid) {
        return this.map.remove(sessionid);
    }

    public synchronized boolean isAlive() {
        for (String id : this.map.keySet()) {
            UniversalScriptSession session = this.map.get(id);
            if (session != null && session.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public synchronized void terminate() throws Exception {
        for (String id : this.map.keySet()) { // 遍历脚本引擎中所有用户会话信息
            UniversalScriptSession session = this.map.get(id);
            if (session != null) {
                session.terminate();
            }
        }
    }

    public synchronized void terminate(String id) throws Exception {
        UniversalScriptSession session = this.map.get(id);
        if (session != null) {
            session.terminate();
        }
    }

    public synchronized List<String> getSessionidList() {
        return new ArrayList<String>(this.map.keySet());
    }

    /**
     * 将用户会话信息注册到会话池
     *
     * @param session 用户会话
     * @return 用户会话
     */
    public synchronized UniversalScriptSession add(UniversalScriptSession session) {
        Ensure.notNull(session);
        return this.map.put(session.getId(), session);
    }

    public synchronized UniversalScriptSession get(String sessionid) {
        return this.map.get(sessionid);
    }

    public synchronized void clear() {
        this.map.clear();
    }
}
