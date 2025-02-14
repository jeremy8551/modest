package cn.org.expect.os;

import java.io.IOException;
import java.util.List;

import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.os.macos.MacOS;
import cn.org.expect.util.Dates;
import cn.org.expect.util.TimeWatch;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MacOSTest {
    private final static Log log = LogFactory.getLog(MacOSTest.class);

    @Test
    public void test() throws IOException {
        TimeWatch watch = new TimeWatch();
        MacOS os = null;
        try {
            os = new MacOS();
            log.info(os.getName() + ", kernel: " + os.getKernelVersion() + ", release: " + os.getReleaseVersion());
            Assert.assertTrue(os.enableOSFileCommand());
            OSFileCommand fileCmd = os.getOSFileCommand();

            log.info("初始化用时: " + watch.useTime());
            Dates.sleep(2000);

            log.info("current user is " + os.getUser());
            log.info("exist / " + fileCmd.exists("/"));

            OSDateCommand datecmd = os.getOSDateCommand();
            log.info("current time is " + Dates.format19(datecmd.getDate()));

            watch.start();
            log.info("");
            log.info("");
            log.info("");
            for (OSUser user : os.getUsers()) {
                log.info(user);
            }

            log.info("");
            log.info("");
            log.info("");
            for (OSUserGroup obj : os.getGroups()) {
                log.info(obj);
            }

            log.info("");
            log.info("");
            log.info("");
            for (OSService obj : os.getOSServices()) {
                log.info(obj);
            }
            log.info("service: " + os.getOSService(60000));
            log.info("用时: " + watch.useTime());

            List<OSCpu> ps = os.getOSCpus();
            for (OSCpu p : ps) {
                log.info("cpus: " + p);
            }

            List<OSDisk> disks = os.getOSDisk();
            for (OSDisk d : disks) {
                log.info(d.toString());
            }

            OSMemory memorys = os.getOSMemory();
            log.info(memorys);

            watch.start();
            log.info("");
            log.info("");
            log.info("");
            List<OSProcess> list = os.getOSProgressList("java");
            for (OSProcess process : list) {
                log.info("pid: " + process.getPid());
                log.info("ppid: " + process.getPPid());
                log.info("cmd: " + process.getCmd());
                log.info("name: " + process.getName());
                log.info("cpu: " + process.getCpu());
                log.info("memory: " + process.getMemory());
                log.info("");
                log.info("");
            }

            log.info("用时: " + watch.useTime());
        } finally {
            os.close();
        }
    }
}
