package cn.org.expect.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Service Provider Interface <br>
 * 为了在 JDK5 中能够支持 SPI 机制增加的类 <br>
 * SPI 类是基础工具类，在其中不应使用 {@linkplain StringUtils} 等工具类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/11/29
 */
public class SPI<E> {

    /**
     * 加载接口对应的实现类
     *
     * @param <E>     接口或类
     * @param loader  类加载器, 不能为null
     * @param service 接口或类，不能为null
     * @return 实现类集合
     */
    public static <E> List<E> load(ClassLoader loader, Class<E> service) {
        return new SPI<E>(loader, service).load();
    }

    /** 类或接口 */
    private final Class<E> service;

    /** 类名与其实例对象的映射，按实例化顺序 */
    private final LinkedHashMap<String, E> providers;

    /** 延迟查找迭代器 */
    private final LazyIterator<E> iterator;

    public SPI(ClassLoader classLoader, Class<E> service) {
        if (classLoader == null) {
            throw new NullPointerException();
        }
        if (service == null) {
            throw new NullPointerException();
        }

        this.service = service;
        this.providers = new LinkedHashMap<String, E>();
        this.iterator = new SPI.LazyIterator<E>(service, classLoader, this.providers);
    }

    /**
     * 加在类或接口的实例对象
     *
     * @return 异常集合
     */
    public List<E> load() {
        Iterator<E> it = this.iterator();
        List<E> list = new ArrayList<E>();
        while (it.hasNext()) {
            try {
                list.add(it.next());
            } catch (Throwable e) {
                this.process(e);
            }
        }
        return list;
    }

    /**
     * 处理（创建实例发生的）异常
     *
     * @param e 异常信息
     */
    protected void process(Throwable e) {
        Logs.error(e.getLocalizedMessage(), e);
    }

    private Iterator<E> iterator() {
        return new InstanceIterator<E>(providers.entrySet().iterator(), this.iterator);
    }

    private static class InstanceIterator<E> implements Iterator<E> {
        private final Iterator<Map.Entry<String, E>> knownProviders;

        private final LazyIterator<E> iterator;

        public InstanceIterator(Iterator<Map.Entry<String, E>> knownProviders, LazyIterator<E> iterator) {
            this.knownProviders = knownProviders;
            this.iterator = iterator;
        }

        public boolean hasNext() {
            if (this.knownProviders.hasNext()) {
                return true;
            } else {
                return this.iterator.hasNext();
            }
        }

        public E next() {
            if (this.knownProviders.hasNext()) {
                return this.knownProviders.next().getValue();
            } else {
                return this.iterator.next();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 延迟加载迭代器
     */
    private static class LazyIterator<E> implements Iterator<E> {
        private final Class<E> service;

        private final ClassLoader classLoader;

        private Enumeration<URL> configs = null;

        private Iterator<String> pending = null;

        private String nextName = null;

        private final LinkedHashMap<String, E> providers;

        private LazyIterator(Class<E> service, ClassLoader classLoader, LinkedHashMap<String, E> providers) {
            this.service = service;
            this.classLoader = classLoader;
            this.providers = providers;
        }

        public boolean hasNext() {
            if (this.nextName != null) {
                return true;
            }

            // 初始化
            if (this.configs == null) {
                try {
                    String fullName = "META-INF/services/" + this.service.getName();
                    if (this.classLoader == null) {
                        this.configs = ClassLoader.getSystemResources(fullName);
                    } else {
                        this.configs = this.classLoader.getResources(fullName);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(service.getName() + ": Error locating configuration files", e);
                }
            }

            while ((this.pending == null) || !this.pending.hasNext()) {
                if (!this.configs.hasMoreElements()) {
                    return false;
                } else {
                    this.pending = this.parse(this.service, this.configs.nextElement());
                }
            }

            this.nextName = this.pending.next();
            return true;
        }

        private Iterator<String> parse(Class<?> service, URL url) throws Error {
            InputStream is = null;
            BufferedReader in = null;
            ArrayList<String> names = new ArrayList<String>();
            try {
                is = url.openStream();
                in = new BufferedReader(new InputStreamReader(is, CharsetName.UTF_8));
                for (int lineno = 1; (lineno = this.parseLine(in, lineno, names)) >= 0; ) {
                }
                return names.iterator();
            } catch (IOException x) {
                throw new RuntimeException(service.getName() + ": Error reading configuration file", x);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Logs.error(url.toString(), e);
                    }
                }

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Logs.error(url.toString(), e);
                    }
                }
            }
        }

        private int parseLine(BufferedReader in, int lineno, List<String> names) throws IOException, Error {
            String line = in.readLine();
            if (line == null) {
                return -1;
            }

            // 注释
            int index = line.indexOf('#');
            if (index >= 0) {
                line = line.substring(0, index);
            }
            line = line.trim();

            int length = line.length();
            if (length != 0) {
                if (!this.providers.containsKey(line) && !names.contains(line)) {
                    names.add(line);
                }
            }
            return lineno + 1;
        }

        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            String className = this.nextName;
            this.nextName = null;
            Class<?> type = this.loadClass(className, false, this.classLoader);
            if (this.service.isAssignableFrom(type)) {
                E obj = this.service.cast(this.newInstance(type));
                this.providers.put(className, obj);
                return obj;
            } else {
                throw new UnsupportedOperationException(service.getName() + ": " + "Provider " + className + " not a subtype");
            }
        }

        /**
         * 判断字符串参数className对应的Java类是否存在
         *
         * @param className  类名
         * @param initialize 是否初始化
         * @param loader     类加载器
         * @return 类信息
         */
        @SuppressWarnings("unchecked")
        public <E> Class<E> loadClass(String className, boolean initialize, ClassLoader loader) {
            try {
                return (Class<E>) Class.forName(className, initialize, loader);
            } catch (Throwable e) {
                throw new RuntimeException(className, e);
            }
        }

        /**
         * 生成一个类的实例对象
         *
         * @param type 类信息
         * @param <E>  类信息
         * @return 实例对象
         */
        @SuppressWarnings("unchecked")
        public <E> E newInstance(Class<?> type) {
            if (type == null) {
                throw new NullPointerException();
            }

            try {
                return (E) type.newInstance();
            } catch (Throwable e) {
                throw new IllegalArgumentException(type.getName(), e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public String toString() {
        return SPI.class.getSimpleName() + "[" + this.service.getName() + "]";
    }
}
