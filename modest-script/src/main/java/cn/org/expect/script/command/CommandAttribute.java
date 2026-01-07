package cn.org.expect.script.command;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.collection.CaseSensitivSet;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.util.Attribute;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

/**
 * 脚本命令属性集合
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-05-26
 */
public class CommandAttribute implements Attribute<String> {

    /** 属性信息 */
    private Map<String, String> attributes;

    /** 有属性值的范围 */
    private Set<String> value;

    /** 无属性值的范围 */
    private Set<String> novalue;

    /** 用户会话信息 */
    private UniversalScriptSession session;

    /** 脚本引擎上下文信息 */
    private UniversalScriptContext context;

    /**
     * 初始化
     */
    protected CommandAttribute() {
        this.attributes = new CaseSensitivMap<String>();
        this.value = new CaseSensitivSet();
        this.novalue = new CaseSensitivSet();
    }

    /**
     * 初始化
     *
     * @param session 用户会话信息
     * @param context 脚本引擎上下文信息
     * @param names   支持的属性名数组（添加数组之外的属性会抛出异常）<br>
     *                属性名右侧没有半角冒号表示不存在属性值（会抛出异常）<br>
     *                属性名右侧使用半角冒号表示存在属性值 <br>
     */
    public CommandAttribute(UniversalScriptSession session, UniversalScriptContext context, String... names) {
        this();
        if (names.length == 0) {
            throw new IllegalArgumentException();
        }
        this.session = session;
        this.context = context;

        for (int i = 0; i < names.length; i++) {
            String name = Ensure.notNull(names[i]);
            if (name.endsWith(":")) {
                this.value.add(StringUtils.removeSuffix(name));
            } else {
                this.novalue.add(name);
            }
        }
    }

    /**
     * 返回属性信息
     *
     * @return 属性集合
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public boolean contains(String key) {
        return this.attributes.containsKey(key);
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    /**
     * 返回整数型属性值
     *
     * @param key 属性名（大小写不敏感）
     * @return 整数
     */
    public int getIntAttribute(String key) {
        return Integer.parseInt(this.attributes.get(key));
    }

    /**
     * 返回属性值
     *
     * @param key   属性名
     * @param array 默认值数组
     * @return 属性值
     */
    public String getAttribute(String key, String... array) {
        String value = this.attributes.get(key);
        if (StringUtils.isBlank(value)) {
            for (String str : array) {
                if (StringUtils.isNotBlank(str)) {
                    return str;
                }
            }
        }
        return value;
    }

    public void setAttribute(String name, String value) {
        String newValue = this.session.getAnalysis().replaceShellVariable(this.session, this.context, value, true, true);

        if (newValue != null && newValue.length() > 0 && this.novalue.contains(name)) {
            throw new UnsupportedOperationException(name);
        } else if (!this.value.contains(name) && !this.novalue.contains(name)) {
            throw new UnsupportedOperationException(name);
        } else {
            this.attributes.put(name, newValue);
        }
    }

    public CommandAttribute clone(UniversalScriptSession session, UniversalScriptContext context) {
        CommandAttribute obj = new CommandAttribute();
        obj.value.addAll(this.value);
        obj.novalue.addAll(this.novalue);

        UniversalScriptAnalysis analysis = session.getAnalysis();
        Iterator<String> it = this.attributes.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = this.attributes.get(key);
            String newValue = analysis.replaceShellVariable(session, context, analysis.unQuotation(value), true, !analysis.containsQuotation(value));
            obj.attributes.put(key, newValue);
        }
        return obj;
    }

    public CommandAttribute clone() {
        CommandAttribute obj = new CommandAttribute();
        obj.attributes.putAll(this.attributes);
        obj.value.addAll(this.value);
        obj.novalue.addAll(this.novalue);
        return obj;
    }
}
