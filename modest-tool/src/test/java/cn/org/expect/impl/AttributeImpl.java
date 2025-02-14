package cn.org.expect.impl;

import cn.org.expect.util.Attribute;

public class AttributeImpl implements Attribute<String> {

    public boolean contains(String key) {
        return false;
    }

    public void setAttribute(String key, String value) {
    }

    public String getAttribute(String key) {
        return null;
    }
}
