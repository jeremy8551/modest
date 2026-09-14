package cn.org.expect.script.command;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.test.ModestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试 if 命令编译器
 */
@RunWith(ModestRunner.class)
public class IfCommandCompilerTest {

    @EasyBean
    private EasyContext context;

    /**
     * 验证三层 if 嵌套时中间层的 elseif 能被正确解析
     */
    @Test
    public void testNestedIfWithElseIf() {
        UniversalScriptEngineFactory factory = this.context.getBean(UniversalScriptEngineFactory.class);
        UniversalScriptEngine engine = factory.getScriptEngine();
        String script = "if 1 == 1 then\n" //
            + "  if 1 == 1 then\n" //
            + "    if 1 == 1 then\n" //
            + "      echo THIRD_LEVEL\n" //
            + "    fi\n" //
            + "  elseif 1 == 2 then\n" //
            + "    echo ELSEIF_BRANCH\n" //
            + "  fi\n" //
            + "fi\n" //
            + "exit 0";

        engine.evaluate(script);
    }
}
