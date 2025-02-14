package cn.org.expect.script.method;

import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.annotation.EasyVariableExtension;

@EasyVariableExtension
public class StringFunction {

    public static int test123(CharSequence str, int from) {
        return from;
    }

    public static Object getNull(UniversalScriptEngine engine) {
        return null;
    }

    public static Object getText(UniversalScriptEngine engine, String text) {
        return text;
    }
}
