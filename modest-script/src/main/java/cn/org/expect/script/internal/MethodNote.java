package cn.org.expect.script.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * 变量方法的注释
 */
public class MethodNote {

    /** 方法注释 */
    private String note;

    /** 变量注释 */
    private Property variable;

    /** 参数注释 */
    private final List<Property> list;

    /** 返回值注释 */
    private String returnValue;

    /** true表示在生成文档时忽略方法，false表示使用 */
    private boolean ignore;

    public MethodNote() {
        this.list = new ArrayList<Property>();
        this.note = "";
        this.returnValue = "";
        this.ignore = false;
    }

    public String getText() {
        return note;
    }

    public void setText(String note) {
        this.note = note;
    }

    public Property getVariable() {
        return variable;
    }

    public void setVariable(Property property) {
        this.variable = property;
    }

    public List<Property> getParameterList() {
        return list;
    }

    public String getReturn() {
        return returnValue;
    }

    public void setReturn(String returnValue) {
        this.returnValue = returnValue;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public static class Property {
        private final String name;
        private final String value;

        public Property(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getNote() {
            return value;
        }
    }
}
