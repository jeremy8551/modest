package cn.org.expect.script.internal;

import java.io.File;
import java.io.IOException;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.OSUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Settings;

/**
 * 生成脚本引擎帮助文档
 */
public class ScriptReadme {

    public static void main(String[] args) throws IOException {
        // 根目录
        File userDir = Settings.getUserDir();
        File project = FileUtils.findUpward(userDir, Settings.getProjectName());
        if (!FileUtils.isDirectory(project)) {
            throw new UniversalScriptException("script.stderr.message136", project, userDir, Settings.getProjectName());
        }

        // 帮助文档
        File markdown = new File(project, "README.md");
        System.out.println(ResourcesUtils.getMessage("script.stdout.message058", markdown.getAbsolutePath()));

        // 在根目录生成文档
        DefaultEasyContext context = new DefaultEasyContext();
        UniversalScriptEngine engine = context.getBean(UniversalScriptEngineFactory.class).getScriptEngine();
        engine.getContext().addLocalVariable("project.build.sourceDirectory", args[0]);
        engine.evaluate("help > " + markdown.getAbsolutePath());

        // 在用户桌面生成文档
        File desktop = OSUtils.getDesktop();
        if (FileUtils.isDirectory(desktop)) {
            File document = new File(desktop, "脚本引擎文档.md");
            FileUtils.copy(markdown, document);
            System.out.println(ResourcesUtils.getMessage("script.stdout.message058", document.getAbsolutePath()));
        }
    }
}
