package cn.org.expect.codegen;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.org.expect.codegen.config.GeneratorConfig;

/**
 * SSM 代码生成命令行入口
 */
public final class SsmCodeGeneratorMain {

    private SsmCodeGeneratorMain() {
    }

    /**
     * 执行代码生成
     *
     * @param args 命令行参数
     * @throws Exception 配置读取或代码生成失败时抛出
     */
    public static void main(String[] args) throws Exception {
        Map<String, String> arguments = parseArguments(args);
        String input = arguments.get("input");
        String output = arguments.get("output");
        if (input == null || output == null) {
            throw new IllegalArgumentException("必须指定 --input <数据库设计.xlsx> 与 --output <输出目录>");
        }

        GeneratorConfig config = arguments.containsKey("config") ? GeneratorConfig.load(new File(arguments.get("config"))) : GeneratorConfig.defaults();
        new SsmCodeGenerator(config).generate(new File(input), new File(output));
    }

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数
     * @return 参数映射
     */
    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<String, String>();
        for (int index = 0; index < args.length; index += 2) {
            if (!args[index].startsWith("--") || index + 1 >= args.length) {
                throw new IllegalArgumentException("参数必须使用 --名称 值 的格式");
            }
            arguments.put(args[index].substring(2), args[index + 1]);
        }
        return arguments;
    }
}
