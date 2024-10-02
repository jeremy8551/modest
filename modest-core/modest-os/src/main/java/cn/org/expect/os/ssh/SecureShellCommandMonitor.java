package cn.org.expect.os.ssh;

import java.util.Date;

import cn.org.expect.io.BufferedLineReader;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.os.OSCommandException;
import cn.org.expect.os.OSConnectCommand;
import cn.org.expect.time.Timer;
import cn.org.expect.time.TimerException;
import cn.org.expect.time.TimerTask;
import cn.org.expect.util.Dates;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 用于防止 SSH 协议实现类超时中断退出
 */
public class SecureShellCommandMonitor extends TimerTask {
    private final static Log log = LogFactory.getLog(SecureShellCommandMonitor.class);

    /** 循环检查的周期，默认3分钟 */
    public static int PERIOD = 3 * 60 * 1000;

    /** 启动命令超时监听器的阀值,单位秒 */
    public final static int START_MONITOR = 30;

    /** 最近一次执行命令的时间 */
    private Date lastRunningTime;

    /** 监听器归属的 SSH 终端 */
    private SecureShellCommand terminal;

    /** 操作系统host */
    private String host;

    /** sshd服务的端口 */
    private int port;

    /** 登录用户名 */
    private String username;

    /** 登录密码 */
    private String password;

    /**
     * 初始化
     */
    public SecureShellCommandMonitor() {
        this.setTaskId("SSHClientMonitor" + StringUtils.toRandomUUID());
        this.setSchedule(Timer.SCHEDULE_DELAY_LOOP);
        this.setPeriod(SecureShellCommandMonitor.PERIOD);
        this.setDelay(SecureShellCommandMonitor.PERIOD);
    }

    /**
     * 启动监听器
     */
    public synchronized boolean startMonitor(SecureShellCommand client) {
        this.terminal = client;
        this.host = client.getProperty(OSConnectCommand.HOST);
        this.port = Integer.parseInt(client.getProperty(OSConnectCommand.PORT));
        this.username = client.getProperty(OSConnectCommand.USERNAME);
        this.password = client.getProperty(OSConnectCommand.PASSWORD);
        return true;
    }

    public void execute() throws TimerException {
        if (this.sendKeepAliveMsg(this.terminal)) {
            return;
        }

        if (this.terminal == null) {
            this.cancel();
            return;
        }

        String pid = this.terminal.getPid();
        if (StringUtils.isBlank(pid)) {
            this.cancel();
            return;
        }

        SecureShellCommand client = new SecureShellCommand();
        try {
            if (!client.connect(this.host, this.port, this.username, this.password)) { // establish a ssh2 connection
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg001", this.username + "@" + this.host + ":" + this.port + "?password=" + this.password));
                }
                return;
            }

            if (!this.isRunning(client, pid) && this.isKilled(client, pid, this.lastRunningTime)) {
                this.terminal.getConfig().setProperty("terminate", "killed");
            }
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg004"), e);
            }
        } finally {
            client.close();
        }
    }

    /**
     * 判断ssh终端是否正在执行命令
     *
     * @param client ssh终端
     * @param pid    命令的进程编号
     * @return 返回true表示正在运行命令
     * @throws OSCommandException 运行命令发生错误
     */
    public boolean isRunning(SecureShellCommand client, String pid) throws OSCommandException {
        String shell = "ps -p " + pid + " -o comm=";
        int exitcode = client.execute(shell, 60000, null, null);
        if (exitcode == 0 || exitcode == 1) {
            if (StringUtils.isBlank(client.getStdout())) { // not exists system process
                if (log.isInfoEnabled()) {
                    log.info(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg006", pid));
                }

                Dates.sleep(3000); // wait 3sec
                this.terminal.terminate(); // terminate task
                this.cancel();
                return false;
            } else {
                if (log.isInfoEnabled()) {
                    log.info(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg005", pid));
                }
                this.lastRunningTime = new Date();
                return true;
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg004", shell + ", exitcode is " + exitcode));
            }
            return true;
        }
    }

    /**
     * 通知 SSH 服务器保持连接
     *
     * @param client shell接口
     * @return 返回true表示发送存活信号成功
     */
    public boolean sendKeepAliveMsg(SecureShellCommand client) {
        try {
            if (client == null) {
                return false;
            } else {
                client.getSession().sendKeepAliveMsg();
                return true;
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getLocalizedMessage(), e);
            }
            return false;
        }
    }

    /**
     * 判断进程是否是被kill命令终止的
     *
     * @param client         ssh终端
     * @param pid            进程编号
     * @param lastActiveTime 进程最后活动时间(用于缩小在查找历史记录的时间范围)
     * @return 返回true表示命令已被终止
     * @throws OSCommandException 运行命令发生错误
     */
    private boolean isKilled(SecureShellCommand client, String pid, Date lastActiveTime) throws OSCommandException {
        client.execute("export HISTTIMEFORMAT=\"%F %T \" && history", 0, null, null);
        String historyLog = client.getStdout();
        int dateStartPos = -1;
        int timeEndPos = -1;
        int cmdStartPos = -1;
        BufferedLineReader in = new BufferedLineReader(historyLog);
        try {
            while (in.hasNext()) {
                String line = in.next();
                if (cmdStartPos == -1) {
                    String[] array = StringUtils.splitByBlank(StringUtils.trimBlank(line));
                    if (array.length >= 4 && Dates.testFormat10(array[1])) {
                        dateStartPos = line.indexOf(array[1]);
                        if (dateStartPos == -1) {
                            throw new IllegalArgumentException(line);
                        }

                        int timeStartPos = line.indexOf(array[2], dateStartPos + array[1].length()); // index of timestamp string pos
                        if (timeStartPos == -1) {
                            throw new IllegalArgumentException(line);
                        }

                        timeEndPos = timeStartPos + array[2].length();
                        cmdStartPos = StringUtils.indexOfNotBlank(line, timeEndPos, -1); // shell command start position
                        if (cmdStartPos == -1) {
                            throw new IllegalArgumentException(line);
                        }
                    }
                }

                // 在用户历史命令列表中查找进程编号
                int killCmdIdx = StringUtils.indexOf(line, "kill", cmdStartPos, true);
                if (killCmdIdx != -1) {
                    int shellPidIdx = StringUtils.indexOf(line, pid, killCmdIdx, true);
                    if (shellPidIdx != -1) {
                        if (lastActiveTime == null) {
                            if (log.isInfoEnabled()) {
                                log.info(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg007", pid, line));
                            }
                            return true;
                        }

                        String dateStr = line.substring(dateStartPos, timeEndPos);
                        if (Dates.parse(dateStr).compareTo(lastActiveTime) >= 0) {
                            if (log.isInfoEnabled()) {
                                log.info(ResourcesUtils.getMessage("ssh2.jsch.standard.output.msg007", pid, line));
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        } finally {
            IO.close(in);
        }
    }

    public void terminate() throws TimerException {
    }

}
