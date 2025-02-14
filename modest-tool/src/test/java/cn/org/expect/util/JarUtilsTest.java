package cn.org.expect.util;

import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;

public class JarUtilsTest {

    @Test
    public void testGetPath() {
        String jarPath = JarUtils.getPath(Test.class);
        File jarfile = new File(jarPath);

        Assert.assertTrue(jarfile.exists() && jarfile.isFile());
        Assert.assertNull(JarUtils.getPath(StringUtils.class));

        String servletApiJarFile = JarUtils.getPath(HttpServletResponse.class);
        Assert.assertTrue(FileUtils.isFile(servletApiJarFile));
    }

    @Test
    public void testTo() {
        String jarPath = JarUtils.getPath(Servlet.class);
        File jarfile = new File(jarPath);
        Assert.assertTrue(jarfile.exists() && jarfile.isFile());
        Assert.assertEquals("javax.servlet-api-3.1.0-sources.jar", JarUtils.toSourceJar(jarfile.getAbsolutePath()).getName());
    }

    @Test
    public void testRead() throws IOException {
        String jarPath = JarUtils.getPath(Servlet.class);
        File jarfile = new File(jarPath);
        Assert.assertTrue(jarfile.exists() && jarfile.isFile());

        String license = JarUtils.read(jarfile, "META-INF/LICENSE.txt", CharsetName.UTF_8);
        Assert.assertTrue(StringUtils.isNotBlank(license));
    }
}
