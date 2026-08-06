package cn.org.expect.maven.plugin.codegen;

import java.io.File;
import java.io.IOException;

import cn.org.expect.codegen.SsmCodeGenerator;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 根据数据库设计工作簿生成工程代码
 */
@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CodegenMojo extends AbstractMojo {

    /** 数据库设计工作簿 */
    @Parameter(defaultValue = "${project.basedir}/.doc/数据库设计.xlsx", required = true)
    private String input;

    /** 工程代码根目录 */
    @Parameter(defaultValue = "${project.basedir}/src/java/main", required = true)
    private String output;

    /**
     * 执行代码生成
     *
     * @throws MojoExecutionException 读取数据库设计或生成代码失败
     */
    @Override
    public void execute() throws MojoExecutionException {
        if (StringUtils.isBlank(this.input) || !FileUtils.isFile(this.input)) {
            getLog().info("skip not existing Database Design Document");
            return;
        }

        try {
            getLog().info("Generate code from " + this.input + " to " + this.output);
            new SsmCodeGenerator().generate(new File(this.input), new File(this.output));
        } catch (IOException e) {
            throw new MojoExecutionException("代码生成失败", e);
        }
    }
}
