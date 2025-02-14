package cn.org.expect.script;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyLog;
import cn.org.expect.util.ClassUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 打印脚本引擎中已注册的命令
 */
@EasyLog("sout+:info")
@RunWith(ModestRunner.class)
public class ScriptPrintCommandsTest {
    private final static Log log = LogFactory.getLog(ScriptPrintCommandsTest.class);

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        String[] list = ClassUtils.getClassPath();
        for (String classpath : list) {
            log.info("classpath: {}", classpath);
        }

        List<EasyBeanEntry> entryList = this.context.getBeanEntryCollection(UniversalCommandCompiler.class).values();
        Collections.sort(entryList, new Comparator<EasyBeanEntry>() {
            public int compare(EasyBeanEntry o1, EasyBeanEntry o2) {
                return o1.getType().getName().compareTo(o2.getType().getName());
            }
        });

        for (EasyBeanEntry entry : entryList) {
            log.info(entry.getType().getName());
        }
        log.info("共找到 {} 个脚本命令类!", entryList.size());

        List<EasyBeanEntry> methods = this.context.getBeanEntryCollection(UniversalScriptVariableMethod.class).values();
        Collections.sort(methods, new Comparator<EasyBeanEntry>() {
            public int compare(EasyBeanEntry o1, EasyBeanEntry o2) {
                return o1.getType().getName().compareTo(o2.getType().getName());
            }
        });

        for (EasyBeanEntry entry : methods) {
            log.info(entry.getType().getName());
        }
        log.info("共找到 {} 个变量方法类!", methods.size());
    }
}
