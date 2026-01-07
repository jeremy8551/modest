package cn.org.expect.ssm.mybatis;

import java.util.Collections;
import java.util.Properties;

import cn.org.expect.util.FileUtils;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * 代码生成器
 */
public class CodeGenerator {

    public static void main(String[] args) {
        Properties properties = FileUtils.loadProperties(null, "db2.properties", null);
        String url = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        FastAutoGenerator generator = FastAutoGenerator.create(url, username, password);

        // 全局配置
        generator.globalConfig(builder -> {
            builder.author("jeremy8551@gmail.com"); // 设置作者
            builder.outputDir("/Users/user/Documents/Java/modest/modest-ssm-demo/src/main/java"); // 输出目录
            builder.disableOpenDir(); // 生成代码后不自动打开文件浏览器
            builder.enableSpringdoc(); // 开启 swagger3 注解
        });

        // 包配置
        generator.packageConfig(builder -> {
            builder.parent("icu.ssm"); // 设置父包名
            builder.entity("entity"); // 设置实体类包名
            builder.mapper("dao"); // 设置 Mapper 接口包名
            builder.service("service"); // 设置 Service 接口包名
            builder.pathInfo(Collections.singletonMap(OutputFile.xml, "/Users/user/Documents/Java/modest/modest-ssm-demo/src/main/resources/mapper")); //
        });

        // 代码生成策略
        generator.strategyConfig(builder -> {
//                    builder.addInclude("USER_INFO", "ROLE_INFO"); // 设置需要生成的表名，如果注释，表示对所有表生成代码
            builder.entityBuilder().enableLombok().enableFileOverride().enableTableFieldAnnotation().enableFileOverride(); // 启用 Lombok
            builder.mapperBuilder().enableFileOverride(); //
            builder.serviceBuilder().enableFileOverride(); //
            builder.controllerBuilder().enableRestStyle().enableFileOverride(); // 启用 REST 风格
        });

        // 使用 Freemarker 模板引擎
        generator.templateEngine(new FreemarkerTemplateEngine());

        // 生成代码
        generator.execute();
    }
}
