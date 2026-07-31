package cn.org.expect.util;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import cn.org.expect.collection.ArrayDeque;
import org.junit.Assert;
import org.junit.Test;

public class ClassUtilsTest {

    @Test
    public void test() {
        String[] array = ClassUtils.getClassPath();
        for (String path : array) {
            if (FileUtils.isDirectory(path)) {
                Set<String> names = ClassUtils.findShortPackage(ClassUtils.getClassLoader(), path);
                if (names.isEmpty()) {
                    Assert.fail(path);
                }

                for (String name : names) {
                    Assert.assertTrue(name.startsWith(cn.org.Test.class.getPackage().getName()));
                }
            }
        }
    }

    @Test
    public void testInArray() {
        Assert.assertFalse(ClassUtils.inArray(null, String.class, Integer.class));
        Assert.assertTrue(ClassUtils.inArray(null, String.class, Integer.class, null));
        Assert.assertTrue(ClassUtils.inArray(String.class, String.class, Integer.class, null));
        Assert.assertFalse(ClassUtils.inArray(String.class, Integer.class, null));
    }

    @Test
    public void testGetPackageName() {
        String str = ClassUtils.class.getName();
        int b1 = str.indexOf('.', 0);
        int b2 = str.indexOf('.', b1 + 1);

        Assert.assertEquals(str.substring(0, b2), ClassUtils.getPackageName(ClassUtils.class, 2));
    }

    @Test
    public void testContainsMethod() {
        Assert.assertTrue(ClassUtils.containsMethod(String.class, "toString"));
        Assert.assertTrue(ClassUtils.containsMethod(String.class, "split", String.class));
        Assert.assertTrue(ClassUtils.containsMethod(String.class, "replaceFirst", String.class, String.class));
    }

    @Test
    public void testGetPackageNameString() {
        Class<?> cls = ClassUtils.class;
        String str = cls.getName();
        int end = str.lastIndexOf('.');
        String classpath = str.substring(0, end);
        Assert.assertEquals(classpath, cls.getPackage().getName());

        int n = 0;
        for (int i = 0; i < classpath.length(); i++) {
            char c = classpath.charAt(i);
            if (c == '.') {
                n++;
            }
        }
        Assert.assertEquals(classpath, ClassUtils.getPackageName(cls, ++n));
    }

    @Test
    public void testGetPackageNameString1() {
        Assert.assertNull(ClassUtils.getPackageName((String) null, 1));
        Assert.assertEquals("", ClassUtils.getPackageName("", 0));
        Assert.assertEquals("", ClassUtils.getPackageName("", 1));
        Assert.assertEquals("a", ClassUtils.getPackageName("a", 0));
        Assert.assertEquals("a", ClassUtils.getPackageName("a", 1));
        Assert.assertEquals("a", ClassUtils.getPackageName("a", 2));
        Assert.assertEquals("a", ClassUtils.getPackageName("a", -1));
        Assert.assertEquals("a", ClassUtils.getPackageName("a", -2));

        Assert.assertEquals("a", ClassUtils.getPackageName("a.b", 1));
        Assert.assertEquals("a.b", ClassUtils.getPackageName("a.b", 0));
        Assert.assertEquals("b", ClassUtils.getPackageName("a.b", -1));

        Assert.assertEquals("a.b", ClassUtils.getPackageName("a.b", 2));
        Assert.assertEquals("a.b", ClassUtils.getPackageName("a.b", -2));

        Assert.assertEquals("a.b", ClassUtils.getPackageName("a.b.c", 2));
        Assert.assertEquals("b.c", ClassUtils.getPackageName("a.b.c", -2));
    }

    @Test
    public void testForName() {
        Assert.assertNotNull(ClassUtils.forName(String.class.getName()));
        Assert.assertNotNull(ClassUtils.forName(String.class.getName(), false, null));
        Assert.assertNotNull(ClassUtils.forName(String.class.getName(), true, null));
    }

    @Test
    public void testGetClassLoader() {
        Assert.assertNotNull(ClassUtils.getClassLoader());
    }

    @Test
    public void testGetJvmJavaClassPath() {
        String[] paths = ClassUtils.getClassPath();
        Assert.assertFalse(StringUtils.isBlank(paths));
        for (String filepath : paths) {
            if (!FileUtils.isFile(filepath) && !FileUtils.isDirectory(filepath)) {
                Assert.fail(filepath);
            }
        }
    }

    @Test
    public void testasClassname() {
        Class<?>[] array = {String.class, Integer.class, StringUtils.class};
        List<String> nameList = ClassUtils.asNameList(array);
        Assert.assertEquals(String.class.getName(), nameList.get(0));
        Assert.assertEquals(Integer.class.getName(), nameList.get(1));
        Assert.assertEquals(StringUtils.class.getName(), nameList.get(2));
    }

    @Test
    public void testNewInstance() {
        try {
            ClassUtils.newInstance(Object.class.getName(), null);
        } catch (Exception e) {
            Logs.error(e.getLocalizedMessage(), e);
            Assert.fail();
        }

        try {
            ClassUtils.newInstance(Object.class);
        } catch (Exception e) {
            Logs.error(e.getLocalizedMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void testLoadClass() {
        try {
            ClassUtils.loadClass(String.class.getName());
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            ClassUtils.loadClass("Testlkjsadfljaslkdjf" + String.class.getName());
            Assert.fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testgetAllInterface() {
        Class<?> cls = ArrayDeque.class;
        List<Class<?>> list = ClassUtils.getAllInterface(cls, new ClassUtils.Filter() {
            public boolean accept(Class<?> cls, String name) {
                return !name.startsWith("java.");
            }
        });
        Assert.assertTrue(list.isEmpty());

        list = ClassUtils.getAllInterface(cls);
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

        String[] array2 = ClassUtils.getInterfaceGenerics(ArrayGenericTest1.class, G1.class);
        Assert.assertEquals(1, array2.length);

        String[] array3 = ClassUtils.getInterfaceGenerics(ArrayGenericTest2.class, G1.class);
        Assert.assertEquals(1, array3.length);
    }

    @Test
    public void testIsGenericArray() {
        Assert.assertFalse(ClassUtils.isGenericArray(null));
        Assert.assertFalse(ClassUtils.isGenericArray(""));
        Assert.assertFalse(ClassUtils.isGenericArray("1"));
        Assert.assertFalse(ClassUtils.isGenericArray("["));
        Assert.assertFalse(ClassUtils.isGenericArray("[;"));
        Assert.assertFalse(ClassUtils.isGenericArray("[L;"));
        Assert.assertTrue(ClassUtils.isGenericArray("[Ljava.lang.String;"));
        Assert.assertTrue(ClassUtils.isGenericArray("[I"));
        Assert.assertTrue(ClassUtils.isGenericArray("[D"));
    }

    @Test
    public void testgetGenericArray() {
        Assert.assertEquals("java.lang.String", ClassUtils.getGenericArray("[Ljava.lang.String;"));
        Assert.assertEquals("int", ClassUtils.getGenericArray("[I"));
        Assert.assertEquals("b", ClassUtils.getGenericArray("[Lb;"));
        Assert.assertEquals("double", ClassUtils.getGenericArray("[D"));
        Assert.assertNull(ClassUtils.getGenericArray(null));
        Assert.assertNull(ClassUtils.getGenericArray(""));
        Assert.assertNull(ClassUtils.getGenericArray("1"));
        Assert.assertNull(ClassUtils.getGenericArray("["));
        Assert.assertNull(ClassUtils.getGenericArray("[;"));
        Assert.assertNull(ClassUtils.getGenericArray("[L;"));
        Assert.assertNotNull(ClassUtils.getGenericArray("[La;"));
    }

    @Test
    public void testisAssignableFromFrom() {
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

    @Test
    public void testSubArray() {
        Assert.assertNull(ClassUtils.subarray(null, 0, 0));
        Assert.assertArrayEquals(ClassUtils.subarray(new Class<?>[]{}, 0, 0), new Class<?>[0]);

        Class<?>[] strarray = {null, String.class, int.class, double.class, Integer.class, BigDecimal.class};
        Assert.assertArrayEquals(ClassUtils.subarray(strarray, 0, 0), new Class<?>[0]);
        Assert.assertArrayEquals(ClassUtils.subarray(strarray, 0, 1), new Class<?>[]{(Class<?>) null});
        Assert.assertArrayEquals(ClassUtils.subarray(strarray, 0, 2), new Class<?>[]{null, String.class});
        Assert.assertArrayEquals(ClassUtils.subarray(strarray, 0, 3), new Class<?>[]{null, String.class, int.class});
        Assert.assertArrayEquals(ClassUtils.subarray(strarray, 0, 4), new Class<?>[]{null, String.class, int.class, double.class});
        Assert.assertArrayEquals(ClassUtils.subarray(strarray, 0, 5), new Class<?>[]{null, String.class, int.class, double.class, Integer.class});

        Class<?>[] array = ClassUtils.subarray(new Class<?>[]{String.class, int.class, double.class}, 0, 1);
        Assert.assertTrue(array.length == 1 && array[0].equals(String.class));
    }

    enum Testenum {
        A1, A2
    }

    interface G1<E> {
    }

    interface G2<E, F, G> {
    }

    abstract static class GenericTest implements G1<String>, G2<String, Long, Integer> {
    }

    static class GenericABCTest extends GenericTest {
    }

    static class ArrayGenericTest1 implements G1<String[]> {
    }

    static class ArrayGenericTest2 implements G1<int[]> {
    }
}
