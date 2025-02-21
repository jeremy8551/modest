package cn.org.expect.ssm.config;

public class MybatisPlusStdLogger extends com.p6spy.engine.spy.appender.StdoutLogger {
    public void logText(String text) {
        System.out.println(text);
    }
}
