package cn.org.expect.build;

import java.io.File;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 启动并配置当前应用模块
 */
public class ModestBuildApplication {

    /**
     * 启动示例或应用
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();

        if (args.length > 0) {
            File file = new File(FileUtils.replaceFolderSeparator(args[0]));
            if (FileUtils.isFile(file)) {
                engine.evaluate(". " + file.getAbsolutePath());
                return;
            }
        }

        throw new IllegalArgumentException("argument is invalid! " + StringUtils.toString(args));
    }
}
