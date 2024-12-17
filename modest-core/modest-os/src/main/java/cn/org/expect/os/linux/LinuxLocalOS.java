package cn.org.expect.os.linux;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.expression.DataUnitExpression;
import cn.org.expect.io.BufferedLineReader;
import cn.org.expect.os.OS;
import cn.org.expect.os.OSCommand;
import cn.org.expect.os.OSCommandException;
import cn.org.expect.os.OSCommandStdouts;
import cn.org.expect.os.OSCpu;
import cn.org.expect.os.OSDateCommand;
import cn.org.expect.os.OSDisk;
import cn.org.expect.os.OSFileCommand;
import cn.org.expect.os.OSMemory;
import cn.org.expect.os.OSNetwork;
import cn.org.expect.os.OSNetworkCard;
import cn.org.expect.os.OSProcess;
import cn.org.expect.os.OSService;
import cn.org.expect.os.OSUser;
import cn.org.expect.os.OSUserGroup;
import cn.org.expect.os.internal.OSCommandUtils;
import cn.org.expect.os.internal.OSDiskImpl;
import cn.org.expect.os.internal.OSMemoryImpl;
import cn.org.expect.os.internal.OSNetworkCardImpl;
import cn.org.expect.os.internal.OSProcessorImpl;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.CollectionUtils;
import cn.org.expect.util.Dates;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.Settings;
import cn.org.expect.util.StringUtils;

/**
 * Linux 本地操作系统的接口实现类
 */
@EasyBean(value = "linux")
public class LinuxLocalOS implements OS, OSDateCommand, OSNetwork {

    /** unix 系统保留的文件名 */
    public final static Set<String> KEY_FILENAMES = Collections.unmodifiableSet(new HashSet<String>(ArrayUtils.asList(".", "..")));

    /** 操作系统文件命令接口 */
    protected LinuxFileCommand filecmd;

    /** 系统命令执行接口 */
    protected LinuxCommand cmd;

    /** 操作系统名 */
    protected String name;

    /** 操作系统发行版本号 */
    protected String release;

    /** 操作系统内核版本号 */
    protected String kernel;

    /**
     * 初始化
     *
     * @throws IOException 访问文件发生错误
     */
    public LinuxLocalOS() throws IOException {
        this.cmd = new LinuxCommand();
        this.filecmd = new LinuxFileCommand();
        this.init();
    }

    /**
     * 读取操作系统的版本信息
     *
     * @throws IOException 访问文件发生错误
     */
    protected void init() throws IOException {
        File file = new File("/proc/version");
        if (file.exists()) {
            String kernelStr = FileUtils.readline(file, CharsetName.ISO_8859_1, 0);
            if (StringUtils.isNotBlank(kernelStr)) {
                String[] array = StringUtils.splitByBlank(StringUtils.trimBlank(kernelStr));
                if (array.length < 3) {
                    throw new IllegalArgumentException(kernelStr + FileUtils.lineSeparator + Arrays.toString(array));
                }

                this.name = array[0];
                this.kernel = array[2];
            }
        }

        // 如果不能读取 /proc/version 文件，则执行 uname 命令
        if (StringUtils.isBlank(this.name)) {
            this.cmd.execute("uname -a");
            String uname = this.cmd.getStdout();
            String[] array = StringUtils.splitByBlank(StringUtils.trimBlank(uname));
            if (array.length < 3) {
                throw new IllegalArgumentException(uname + FileUtils.lineSeparator + Arrays.toString(array));
            }
            this.name = array[0];
            this.kernel = array[2];
        }

        this.cmd.execute("cat /etc/*-release");
        this.release = StringUtils.trimBlank(this.cmd.getStdout());
    }

    /**
     * 执行操作系统上的命令语句
     *
     * @param command 命令语句
     * @return 命令返回值
     */
    protected int executeCommand(CharSequence command) {
        try {
            return this.cmd.execute(command.toString());
        } catch (Exception e) {
            throw new OSCommandException(command.toString(), e);
        }
    }

    public boolean hasUser(String username) {
        return this.getUser(username) != null;
    }

    public boolean addUser(String username, String password, String group, String home, String shell) {
        Ensure.notBlank(username);
        Ensure.notNull(password);

        StringBuilder command = new StringBuilder();
        command.append("useradd");
        if (StringUtils.isNotBlank(group)) {
            command.append(" -g ").append(group);
        }
        if (StringUtils.isNotBlank(home)) {
            command.append(" -d ").append(home);
        }
        if (StringUtils.isNotBlank(shell)) {
            command.append(" -s ").append(shell);
        }
        command.append(" -m ").append(username);

        int value = this.executeCommand(command.toString());
        if (value == 0) {
            return this.executeCommand("echo \"" + password + "\" | passwd " + username + " --stdin > /dev/null 2>&1") == 0;
        } else {
            return false;
        }
    }

    public List<OSUserGroup> getGroups() {
        BufferedLineReader in = new BufferedLineReader(new File("/etc/group"), CharsetName.ISO_8859_1);
        try {
            List<OSUserGroup> list = new ArrayList<OSUserGroup>();
            while (in.hasNext()) {
                String line = in.next();
                String[] array = StringUtils.split(line, ':');
                if (array.length == 4) {
                    LinuxGroup group = new LinuxGroup();
                    group.setName(array[0]);
                    group.setPassword(array[1]);
                    group.setGid(array[2]);
                    String[] usernames = StringUtils.split(array[3], ',');
                    for (String name : usernames) {
                        if (StringUtils.isNotBlank(name)) {
                            group.addUser(name);
                        }
                    }
                    list.add(group);
                }
            }
            return list;
        } finally {
            IO.close(in);
        }
    }

    public OSUser getUser() {
        String username = Settings.getUserName();
        OSUser user = this.getUser(username);
        if (user == null) {
            LinuxUser obj = new LinuxUser();
            obj.setName(username);
            obj.setHome(Settings.getUserHome().getAbsolutePath());
            obj.setProfiles(this.getUserProfile(obj.getHome()));
            obj.setShell(System.getenv("SHELL"));
            return obj;
        }
        return user;
    }

    public OSUser getUser(String username) {
        List<OSUser> users = this.getUsers();
        for (OSUser user : users) {
            if (user.getName().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public List<OSUser> getUsers() {
        BufferedLineReader in = new BufferedLineReader(new File("/etc/passwd"), CharsetName.ISO_8859_1);
        try {
            List<OSUser> list = new ArrayList<OSUser>();
            while (in.hasNext()) {
                String line = in.next();
                String[] array = StringUtils.split(line, ':');
                if (array.length == 7) {
                    LinuxUser user = new LinuxUser();
                    user.setName(array[0]);
                    user.setPassword(array[1]);
                    user.setId(array[2]);
                    user.setGroup(array[3]);
                    user.setMemo(array[4]);
                    user.setHome(array[5]);
                    user.setShell(array[6]);
                    user.setProfiles(this.getUserProfile(user.getHome()));
                    list.add(user);
                }
            }
            return list;
        } finally {
            IO.close(in);
        }
    }

    /**
     * 读取用户配置文件集合
     *
     * @param userhome 用户根目录绝对路径
     * @return 文件路径集合
     */
    protected List<String> getUserProfile(String userhome) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isBlank(userhome)) {
            return list;
        }

        File dir = new File(userhome);
        if (dir.exists() && dir.canRead()) {
            File[] files = FileUtils.array(dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return StringUtils.inArray(name, ".bash_profile", ".bashrc", ".bash_login", ".profile");
                }
            }));

            for (File file : files) {
                String filename = file.getName();
                if (filename.equals(".bash_profile")) {
                    list.add(file.getAbsolutePath());
                } else if (filename.equals(".bashrc")) {
                    list.add(file.getAbsolutePath());
                } else if (filename.equals(".bash_login")) {
                    list.add(file.getAbsolutePath());
                } else if (filename.equals(".profile")) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }

    public List<OSService> getOSServices() {
        BufferedLineReader in = new BufferedLineReader(new File("/etc/services"), CharsetName.ISO_8859_1);
        try {
            List<OSService> list = new ArrayList<OSService>();
            while (in.hasNext()) {
                String line = in.next();
                LinuxEtcService service = LinuxEtcService.newInstance(line);
                if (service != null) {
                    list.add(service);
                }
            }
            return list;
        } finally {
            IO.close(in);
        }
    }

    public OSService getOSService(int port) {
        List<OSService> services = this.getOSServices();
        for (OSService obj : services) {
            if (obj.getPort() == port) {
                return obj;
            }
        }
        return null;
    }

    public List<OSService> getOSService(String name) {
        List<OSService> list = new ArrayList<OSService>();
        List<OSService> services = this.getOSServices();
        for (OSService obj : services) {
            if (obj.getName().contains(name)) {
                list.add(obj);
            }
        }
        return list;
    }

    public OSCommand getOSCommand() {
        return this.cmd;
    }

    public boolean delUser(String username) {
        OSUser user = this.getUser(username);
        return user != null && this.executeCommand("userdel -r " + username) == 0;
    }

    public boolean changePassword(String username, String password) {
        return this.executeCommand("echo \"" + password + "\" | passwd " + username + " --stdin > /dev/null 2>&1") == 0;
    }

    public boolean supportOSCommand() {
        return true;
    }

    public boolean supportOSFileCommand() {
        return true;
    }

    public String getName() {
        return this.name;
    }

    public String getKernelVersion() {
        return this.kernel;
    }

    public String getReleaseVersion() {
        return this.release;
    }

    public OSFileCommand getOSFileCommand() {
        return this.filecmd;
    }

    public String getLineSeparator() {
        return FileUtils.lineSeparator;
    }

    public char getFolderSeparator() {
        return File.separatorChar;
    }

    public List<OSProcess> getOSProgressList(String findStr) {
        String command = "ps -efl ";
        if (StringUtils.isNotBlank(findStr)) {
            command = "ps -efl | head -n 1; ps -efl | grep -v grep | grep " + findStr;
        }

        this.executeCommand(command);
        List<Map<String, String>> listmap = OSCommandUtils.splitPSCmdStdout(this.cmd.getStdout());
        List<OSProcess> list = new ArrayList<OSProcess>();
        for (Map<String, String> map : listmap) {
            LinuxProgress p = new LinuxProgress(this);
            p.setCpu(map.get("C"));
            p.setMemory(StringUtils.isLong(map.get("SZ")) ? Long.parseLong(map.get("SZ")) : 0);
            p.setPid(map.get("pid"));
            p.setPpid(map.get("ppid"));
            p.setCmd(map.get("cmd"));
            list.add(p);
        }
        return list;
    }

    public OSProcess getOSProgress(String pid) {
        List<OSProcess> list = this.getOSProgressList(null);
        for (OSProcess process : list) {
            if (process.getPid().equals(pid)) {
                return process;
            }
        }
        return null;
    }

    public boolean isEnableOSCommand() {
        return true;
    }

    public boolean isEnableOSFileCommand() {
        return true;
    }

    public boolean enableOSCommand() {
        return true;
    }

    public void disableOSCommand() {
    }

    public boolean enableOSFileCommand() {
        return true;
    }

    public void disableOSFileCommand() {
    }

    public void close() {
    }

    public Date getDate() {
        return new Date();
    }

    public synchronized boolean setDate(Date date) {
        return this.cmd.execute("date -s " + StringUtils.quotes(Dates.format19(date))) == 0;
    }

    public boolean supportOSDateCommand() {
        return true;
    }

    public OSDateCommand getOSDateCommand() {
        return this;
    }

    public boolean supportOSNetwork() {
        return true;
    }

    public OSNetwork getOSNetwork() {
        return this;
    }

    public List<OSNetworkCard> getOSNetworkCards() {
        List<OSNetworkCard> list = new ArrayList<OSNetworkCard>();

        File[] files = FileUtils.array(new File("/etc/sysconfig/network-scripts").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("ifcfg-");
            }
        }));

        // /etc/resolv.conf
        String dns1 = "", dns2 = "";
        BufferedLineReader in = new BufferedLineReader(new File("/etc/resolv.conf"), CharsetName.ISO_8859_1);
        try {
            while (in.hasNext()) {
                String line = in.next();
                String[] p = StringUtils.splitByBlank(StringUtils.trimBlank(Linuxs.removeShellNote(line, null)));
                if (p.length == 2) {
                    if (StringUtils.isBlank(dns1)) {
                        dns1 = p[1];
                    } else {
                        dns2 = p[1];
                    }
                }
            }
        } finally {
            IO.close(in);
        }

        for (File eth : files) {
            Map<String, String> map = new CaseSensitivMap<String>();
            BufferedLineReader cin = new BufferedLineReader(eth, CharsetName.ISO_8859_1);
            try {
                while (cin.hasNext()) {
                    String line = cin.next();
                    String[] p = StringUtils.trimBlank(StringUtils.splitProperty(Linuxs.removeShellNote(line, null)));
                    if (p != null && p.length == 2) {
                        map.put(StringUtils.trimBlank(p[0]), p[1]);
                    }
                }
            } finally {
                IO.close(cin);
            }

            OSNetworkCardImpl card = new OSNetworkCardImpl();
            card.setName(map.get("DEVICE"));
            card.setEnabled(true);
            card.setStatic(StringUtils.inArrayIgnoreCase(map.get("BOOTPROTO"), "none", "static"));
            card.setDhcp("dhcp".equalsIgnoreCase(map.get("BOOTPROTO")));
            card.setDns1(dns1);
            card.setDns2(dns2);

            String type = map.get("TYPE"); // Ethernet, Wireless, InfiniBand, Bridge, Bond, Vlan, Team, TeamPort
            if ("Ethernet".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_ETHERNET);
            } else if ("Wireless".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_WIRELESS);
            } else if ("InfiniBand".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_INFINIBAND);
            } else if ("Bridge".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_BRIDGE);
            } else if ("Bond".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_BOND);
            } else if ("Vlan".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_VLAN);
            } else if ("Team".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_TEAM);
            } else if ("TeamPort".equalsIgnoreCase(type)) {
                card.setType(OSNetworkCard.TYPE_TEAMPORT);
            } else {
                card.setType(OSNetworkCard.TYPE_UNKNOWN);
            }

            if (card.isStatic()) {
                card.setIpAddress(map.get("IPADDR"));
                card.setMask(map.get("NETMASK"));
                card.setGateway(map.get("NETWORK"));
                card.setMacAddress(map.get("HWADDR"));
                card.setIp6Address(map.get("IPV6ADDR"));
                card.setIp6Gateway(map.get("IPV6_DEFAULTGW"));
            } else {
                OSCommandStdouts maps = this.cmd.execute("show ifconfig", "ifconfig " + card.getName(), "show gateway", "route -n |grep default|awk -F\" \" '{print $2}' ");
                List<String> ifconfig = maps.get("show ifconfig");
                if (ifconfig.size() > 1) {
                    String l1 = ifconfig.get(0);
                    String l2 = ifconfig.get(1);
                    String[] a2 = StringUtils.splitByBlank(StringUtils.trimBlank(l2));
                    card.setMacAddress(ArrayUtils.lastElement(StringUtils.splitByBlank(StringUtils.trimBlank(l1))));
                    card.setMask(ArrayUtils.lastElement(a2));
                    card.setIpAddress(ArrayUtils.lastElement(StringUtils.split(a2[1], ':')));
                }

                List<String> gateway = maps.get("show gateway");
                if (gateway.size() > 0) {
                    card.setGateway(StringUtils.trimBlank(gateway.get(0)));
                }
            }

            list.add(card);
        }

        Collections.sort(list, new Comparator<OSNetworkCard>() {
            public int compare(OSNetworkCard o1, OSNetworkCard o2) {
                if (o1.getIPAddress().equals("127.0.0.1") || !o1.isEnabled()) {
                    return 1;
                } else if (o2.getIPAddress().equalsIgnoreCase("127.0.0.1") || !o2.isEnabled()) {
                    return -1;
                } else {
                    return o1.getType() - o2.getType();
                }
            }
        });

        return list;
    }

    public List<OSCpu> getOSCpus() {
        BufferedLineReader in = new BufferedLineReader(new File("/proc/cpuinfo"), CharsetName.ISO_8859_1);
        try {
            List<OSCpu> list = new ArrayList<OSCpu>();
            Map<String, String> map = new CaseSensitivMap<String>();
            while (in.hasNext()) {
                map.clear();

                do {
                    String line = in.next();
                    String[] array = StringUtils.trimBlank(StringUtils.split(line, ':'));
                    if (StringUtils.isBlank(line) || array.length != 2) {
                        break;
                    } else {
                        map.put(StringUtils.trimBlank(array[0]), array[1]);
                    }
                } while (in.hasNext());

                if (map.size() > 0) {
                    OSProcessorImpl obj = new OSProcessorImpl();
                    obj.setId(map.get("processor"));
                    obj.setModeName(map.get("model name"));
                    obj.setCoreId(map.get("core id"));
                    obj.setCacheSize(DataUnitExpression.parse(StringUtils.defaultString(map.get("cache size"), "0")));
                    obj.setCores(StringUtils.parseInt(map.get("cpu cores"), 0));
                    obj.setPhysicalId(map.get("physical id"));
                    obj.setSiblings(StringUtils.parseInt(map.get("siblings"), 0));
                    list.add(obj);
                }
            }
            return list;
        } finally {
            IO.close(in);
        }
    }

    public List<OSDisk> getOSDisk() {
        List<OSDisk> list = new ArrayList<OSDisk>();
        this.cmd.execute("df -Tk");
        BufferedLineReader in = new BufferedLineReader(StringUtils.trimBlank(this.cmd.getStdout()));
        try {
            String[] titles = StringUtils.splitByBlank(StringUtils.trimBlank(in.next()));
            int length = titles.length;
            while (in.hasNext()) {
                String line = StringUtils.trimBlank(in.next());
                String[] array = StringUtils.splitByBlank(line);
                if (array.length < length) {
                    line = line + in.next();
                    array = CollectionUtils.toArray(StringUtils.splitByBlank(line, length));
                } else if (array.length > length) {
                    array = CollectionUtils.toArray(StringUtils.splitByBlank(line, length));
                }

                Ensure.isTrue(array.length == length, line, titles);

                OSDiskImpl obj = new OSDiskImpl();
                obj.setId(array[0]);
                obj.setType(array[1]);
                obj.setTotal(DataUnitExpression.parse(array[2] + "kb"));
                obj.setUsed(DataUnitExpression.parse(array[3] + "kb"));
                obj.setFree(DataUnitExpression.parse(array[4] + "kb"));
                obj.setAmount(array[6]);
                list.add(obj);
            }
            return list;
        } finally {
            IO.close(in);
        }
    }

    public OSMemory getOSMemory() {
        BufferedLineReader in = new BufferedLineReader(new File("/proc/meminfo"), CharsetName.ISO_8859_1);
        try {
            OSMemoryImpl obj = new OSMemoryImpl();
            while (in.hasNext()) {
                String line = in.next();
                String[] array = StringUtils.trimBlank(StringUtils.split(line, ':'));

                if (array != null && array.length == 2) {
                    String name = array[0];

                    if (name.equalsIgnoreCase("MemTotal")) {
                        obj.setTotal(DataUnitExpression.parse(array[1]));
                    } else if (name.equalsIgnoreCase("MemFree")) {
                        obj.setFree(DataUnitExpression.parse(array[1]));
                    } else if (name.equalsIgnoreCase("Active")) {
                        obj.setActive(DataUnitExpression.parse(array[1]));
                    }
                }
            }
            return obj;
        } finally {
            IO.close(in);
        }
    }

    public String getHost() {
        return "127.0.0.1";
    }

}
