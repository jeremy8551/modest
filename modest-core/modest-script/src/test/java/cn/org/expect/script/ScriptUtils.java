package cn.org.expect.script;

import java.util.Properties;
import javax.script.SimpleBindings;

import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

public class ScriptUtils {

    public static SimpleBindings to(Properties config) {
        SimpleBindings bindings = new SimpleBindings();
        for (Object key : config.keySet()) {
            String name = StringUtils.trimBlank(key);
            String value = StringUtils.trimBlank(config.getProperty(name));
            bindings.put(name, value);
        }

        bindings.put("curr_dir_path", FileUtils.joinPath(ClassUtils.getClasspath(ScriptUtils.class), "script"));
        bindings.put("temp", FileUtils.getTempDir("test", "script").getAbsolutePath());
        return bindings;
    }
}
