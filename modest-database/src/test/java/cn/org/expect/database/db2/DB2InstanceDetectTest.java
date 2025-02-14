package cn.org.expect.database.db2;

import java.io.IOException;
import java.util.List;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.os.OS;
import cn.org.expect.os.OSCpu;
import cn.org.expect.os.OSDisk;
import cn.org.expect.os.OSFile;
import cn.org.expect.os.OSFileCommand;
import cn.org.expect.os.OSMemory;
import cn.org.expect.os.OSNetworkCard;
import cn.org.expect.os.OSProcess;
import cn.org.expect.os.OSUser;
import cn.org.expect.os.OSUserGroup;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.EasyRunIf;
import cn.org.expect.util.Dates;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.TimeWatch;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试远程连接Linux与DB2和WAS探测 TODO
 */
@RunWith(ModestRunner.class)
@EasyRunIf(values = {"db2.host", "db2.ssh.port", "db2.ssh.username", "db2.ssh.password"})
public class DB2InstanceDetectTest {
    private final static Log log = LogFactory.getLog(DB2InstanceDetectTest.class);

    @EasyBean("${db2.host}")
    private String host;

    @EasyBean("${db2.ssh.port}")
    private String port;

    @EasyBean("${db2.ssh.username}")
    private String username;

    @EasyBean("${db2.ssh.password}")
    private String password;

    @EasyBean
    private EasyContext context;

    @Test
    public void test() throws IOException {
        TimeWatch watch = new TimeWatch();
        OS os = this.context.getBean(OS.class, this.host, StringUtils.parseInt(this.port, 22), this.username, this.password);
        try {
            log.info("");
            log.info("");
            log.info("操作系统名: " + os.getName());

            log.info("");
            log.info("");
            log.info("内核版本号: " + os.getKernelVersion());

            log.info("");
            log.info("");
            log.info("发行版本号: " + os.getReleaseVersion());

            log.info("");
            log.info("");
            log.info("打开操作系统的命令操作接口: " + os.enableOSCommand());

            log.info("");
            log.info("");
            log.info("打开操作系统的文件操作接口: " + os.enableOSFileCommand());
            OSFileCommand fileCmd = os.getOSFileCommand();

            watch.start();

            log.info("");
            log.info("");
            log.info("处理器信息: ");
            List<OSCpu> ps = os.getOSCpus();
            for (OSCpu p : ps) {
                log.info("cpus: " + p);
            }

            log.info("");
            log.info("");
            log.info("内存信息: ");
            OSMemory mems = os.getOSMemory();
            log.info("mems: " + mems);

            log.info("");
            log.info("");
            log.info("硬盘信息: ");
            List<OSDisk> disks = os.getOSDisk();
            for (OSDisk disk : disks) {
                log.info("disk: " + disk);
            }

            log.info("");
            log.info("");
            log.info("网卡信息: ");
            List<OSNetworkCard> cards = os.getOSNetwork().getOSNetworkCards();
            for (OSNetworkCard c : cards) {
                log.info("nets: " + c);
            }
            log.info("用时: " + watch.useTime());

            log.info("初始化用时: " + watch.useTime());
            Dates.sleep(2000);
            log.info("当前用户是: " + os.getUser());
            log.info("判断目录 " + os.getUser().getHome() + " 是否存在: " + fileCmd.exists(os.getUser().getHome()));

            watch.start();
            log.info("");
            log.info("");
            log.info("用户信息: ");
            for (OSUser user : os.getUsers()) {
                log.info(user);
            }

            log.info("");
            log.info("");
            log.info("用户组信息: ");
            for (OSUserGroup obj : os.getGroups()) {
                log.info(obj);
            }

            watch.start();
            log.info("");
            log.info("");
            log.info("查找 java 进程");
            List<OSProcess> array = os.getOSProgressList("java");
            for (OSProcess p : array) {
                OSProcess osp = os.getOSProgress(p.getPid());
                if (osp == null) {
                    throw new NullPointerException();
                }
            }
            log.info("用时: " + watch.useTime());

            log.info("");
            log.info("");
            log.info("");
            watch.start();
            List<DB2Instance> insts = DB2Instance.get(os);
            for (DB2Instance inst : insts) {
                log.info(inst);

                String[] databaseNames = inst.getDatabaseNames();
                for (String databaseName : databaseNames) {
                    log.info("");
                    log.info("");
                    int dbport = inst.getDatabase(databaseName).getPort();
                    log.info("在实例 " + inst.getName() + " 上查找数据库 " + databaseName + " 端口号 " + dbport + " 对应的服务信息: " + os.getOSService(dbport));
                }

                os.enableOSFileCommand();
                List<OSFile> find = os.getOSFileCommand().find(inst.getUser().getHome(), "db2profile", 'f', null);
                for (OSFile f : find) {
                    log.info("");
                    log.info("数据库配置文件在 " + f);
                }
            }
            log.info("用时: " + watch.useTime());
        } finally {
            os.close();
        }
    }
}
