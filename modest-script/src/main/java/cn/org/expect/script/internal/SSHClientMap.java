package cn.org.expect.script.internal;

import java.util.LinkedHashMap;
import java.util.Set;

import cn.org.expect.os.OSSecureShellCommand;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptProgram;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;

/**
 * SSH 协议客户端集合
 *
 * @author jeremy8551@qq.com
 */
public class SSHClientMap implements UniversalScriptProgram {

    public final static String key = "SSHClientMap";

    public static SSHClientMap get(UniversalScriptContext context, boolean... array) {
        boolean global = array.length != 0 && array[0];
        SSHClientMap obj = context.getProgram(key, global);
        if (obj == null) {
            obj = new SSHClientMap();
            context.addProgram(key, obj, global);
        }
        return obj;
    }

    private LinkedHashMap<String, OSSecureShellCommand> map;

    /**
     * 初始化
     */
    public SSHClientMap() {
        this.map = new LinkedHashMap<String, OSSecureShellCommand>();
    }

    /**
     * 添加一个 SSH 客户端
     *
     * @param name   客户端名
     * @param client SSH 客户端
     */
    public void add(String name, OSSecureShellCommand client) {
        String key = name.toUpperCase();
        this.close(key);
        this.map.put(key, client);
    }

    /**
     * 返回 SSH 客户端
     *
     * @param name 名字
     * @return SSH 客户端
     */
    public OSSecureShellCommand get(String name) {
        return this.map.get(Ensure.notNull(name).toUpperCase());
    }

    /**
     * 返回最近一次添加的 SSH 客户端
     *
     * @return SSH 客户端
     */
    public OSSecureShellCommand last() {
        Set<String> set = this.map.keySet();
        if (set.isEmpty()) {
            return null;
        } else {
            return this.get(CollectionUtils.lastElement(set));
        }
    }

    /**
     * 返回客户端数量
     *
     * @return 客户端数量
     */
    public int size() {
        return this.map.size();
    }

    /**
     * 判断是否添加过客户端
     *
     * @return 返回true表示还未添加客户端
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * 判断是否存在未关闭的 SSH 客户端
     *
     * @return 返回true表示还有未关闭的 SSH 客户端
     */
    public boolean isAlive() {
        Set<String> names = this.map.keySet();
        for (String name : names) {
            OSSecureShellCommand session = this.map.get(name);
            if (session != null && session.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 关闭客户端并从集合中删除
     *
     * @param name 名字
     */
    public void close(String name) {
        String key = name.toUpperCase();
        OSSecureShellCommand ssh = this.map.get(key);
        if (ssh != null) {
            IO.close(ssh);
            this.map.remove(key);
        }
    }

    public void close() {
        Set<String> names = this.map.keySet();
        for (String name : names) {
            this.close(name);
        }
        this.map.clear();
    }

    public ScriptProgramClone deepClone() {
        SSHClientMap obj = new SSHClientMap();
        obj.map.putAll(this.map);
        return new ScriptProgramClone(key, obj);
    }

}
