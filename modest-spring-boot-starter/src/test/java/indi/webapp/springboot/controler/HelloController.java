package indi.webapp.springboot.controler;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.io.TextTableFile;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.util.FileUtils;
import indi.jeremy.bean.Bean1;
import indi.jeremy.bean.Bean2;
import indi.jeremy.bean.DatFile;
import indi.jeremy.bean.TestFile;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/10/3
 */
@Controller
public class HelloController {
    private final static Log log = LogFactory.getLog(HelloController.class);

    @Value("${org.etl.test}")
    private String testvalue;

    @Autowired
    private ScriptEngine engine;

    @Autowired
    private ApplicationContext spring;

    @Autowired
    private EasyContext context;

    @RequestMapping("/help")
    @ResponseBody
    public String help() throws ScriptException {
        log.info(spring.getId());
        log.info(context.getName());

        // 测试yaml文件中的属性与脚本引擎中显示的属性是否相等
        String id0 = engine.toString();
        engine.eval("set test2=\"${org.etl.test}\"; echo ${test2}; ");
        Assert.assertEquals(this.testvalue, engine.getContext().getAttribute("test2"));

        // 测试语句
        String id1 = engine.toString();
        engine.eval("set test='test1'");
        Assert.assertEquals("test1", engine.getContext().getAttribute("test"));
        Assert.assertEquals(id0, id1);

        // 测试从脚本引擎中取Spring容器中的组件
        Assert.assertNotNull(this.spring.getBean(UniversalScriptEngineFactory.class));
        Assert.assertNotNull(this.spring.getBean(UniversalScriptEngine.class));

        TestFile bean = context.getBean(TestFile.class);
        Assert.assertNotNull(bean);
        bean.hello();

        Bean1 bean1 = context.getBean(Bean1.class, "bean1");
        Assert.assertNotNull(bean1);
        bean1.hello();

        Bean2 bean2 = context.getBean(Bean2.class, new Bean1());
        Assert.assertNotNull(bean2);
        bean2.hello();

        TextTableFile fexfile = context.getBean(TextTableFile.class, "fex");
        Assert.assertNotNull(fexfile);
        Assert.assertEquals(DatFile.class, fexfile.getClass());

        // 测试不应该扫描的类包
        EasyBeanEntry logBeanEntry = context.getBeanEntry(TextTableFile.class, "log");
        if (logBeanEntry != null) {
            log.info(logBeanEntry.getType().getName());
        }
        Assert.assertNull(logBeanEntry);

        // 打印脚本引擎的使用说明
        String id2 = engine.toString();
        engine.eval("set test3=`help`");

        Assert.assertEquals(id0, id2); // 测试脚本引擎对象的生命周期是 request，也就是在一次 SpringBoot 请求中是同一个脚本引擎
        return FileUtils.replaceLineSeparator((String) engine.getContext().getAttribute("test3"), "<br>");
    }
}
