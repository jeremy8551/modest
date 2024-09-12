package cn.org.expect.os;

/**
 * 用于描述网卡信息<br>
 * 如：以太网卡，WIFI网卡，USB网卡，雷电网卡
 *
 * @author jeremy8551@qq.com
 * @createtime 2020-09-06
 */
public interface OSNetworkCard {

    /** 以太网卡 */
    int TYPE_ETHERNET = 1;

    /** 无线网卡 */
    int TYPE_WIRELESS = 2;

    /** InfiniBand网络设备 */
    int TYPE_INFINIBAND = 3;

    /** 网桥设备 */
    int TYPE_BRIDGE = 4;

    /** BOND网卡：将多个物理网卡绑定到一个通道上 */
    int TYPE_BOND = 5;

    /** VLAN网卡配置 */
    int TYPE_VLAN = 6;

    /** Team网络（支持负载均衡和链路聚合，最多支持8块网卡） */
    int TYPE_TEAM = 7;

    /** 多网卡链路聚合 */
    int TYPE_TEAMPORT = 8;

    /** 未知的网卡 */
    int TYPE_UNKNOWN = 9;

    /**
     * 返回网卡或网络设备类型：Ethernet, Wireless, InfiniBand, Bridge, Bond, Vlan, Team, TeamPort
     *
     * @return 返回网卡类型值
     */
    int getType();

    /**
     * 判断网卡或网络设备是否可用
     *
     * @return 返回true表示网卡可用
     */
    boolean isEnabled();

    /**
     * 返回网卡或网络设备的名字：eth0
     *
     * @return 网络设备的名字
     */
    String getName();

    /**
     * 返回网卡或网络设备的 Ipv4 地址 <br>
     * 192.168.1.100
     *
     * @return Ipv4 地址
     */
    String getIPAddress();

    /**
     * 返回网卡或网络设备的 Ipv6 地址
     *
     * @return Ipv6 地址
     */
    String getIP6Address();

    /**
     * 返回网卡或网络设备的子网掩码 255.255.255.0
     *
     * @return 子网掩码
     */
    String getMask();

    /**
     * 返回网卡或网络设备的网关地址 192.168.1.1
     *
     * @return 网关地址
     */
    String getGateway();

    /**
     * 返回网卡或网络设备的MAC地址 a3:34:h8:18:d8:q8
     *
     * @return MAC地址
     */
    String getMacAddress();

    /**
     * 返回网卡或网络设备的 DNS 服务器地址
     *
     * @return DNS 服务器地址
     */
    String getDNS1();

    /**
     * 返回网卡或网络设备的备选 DNS 服务器地址
     *
     * @return DNS 服务器地址
     */
    String getDNS2();

    /**
     * 判断网卡或网络设备是否使用DHCP方式分配IP地址
     *
     * @return 返回true表示使用DHCP分配地址
     */
    boolean isDHCP();

    /**
     * 判断网卡或网络设备是否使用静态IP地址
     *
     * @return 返回true表示使用静态IP地址
     */
    boolean isStatic();

}
