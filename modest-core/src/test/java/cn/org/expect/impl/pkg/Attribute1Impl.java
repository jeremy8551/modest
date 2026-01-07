package cn.org.expect.impl.pkg;

import cn.org.expect.util.Attribute;
import com.google.auto.service.AutoService;

@AutoService(Attribute.class)
public class Attribute1Impl implements Attribute<String> {

    public boolean contains(String key) {
        return false;
    }

    public void setAttribute(String key, String value) {
    }

    public String getAttribute(String key) {
        return null;
    }
}
