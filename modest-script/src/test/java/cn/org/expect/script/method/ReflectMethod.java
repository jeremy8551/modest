package cn.org.expect.script.method;

public class ReflectMethod {

    public ReflectMethod() {
    }

    public String test() {
        return "test";
    }

    public String test(int i) {
        return "test" + i;
    }

    public String test(int i, String... a) {
        return "test" + i + a.length;
    }

    public String test(int i, int j) {
        return "test" + i + j;
    }
}
