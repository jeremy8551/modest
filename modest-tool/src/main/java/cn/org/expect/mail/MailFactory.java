package cn.org.expect.mail;

/**
 * 根据 Hutool 版本创建邮件工具
 */
public class MailFactory {

    /** Hutool 5.7 邮件实现类 */
    private static final String HUTOOL_57_MAIL = HutoolMailV57.class.getName();

    /** Hutool 5.8 邮件实现类 */
    private static final String HUTOOL_58_MAIL = HutoolMailV58.class.getName();

    /**
     * 创建与当前 Hutool 依赖匹配的邮件工具
     *
     * @return 邮件工具
     * @throws Exception 实现类加载或实例化失败
     */
    public static Mail build() throws Exception {
        String version = getHutoolVersion();
        String className = version.startsWith("5.8") ? HUTOOL_58_MAIL : HUTOOL_57_MAIL;
        try {
            return (Mail) Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            String fallback = HUTOOL_58_MAIL.equals(className) ? HUTOOL_57_MAIL : HUTOOL_58_MAIL;
            return (Mail) Class.forName(fallback).newInstance();
        }
    }

    /**
     * 查询运行时 Hutool 版本
     *
     * @return Hutool 版本号
     */
    private static String getHutoolVersion() {
        Package hutoolPackage = cn.hutool.core.util.StrUtil.class.getPackage();
        String version = hutoolPackage == null ? null : hutoolPackage.getImplementationVersion();
        return version == null ? "5.8" : version;
    }
}
