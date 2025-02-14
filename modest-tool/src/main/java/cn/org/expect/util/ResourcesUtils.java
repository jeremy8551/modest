package cn.org.expect.util;

import cn.org.expect.message.ResourceMessageBundleRepository;

/**
 * 资源文件工具
 *
 * @author jeremy8551@gmail.com
 */
public class ResourcesUtils {

    /** 外部资源配置文件路径 */
    public final static String PROPERTY_RESOURCE = Settings.getPropertyName("resource");

    /** 国际化资源配置 */
    public final static String PROPERTY_LOCALE = Settings.getPropertyName("locale");

    /** 国际化资源信息仓库 */
    protected final static ResourceMessageBundleRepository repository = new ResourceMessageBundleRepository(ClassUtils.getClassLoader());

    /**
     * 国际化资源信息仓库
     *
     * @return 国际化资源信息仓库
     */
    public static ResourceMessageBundleRepository getRepository() {
        return repository;
    }

    /**
     * 返回资源文件中属性值
     *
     * @param key  属性名
     * @param args 属性值中占位符对应的参数
     * @return 属性值
     */
    public static String getMessage(String key, Object... args) {
        String message = repository.get(key);
        return StringUtils.replaceIndexHolder(message, args);
    }

    /**
     * 返回 true 表示存在国际化信息
     *
     * @param key 资源标签
     * @return 返回true表示存在国际化信息
     */
    public static boolean existsMessage(String key) {
        return repository.contains(key);
    }
}
