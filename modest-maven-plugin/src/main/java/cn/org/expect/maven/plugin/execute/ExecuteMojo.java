package cn.org.expect.maven.plugin.execute;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.util.JavaDialectFactory;
import cn.org.expect.log.Log;
import cn.org.expect.maven.plugin.MavenPluginLog;
import cn.org.expect.maven.plugin.MavenPluginLogAware;
import cn.org.expect.maven.plugin.MavenPluginLogImpl;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.Numbers;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.SPI;
import cn.org.expect.util.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * 执行 Java 程序
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025-11-29
 */
@Mojo(name = "execute", requiresProject = true, defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ExecuteMojo extends AbstractMojo {

    /**
     * 当前插件信息
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    protected PluginDescriptor plugin;

    /** 项目信息 */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * 执行命令, 格式为：<br>
     * &lt;command&gt;&lt;/command&gt;<br>
     * &lt;os&gt;macos,linux,windows&lt;/os&gt;<br>
     * &lt;active&gt;true|false&lt;/active&gt;<br>
     * &lt;skip&gt;true|false&lt;/skip&gt;<br>
     */
    @Parameter
    protected List<Job> exec;

    /**
     * 输出详细信息
     */
    @Parameter
    protected boolean executeVerbose;

    public boolean isExecuteVerbose() {
        return executeVerbose;
    }

    /**
     * 判断是否忽略执行当前插件
     *
     * @return true表示忽略执行，false表示继续执行
     */
    protected boolean ignore() {
        return CollectionUtils.isEmpty(this.exec);
    }

    public void execute() throws MojoExecutionException {
        if (this.ignore()) {
            return;
        }

        ClassLoader projectClassLoader = this.createProjectClassLoader(); // 创建包含项目依赖的类加载器
        ResourcesUtils.getRepository().load(projectClassLoader); // 重新加载所有资源文件
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(projectClassLoader); // 设置线程上下文类加载器为项目类加载器
            this.execute(projectClassLoader);
        } catch (Exception e) {
            String message = this.plugin.getGroupId() + ":" + this.plugin.getArtifactId() + ":" + this.plugin.getVersion();
            throw new MojoExecutionException(message, e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader); // 恢复原始类加载器
        }
    }

    protected void execute(ClassLoader classLoader) throws Exception {
        for (Iterator<Job> it = this.exec.iterator(); it.hasNext(); ) {
            Job job = it.next();
            if (job.ignore(this)) {
                continue;
            }

            this.execute(classLoader, job);

            if (it.hasNext()) {
                getLog().info("");
            }
        }
    }

    private void execute(ClassLoader classLoader, Job job) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String cmd = StringUtils.trimBlank(job.getCommand());
        if (StringUtils.isBlank(cmd)) {
            return;
        }

        String[] array = StringUtils.splitByBlank(cmd);
        String command = array[0];
        String className = array[0];
        String methodName = "main";
        String[] args = new String[array.length - 1];
        System.arraycopy(array, 1, args, 0, args.length);

        // 如果命令不是类名，则从命令中获取类名与方法名
        if (ClassUtils.forName(command, true, classLoader) == null) {
            className = FileUtils.getFilenameNoExt(command);
            methodName = FileUtils.getFilenameExt(command);
            if (args.length == 0) {
                args = null;
            }
        }

        this.getLog().info("Executing Java class: " + className + ", method: " + methodName);
        for (int i = 0; args != null && i < args.length; i++) {
            this.getLog().info("Parameter " + StringUtils.right(i + 1, Numbers.digit(args.length), ' ') + ": " + args[i]);
        }

        // 使用项目类加载器加载类
        Class<?> clazz = classLoader.loadClass(className);
        Object instance = clazz.newInstance();

        // 自动注入 Log 类型的字段（静态字段和实例字段，非final）
        this.injectLogFields(clazz, instance);

        // 自动注入 setLog 方法（如果存在）
        if (ClassUtils.getMethod(instance, "setLog", Log.class) != null) {
            ClassUtils.executeMethod(instance, "setLog", new MavenPluginLogImpl(this));
        }

        // 通过反射设置 LogFactory 的 Builder
        this.useMavenPluginLog(classLoader);

        // 执行 main 方法
        if (args == null) {
            ClassUtils.executeMethod(instance, methodName);
        } else {
            ClassUtils.executeMethod(instance, methodName, (Object) args);
        }
    }

    /**
     * 自动注入 Log 类型的字段（包括静态字段和实例字段，非final）
     *
     * @param clazz    目标类
     * @param instance 目标实例
     */
    protected void injectLogFields(Class<?> clazz, Object instance) {
        MavenPluginLog log = new MavenPluginLogImpl(this);

        // 遍历类及其所有父类的字段
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                // 检查字段是否为 Log 类型且不是 final
                if (Log.class.isAssignableFrom(field.getType()) && !Modifier.isFinal(field.getModifiers())) {
                    JavaDialectFactory.get().setField(instance, field, log);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    /**
     * 统一使用Maven插件日志
     */
    protected void useMavenPluginLog(ClassLoader classLoader) {
        List<MavenPluginLogAware> list = SPI.load(classLoader, MavenPluginLogAware.class);
        for (MavenPluginLogAware logAware : list) {
            logAware.use(new MavenPluginLogImpl(this));
        }
    }

    /**
     * 创建包含项目依赖的类加载器
     */
    protected ClassLoader createProjectClassLoader() throws MojoExecutionException {
        try {
            List<URL> urls = new ArrayList<URL>();

            // 添加项目编译输出目录
            String outputDirectory = this.project.getBuild().getOutputDirectory();
            if (outputDirectory != null) {
                urls.add(new File(outputDirectory).toURI().toURL());

                if (this.executeVerbose) {
                    this.getLog().info("add classpath: " + outputDirectory);
                }
            }

            // 添加项目的所有依赖
            for (Object obj : this.project.getArtifacts()) {
                Artifact artifact = (Artifact) obj;
                File file = artifact.getFile();
                if (file != null) {
                    urls.add(file.toURI().toURL());

                    if (this.executeVerbose) {
                        this.getLog().info("add dependency to classpath: " + artifact.getId());
                    }
                }
            }

            // 创建 URLClassLoader，父类加载器为当前插件的类加载器
            return new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
        } catch (Exception e) {
            throw new MojoExecutionException("failed to create project classloader", e);
        }
    }
}
