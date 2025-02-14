package cn.org.expect.script.method;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.concurrent.BaseJob;
import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.annotation.EasyVariableExtension;
import cn.org.expect.util.StringUtils;

@EasyVariableExtension
public class ScriptEngineExtension {

    /**
     * 返回当前时间戳
     *
     * @param engine 脚本引擎
     * @return 时间戳
     */
    public static long currentTimeMillis(UniversalScriptEngine engine) {
        return System.currentTimeMillis();
    }

    /**
     * 返回组件
     *
     * @param engine        脚本引擎
     * @param beanClassName 组件类信息
     * @param name          组件名
     * @return 组件
     */
    public static Object getBean(UniversalScriptEngine engine, String beanClassName, String name) {
        EasyContext ioc = engine.getContext().getContainer();
        Class<?> type = ioc.forName(beanClassName);
        if (StringUtils.isBlank(name)) {
            return ioc.getBean(type);
        } else {
            return ioc.getBean(type, name);
        }
    }

    /**
     * 返回并发任务容器
     *
     * @param engine 脚本引擎
     * @param number 容器并发数（同时运行任务的个数）
     * @return 并发任务容器
     */
    public static EasyJobService getJobService(UniversalScriptEngine engine, int number) {
        EasyContext ioc = engine.getContext().getContainer();
        return ioc.getBean(ThreadSource.class).getJobService(number);
    }

    /**
     * 并发运行多个脚本语句
     *
     * @param engine   脚本
     * @param number   个数
     * @param commands 脚本语句集合
     * @return 脚本语句的返回结果
     * @throws Exception 发生错误
     */
    public static List<Object> evaluate(final UniversalScriptEngine engine, int number, List<String> commands) throws Exception {
        final List<Object> list = new ArrayList<Object>();
        List<EasyJob> jobs = new ArrayList<EasyJob>();
        for (final String command : commands) {
            jobs.add(new BaseJob() {
                public int execute() {
                    Object value = engine.getFactory().getScriptEngine().evaluate(command);
                    if (value != null) {
                        list.add(value);
                    }
                    return 0;
                }
            });
        }

        // 线程池运行并发任务
        EasyContext ioc = engine.getContext().getContainer();
        EasyJobService service = ioc.getBean(ThreadSource.class).getJobService(number);
        service.execute(jobs);
        return list;
    }
}
