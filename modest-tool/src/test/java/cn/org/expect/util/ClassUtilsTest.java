package cn.org.expect.util;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import cn.org.expect.collection.ArrayDeque;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ClassUtilsTest {

    @Test
    public void test() {
        String[] array = ClassUtils.getJavaClassPath();
        for (String path : array) {
            if (FileUtils.isDirectory(path)) {
                Set<String> names = ClassUtils.findShortPackage(ClassUtils.getDefaultClassLoader(), path);
                if (names.isEmpty()) {
                    Assert.fail(path);
                }

                System.out.println(path + ", short package Name: " + names);
                for (String name : names) {
                    Assert.assertTrue(name.startsWith(cn.org.Test.class.getPackage().getName()));
                }
            }
        }
    }

    @Test
    public void testInArray() {
        assertFalse(ClassUtils.inArray(null, String.class, Integer.class));
        assertTrue(ClassUtils.inArray(null, String.class, Integer.class, null));
        assertTrue(ClassUtils.inArray(String.class, String.class, Integer.class, null));
        assertFalse(ClassUtils.inArray(String.class, Integer.class, null));
    }

    @Test
    public void testgetPackageName() {
        String str = ClassUtils.class.getName();
        int b1 = str.indexOf('.', 0);
        int b2 = str.indexOf('.', b1 + 1);

        assertEquals(str.substring(0, b2), ClassUtils.getPackageName(ClassUtils.class, 2));
    }

    @Test
    public void testgetClasspath() {
        String classpath = ClassUtils.getClasspath(ClassUtilsTest.class);
        Assert.assertNotNull(classpath);

        File dir = new File(classpath);
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(dir.isDirectory());
    }

    @Test
    public void testcontainsMethod() {
        assertTrue(ClassUtils.containsMethod(String.class, "toString"));
        assertTrue(ClassUtils.containsMethod(String.class, "split", String.class));
        assertTrue(ClassUtils.containsMethod(String.class, "replaceFirst", String.class, String.class));
    }

    @Test
    public void testGetClasspath() {
        String path = ClassUtils.getClasspath(ClassUtils.class);
        Assert.assertTrue(StringUtils.isNotBlank(path) && FileUtils.isDirectory(path));
        Assert.assertTrue(FileUtils.isDirectory(ClassUtils.getClasspath(StringUtils.class)));
        Assert.assertTrue(FileUtils.isDirectory(ClassUtils.getClasspath(ClassUtils.class)));
    }

    @Test
    public void testgetPackageNameString() {
        Class<?> cls = ClassUtils.class;
        String str = cls.getName();
        int end = str.lastIndexOf('.');
        String classpath = str.substring(0, end);
        assertEquals(classpath, cls.getPackage().getName());

        int n = 0;
        for (int i = 0; i < classpath.length(); i++) {
            char c = classpath.charAt(i);
            if (c == '.') {
                n++;
            }
        }
        assertEquals(classpath, ClassUtils.getPackageName(cls, ++n));
    }

    @Test
    public void testgetPackageNameString1() {
        assertEquals(null, ClassUtils.getPackageName((String) null, 1));
        assertEquals("", ClassUtils.getPackageName("", 0));
        assertEquals("", ClassUtils.getPackageName("", 1));
        assertEquals("a", ClassUtils.getPackageName("a", 0));
        assertEquals("a", ClassUtils.getPackageName("a", 1));
        assertEquals("a", ClassUtils.getPackageName("a", 2));
        assertEquals("a", ClassUtils.getPackageName("a", -1));
        assertEquals("a", ClassUtils.getPackageName("a", -2));

        assertEquals("a", ClassUtils.getPackageName("a.b", 1));
        assertEquals("a.b", ClassUtils.getPackageName("a.b", 0));
        assertEquals("b", ClassUtils.getPackageName("a.b", -1));

        assertEquals("a.b", ClassUtils.getPackageName("a.b", 2));
        assertEquals("a.b", ClassUtils.getPackageName("a.b", -2));

        assertEquals("a.b", ClassUtils.getPackageName("a.b.c", 2));
        assertEquals("b.c", ClassUtils.getPackageName("a.b.c", -2));
    }

    @Test
    public void testforName() {
        assertNotNull(ClassUtils.forName(String.class.getName()));
        assertNotNull(ClassUtils.forName(String.class.getName(), false, null));
        assertNotNull(ClassUtils.forName(String.class.getName(), true, null));
    }

    @Test
    public void testgetDefaultClassLoader() {
        assertNotNull(ClassUtils.getDefaultClassLoader());
    }

    @Test
    public void testgetJarPath() {
        String jarPath = ClassUtils.getJarPath(Test.class);
        File jarfile = new File(jarPath);
        assertTrue(jarfile.exists() && jarfile.isFile());
        assertNull(ClassUtils.getJarPath(StringUtils.class));
    }

    @Test
    public void testGetJvmJavaClassPath() {
        String[] paths = ClassUtils.getJavaClassPath();
        assertFalse(StringUtils.isBlank(paths));

        System.out.println("classpath: ");
        for (String s : paths) {
            System.out.println(s);
        }
    }

    @Test
    public void testasClassname() {
        Class<?>[] array = {String.class, Integer.class, StringUtils.class};
        List<String> nameList = ClassUtils.asNameList(array);
        assertEquals(String.class.getName(), nameList.get(0));
        assertEquals(Integer.class.getName(), nameList.get(1));
        assertEquals(StringUtils.class.getName(), nameList.get(2));
    }

    @Test
    public void testinst() {
        try {
            Object o = ClassUtils.newInstance(Object.class.getName(), null);
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            Object o = ClassUtils.newInstance(Object.class);
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testloadClass() {
        try {
            ClassUtils.loadClass(String.class.getName());
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            ClassUtils.loadClass("Testlkjsadfljaslkdjf" + String.class.getName());
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testgetAllInterface() {
        Class<?> cls = ArrayDeque.class;
        List<Class<?>> list = ClassUtils.getAllInterface(cls, new InterfaceFilter() {
            public boolean accept(Class<?> cls, String name) {
                return !name.startsWith("java.");
            }
        });
        Assert.assertTrue(list.isEmpty());

        list = ClassUtils.getAllInterface(cls, null);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(Cloneable.class, list.get(0));
        Assert.assertEquals(Serializable.class, list.get(1));
        Assert.assertEquals(Collection.class, list.get(2));
        Assert.assertEquals(Iterable.class, list.get(3));
    }

    @Test
    public void testgetInterfaceGenerics() {
        String[] array = ClassUtils.getInterfaceGenerics(GenericTest.class, G2.class);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(String.class.getName(), array[0]);
        Assert.assertEquals(Long.class.getName(), array[1]);
        Assert.assertEquals(Integer.class.getName(), array[2]);

        String[] array1 = ClassUtils.getInterfaceGenerics(GenericSample.class, List.class);
        Assert.assertEquals(1, array1.length);
        Assert.assertEquals(String.class.getName(), array1[0]);
    }

    interface G1<E> {
    }

    interface G2<E, F, G> {
    }

    enum Testenum {
        A1, A2
    }

    abstract class GenericTest implements G1<String>, G2<String, Long, Integer> {
    }

    class GenericABCTest extends GenericTest {
    }

    @Test
    public void testisAssignableFrom() {
        Assert.assertTrue(G1.class.isAssignableFrom(GenericTest.class));
        Assert.assertTrue(G2.class.isAssignableFrom(GenericTest.class));
        Assert.assertTrue(G2.class.isAssignableFrom(GenericABCTest.class));
        Assert.assertTrue(G1.class.isAssignableFrom(GenericABCTest.class));
        Assert.assertTrue(GenericTest.class.isAssignableFrom(GenericABCTest.class));

        Assert.assertTrue(GenericTest.class.isAssignableFrom(GenericTest.class)); // 判断相同类
        Assert.assertTrue(G2.class.isAssignableFrom(G2.class)); // 判断相同类
        Assert.assertTrue(GenericTest.class.isAssignableFrom(GenericTest.class)); // 判断相同类

        Assert.assertTrue(Modifier.isAbstract(GenericTest.class.getModifiers()));
        Assert.assertTrue(Modifier.isAbstract(G2.class.getModifiers()));
        Assert.assertFalse(Modifier.isAbstract(Testenum.class.getModifiers()));
        Assert.assertFalse(Modifier.isAbstract(Testenum.A1.getClass().getModifiers()));
        Assert.assertFalse(Modifier.isAbstract(GenericABCTest.class.getModifiers()));
    }

//    @Test
//    public void test() throws NamingException { TODO 需要测试
//        String key = "java:comp/env/testName1";
//        String value = "test";
//
//        Context cxt = new InitialContext();
//        cxt.bind(key, value);
//
//        Assert.assertEquals(value, ClassUtils.lookup(key));
//    }

}
