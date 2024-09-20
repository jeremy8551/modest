package cn.org.expect.os;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import javax.script.SimpleBindings;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ClassUtils;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.ObjectUtils;
import cn.org.expect.util.StringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class WithSSHRule implements TestRule {

    /** 容器上下文信息 */
    protected DefaultEasyContext context;

    /** 脚本引擎的环境变量集合 */
    protected WithDBConfig environment;

    /** true表示找不到数据库 */
    protected boolean notFindServer;

    public WithSSHRule() {
    }

    public Statement apply(Statement statement, Description description) {
        this.init();
        return new WithDBStatement(this, statement);
    }

    /**
     * 返回容器上下文信息
     *
     * @return 容器上下文信息
     */
    public DefaultEasyContext getContext() {
        this.init();
        return context;
    }

    /**
     * 返回脚本引擎的环境变量集合
     *
     * @return 环境变量集合
     */
    public SimpleBindings getEnvironment() {
        this.init();
        return environment;
    }

    protected void init() {
        if (context == null) {
            context = new DefaultEasyContext("sout:info");

            try {
                environment = new WithDBConfig(context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                String sshhost = (String) environment.get("ssh.host");
                this.notFindServer = !Ping.run(sshhost);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class WithDBStatement extends Statement {
        private Statement statment;
        private WithSSHRule rule;

        public WithDBStatement(WithSSHRule rule, Statement statment) {
            this.statment = statment;
            this.rule = rule;
        }

        @Override
        public void evaluate() throws Throwable {
            if (rule.notFindServer) {
                System.out.println("**************** 未配置SSH服务器 ****************");
                return;
            }

            try {
                System.out.println("================ 测试开始 ====================");
                System.out.println();
                this.statment.evaluate();
            } finally {
                System.out.println();
                System.out.println("================ 测试结束 ====================");
            }
        }
    }

    public static class WithDBConfig extends SimpleBindings {

        public WithDBConfig(EasyContext context) throws IOException {
            super();

            Properties p = this.load();
            String jdbcUrl = p.getProperty("database.url");
            String driverClassname = p.getProperty("database.driverClassName");
            String dbusername = p.getProperty("database.admin");
            String dbuserpass = p.getProperty("database.adminPw");

            this.put("curr_dir_path", FileUtils.joinPath(ClassUtils.getClasspath(WithSSHRule.class), "script"));
            this.put("temp", FileUtils.getTempDir("test", WithDBConfig.class.getSimpleName()).getAbsolutePath());
            this.put("databaseDriverName", driverClassname);
            this.put("databaseUrl", jdbcUrl);
            this.put("username", dbusername);
            this.put("password", dbuserpass);
            this.put("admin", dbusername);
            this.put("adminPw", dbuserpass);
            this.put("databaseHost", p.getProperty("database.host"));
            this.put("databaseSSHUser", p.getProperty("database.ssh.username"));
            this.put("databaseSSHUserPw", p.getProperty("database.ssh.password"));
            this.put("ftphost", p.getProperty("ftp.host"));
            this.put("ftpuser", p.getProperty("ftp.username"));
            this.put("ftppass", p.getProperty("ftp.password"));
            this.put("proxyhost", p.getProperty("proxy.host"));
            this.put("proxyuser", p.getProperty("proxy.ssh.username"));
            this.put("proxypass", p.getProperty("proxy.ssh.password"));
            this.put("sshhost", p.getProperty("ssh.host"));
            this.put("sshusername", p.getProperty("ssh.username"));
            this.put("sshpassword", p.getProperty("ssh.password"));
            this.put("adminUsername", dbusername);
            this.put("adminPassword", dbuserpass);
        }

        public Properties load() throws IOException {
            Properties p = new Properties();
            p.load(ClassUtils.getResourceAsStream("/testconfig.properties"));

            String envmode = WithSSHRule.class.getPackage().getName() + ".test.mode";
            String mode = ObjectUtils.coalesce(System.getProperty(envmode), "home");
            InputStream in = ClassUtils.getResourceAsStream("/testconfig-" + mode + ".properties");
            if (in != null) {
                p.load(in);
            }

            // 将 Properties 中属性保存到集合中
            for (Iterator<Object> it = p.keySet().iterator(); it.hasNext(); ) {
                String key = StringUtils.trimBlank(it.next());
                String value = StringUtils.trimBlank(p.getProperty(key));
                this.put(key, value);
            }
            return p;
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null || (key instanceof String && ((String) key).length() == 0)) {
                return false;
            } else {
                return super.containsKey(key);
            }
        }

        @Override
        public Object get(Object key) {
            if (key == null || (key instanceof String && ((String) key).length() == 0)) {
                return null;
            } else {
                return super.get(key);
            }
        }
    }

}
