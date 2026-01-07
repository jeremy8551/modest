package cn.org.expect.maven.plugin.execute;

import cn.org.expect.util.StringUtils;
import org.apache.maven.plugin.logging.Log;

public class Job {

    /**
     * 执行命令
     */
    private String command;

    /**
     * Maven插件目标执行依赖的操作系统：windows、linux、macos
     */
    private String os;

    /**
     * 激活或忽略任务
     */
    private String active;

    /**
     * 跳过或执行任务
     */
    private String skip;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public boolean ignore(ExecuteMojo mojo) {
        Log log = mojo.getLog();
        if (StringUtils.isNotBlank(this.os)) {
            if (mojo.isExecuteVerbose()) {
                log.info("os: " + this.os + ", os.name: " + System.getProperty("os.name"));
            }

            String[] array = StringUtils.split(this.os, ',');
            for (String os : array) {
                if (StringUtils.isBlank(os)) {
                    continue;
                }

                if ("windows".equalsIgnoreCase(os)) {
                    if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                        return true;
                    }
                } else if ("linux".equalsIgnoreCase(os)) {
                    if (!System.getProperty("os.name").toLowerCase().contains("linux")) {
                        return true;
                    }
                } else if ("macos".equalsIgnoreCase(os)) {
                    if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
                        return true;
                    }
                }
            }
        }

        if (mojo.isExecuteVerbose()) {
            log.info("skip: " + this.skip);
        }

        // 检查是否忽略执行
        if (StringUtils.isNotBlank(this.skip) && Boolean.parseBoolean(StringUtils.trimBlank(this.skip))) {
            return true;
        }

        if (mojo.isExecuteVerbose()) {
            log.info("active: " + this.active);
        }

        // 检查是否忽略执行
        return StringUtils.isNotBlank(this.active) && !Boolean.parseBoolean(StringUtils.trimBlank(this.active));
    }
}
