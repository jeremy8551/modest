package cn.org.expect.message;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import cn.org.expect.util.Ensure;
import cn.org.expect.util.Logs;

/**
 * 扫描 META-INF/modest 目录中的文件
 *
 * @author jeremy8551@gmail.com
 * @createtime 2025/1/27
 */
public class ResourceScanner implements Iterator<InputStream> {

    /** 类加载器 */
    private final ClassLoader classLoader;

    /** 资源文件名 */
    private final String resourceName;

    /** 资源文件集合的遍历器 */
    private Enumeration<URL> enumeration;

    /** 资源文件的输入流 */
    private InputStream inputStream;

    /**
     * 初始化
     *
     * @param classLoader  类加载器
     * @param resourceName 资源路径
     */
    public ResourceScanner(ClassLoader classLoader, String resourceName) {
        this.classLoader = Ensure.notNull(classLoader);
        this.resourceName = Ensure.notBlank(resourceName);

        if (Logs.isDebugEnabled()) {
            Logs.debug("Resource name: {}", resourceName);
        }
    }

    public boolean hasNext() {
        if (this.inputStream != null) {
            return true;
        }

        // 初始化
        if (this.enumeration == null) {
            try {
                this.enumeration = this.classLoader.getResources(this.resourceName);
            } catch (Throwable e) {
                Logs.error(this.resourceName, e);
                return false;
            }
        }

        if (this.enumeration.hasMoreElements()) {
            try {
                URL url = this.enumeration.nextElement();

                if (Logs.isDebugEnabled()) {
                    Logs.debug("Resource name: {}, load: {}", this.resourceName, url);
                }

                this.inputStream = url.openStream();
            } catch (Throwable e) {
                Logs.error(this.resourceName, e);
                return false;
            }
        } else {
            return false;
        }

        return this.inputStream != null;
    }

    public InputStream next() {
        if (this.hasNext()) {
            InputStream inputStream = this.inputStream;
            this.inputStream = null;
            return inputStream;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
