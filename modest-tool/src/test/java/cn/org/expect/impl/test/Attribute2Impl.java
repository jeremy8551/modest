package cn.org.expect.impl.test;

import cn.org.expect.util.Attribute;

public class Attribute2Impl implements Attribute<String> {

    public boolean contains(String key) {
        return false;
    }

    public void setAttribute(String key, String value) {

    }

    public String getAttribute(String key) {
        return null;
    }
}
