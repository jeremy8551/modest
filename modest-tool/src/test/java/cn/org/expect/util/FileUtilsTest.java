package cn.org.expect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

    @Test
    public void testcreateTempDirectory() throws IOException {
        // 清空临时文件
        FileUtils.clearDirectory(FileUtils.getTempDir());

        Logs.info(FileUtils.createTempDirectory(null).getAbsolutePath());
        Logs.info(FileUtils.createTempDirectory("").getAbsolutePath());

        File dir1 = FileUtils.createTempDirectory("1");
        Assertions.assertEquals("1", dir1.getName());
        Logs.info(dir1.getAbsolutePath());

        File dir2 = FileUtils.createTempDirectory("a");
        Assertions.assertEquals("a", dir2.getName());
        Logs.info(dir2.getAbsolutePath());

        File dir3 = FileUtils.createTempDirectory("abc");
        Assertions.assertEquals("abc", dir3.getName());
        Logs.info(dir3.getAbsolutePath());
    }

    @Test
    public void testfindFile() throws IOException {
        File root = FileUtils.getTempDir("test", FileUtilsTest.class.getSimpleName());
        File d0 = new File(root, "findfile");
        d0.mkdirs();

        File d1 = new File(d0, "d1");
        d1.mkdirs();

        File f11 = new File(d1, "20200102.txt");
        f11.createNewFile();

        File d2 = new File(d0, "d2");
        d2.mkdirs();

        File f21 = new File(d2, "20200102.txt");
        f21.createNewFile();

        File d3 = new File(d2, "d3");
        d3.mkdirs();

        File f22 = new File(d3, "20200102.txt");
        f22.createNewFile();

        List<File> fs = FileUtils.find(d0, "20200102.txt");
        Assertions.assertEquals(3, fs.size());
        Assertions.assertTrue(fs.indexOf(f11) != -1);
        Assertions.assertTrue(fs.indexOf(f21) != -1);
        Assertions.assertTrue(fs.indexOf(f22) != -1);

        fs = FileUtils.find(d0, "20200102[^ ]{4}");
        Assertions.assertEquals(3, fs.size());
        Assertions.assertTrue(fs.indexOf(f11) != -1);
        Assertions.assertTrue(fs.indexOf(f21) != -1);
        Assertions.assertTrue(fs.indexOf(f22) != -1);
    }

    @Test
    public void testReplaceLinSeparator() {
        Assertions.assertEquals("", FileUtils.replaceLineSeparator("", ":"));
        Assertions.assertNull(FileUtils.replaceLineSeparator(null, ":"));

        Assertions.assertEquals("1", FileUtils.replaceLineSeparator("1", ":"));
        Assertions.assertEquals("1:22:3:44:", FileUtils.replaceLineSeparator("1\r22\n3\r\n44\r\n", ":"));
        Assertions.assertEquals("1:22:3:44", FileUtils.replaceLineSeparator("1\r22\n3\r\n44", ":"));
        Assertions.assertEquals("1:22:3:44", FileUtils.replaceLineSeparator("1\r22\n3\r\n44", ":"));
        Assertions.assertEquals(("1" + Settings.getLineSeparator() + "22" + Settings.getLineSeparator() + "3" + Settings.getLineSeparator() + "44"), FileUtils.replaceLineSeparator("1\r22\n3\r\n44"));
    }

    @Test
    public void test0111() {
        Assertions.assertNull(FileUtils.getParent(null));
        Assertions.assertNull(FileUtils.getParent(""));
        Assertions.assertNull(FileUtils.getParent("/"));
        Assertions.assertNull(FileUtils.getParent("/1"));
        Assertions.assertEquals("/1", FileUtils.getParent("/1/2"));
        Assertions.assertEquals("/1", FileUtils.getParent("/1/2/"));
        Assertions.assertEquals("/1/2", FileUtils.getParent("/1/2/3.txt"));
    }

    @Test
    public void testdos2unix() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "1\r\n2\r\n3\r\n"));

        String nt = FileUtils.readline(file, CharsetUtils.get(), 0);
        Assertions.assertEquals("1\\r\\n2\\r\\n3\\r\\n", StringUtils.escapeLineSeparator(nt));
        Assertions.assertTrue(nt.indexOf(FileUtils.LINE_SEPARATOR_WINDOWS) != -1);
        Assertions.assertTrue(FileUtils.dos2unix(file, CharsetUtils.get(), null));

        String text = FileUtils.readline(file, CharsetUtils.get(), 0);
        Assertions.assertEquals(-1, text.indexOf(FileUtils.LINE_SEPARATOR_WINDOWS));
    }

    @Test
    public void testExists() throws IOException {
        File file = FileUtils.createTempDirectory(null);
        Logs.info(file.getAbsolutePath());

        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertFalse(FileUtils.exists(file.getAbsolutePath()));

        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(FileUtils.exists(file.getAbsolutePath()));
    }

    @Test
    public void testCleanFileFile() throws IOException {
        File file = FileUtils.createTempFile(null);
        FileUtils.write(file, CharsetUtils.get(), false, "测试内容是否删除");
        Assertions.assertTrue(FileUtils.clearFile(file));
        Assertions.assertEquals(0, file.length());
    }

    @Test
    public void testCleanFileString() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "测试内容是否"));
        Assertions.assertTrue(FileUtils.clearFile(file) && file.length() == 0);
    }

    @Test
    public void testIsFileFile() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createFile(file) && FileUtils.isFile(file));
    }

    @Test
    public void testIsFileString() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(FileUtils.isFile(file.getAbsolutePath()));

        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(file.mkdirs());
        Assertions.assertFalse(FileUtils.isFile(file.getAbsolutePath()));
    }

    @Test
    public void testIsDirString() throws IOException {
        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));

        Assertions.assertTrue(FileUtils.createDirectory(file));
        Assertions.assertTrue(FileUtils.isDirectory(file.getAbsolutePath()));

        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertFalse(FileUtils.isDirectory(file.getAbsolutePath()));
    }

    @Test
    public void testGetFilename() {
        Assertions.assertEquals("", FileUtils.getFilename(""));
        Assertions.assertEquals("test", FileUtils.getFilename("/home/test/test"));
        Assertions.assertEquals("test.", FileUtils.getFilename("/home/test/test."));
        Assertions.assertEquals("test.txt", FileUtils.getFilename("/home/test/test.txt"));
        Assertions.assertEquals("test", FileUtils.getFilename("/home/test./test"));
        Assertions.assertEquals("test", FileUtils.getFilename("/home/.test\\test"));
        Assertions.assertEquals("test.txt", FileUtils.getFilename("/home/.test\\test.txt"));
        Assertions.assertEquals(".txt", FileUtils.getFilename("/home/.test\\.txt"));
        Assertions.assertEquals(".txt", FileUtils.getFilename("/home/.test/.txt"));
    }

    @Test
    public void testGetFilenameNoExt() {
        Assertions.assertEquals("test", FileUtils.getFilenameNoExt("/home/test/shell/test.txt"));
        Assertions.assertEquals("test", FileUtils.getFilenameNoExt("/home/test/shell/test."));
        Assertions.assertEquals("test", FileUtils.getFilenameNoExt("/home/test/shell/test"));
        Assertions.assertEquals("test", FileUtils.getFilenameNoExt("shell/test"));
        Assertions.assertEquals("test", FileUtils.getFilenameNoExt("test"));
        Assertions.assertEquals("t", FileUtils.getFilenameNoExt("t"));
        Assertions.assertEquals("", FileUtils.getFilenameNoExt(""));
        Assertions.assertNull(FileUtils.getFilenameNoExt(null));
    }

    @Test
    public void testGetFilenameNoSuffix() {
        Assertions.assertNull(FileUtils.getFilenameNoSuffix(null));
        Assertions.assertEquals("1", FileUtils.getFilenameNoSuffix("1.del"));
        Assertions.assertEquals("", FileUtils.getFilenameNoSuffix("."));
        Assertions.assertEquals("", FileUtils.getFilenameNoSuffix(".del.gz"));
        Assertions.assertEquals("INC_QYZX_ECC_LACKOFINTERESTS18", FileUtils.getFilenameNoSuffix("INC_QYZX_ECC_LACKOFINTERESTS18.del.gz"));
        Assertions.assertEquals("INC_QYZX_ECC_LACKOFINTERESTS18", FileUtils.getFilenameNoSuffix("D:\\home\\test\\INC_QYZX_ECC_LACKOFINTERESTS18.del.gz"));
        Assertions.assertEquals("INC_QYZX_ECC_LACKOFINTERESTS18", FileUtils.getFilenameNoSuffix("D:\\home\\test\\INC_QYZX_ECC_LACKOFINTERESTS18.del"));
    }

    @Test
    public void testGetFilenameExt() {
        Assertions.assertEquals("", FileUtils.getFilenameExt(""));
        Assertions.assertEquals("", FileUtils.getFilenameExt("/home/test/test"));
        Assertions.assertEquals("", FileUtils.getFilenameExt("/home/test/test."));
        Assertions.assertEquals("txt", FileUtils.getFilenameExt("/home/test/test.txt"));
        Assertions.assertEquals("", FileUtils.getFilenameExt("/home/test./test"));
        Assertions.assertEquals("", FileUtils.getFilenameExt("/home/.test\\test"));
        Assertions.assertEquals("txt", FileUtils.getFilenameExt("/home/.test\\test.txt"));
        Assertions.assertEquals("txt", FileUtils.getFilenameExt("/home/.test\\.txt"));
        Assertions.assertEquals("txt", FileUtils.getFilenameExt("/home/.test/.txt"));
    }

    @Test
    public void testGetFilenameSuffix() {
        Assertions.assertNull(FileUtils.getFilenameSuffix(null));
        Assertions.assertEquals("", FileUtils.getFilenameSuffix(""));
        Assertions.assertEquals("", FileUtils.getFilenameSuffix("1"));
        Assertions.assertEquals("", FileUtils.getFilenameSuffix("1."));
        Assertions.assertEquals("d", FileUtils.getFilenameSuffix("1.d"));
        Assertions.assertEquals("del", FileUtils.getFilenameSuffix("1.del"));
        Assertions.assertEquals("del.gz", FileUtils.getFilenameSuffix("1.del.gz"));
        Assertions.assertEquals("del.gz", FileUtils.getFilenameSuffix("\\1.del.gz"));
        Assertions.assertEquals("del.gz", FileUtils.getFilenameSuffix("/1.del.gz"));
        Assertions.assertEquals("del.gz", FileUtils.getFilenameSuffix("1/1.del.gz"));
    }

    @Test
    public void testStripFilenameExt() {
        Assertions.assertNull(FileUtils.removeFilenameExt(null));
        Assertions.assertEquals("", FileUtils.removeFilenameExt(""));
        Assertions.assertEquals("/home/test/", FileUtils.removeFilenameExt("/home/test/"));
        Assertions.assertEquals("/home/test/test", FileUtils.removeFilenameExt("/home/test/test.txt"));
        Assertions.assertEquals("/home/test/test", FileUtils.removeFilenameExt("/home/test/test"));
        Assertions.assertEquals("/home/.test/test", FileUtils.removeFilenameExt("/home/.test/test"));
        Assertions.assertEquals("/home/test/test", FileUtils.removeFilenameExt("/home/test/test."));
        Assertions.assertEquals("/home/test/", FileUtils.removeFilenameExt("/home/test/.test"));

        Assertions.assertEquals("/home/test\\", FileUtils.removeFilenameExt("/home/test\\"));
        Assertions.assertEquals("/home/test\\test", FileUtils.removeFilenameExt("/home/test\\test.txt"));
        Assertions.assertEquals("/home/test\\test", FileUtils.removeFilenameExt("/home/test\\test"));
        Assertions.assertEquals("/home/.test\\test", FileUtils.removeFilenameExt("/home/.test\\test"));
        Assertions.assertEquals("/home/test\\test", FileUtils.removeFilenameExt("/home/test\\test."));
        Assertions.assertEquals("/home/test\\", FileUtils.removeFilenameExt("/home/test\\.test"));
    }

    @Test
    public void testGetNoRepeatFilename() throws IOException {
        File tempDir = FileUtils.createTempDirectory(null);
        File parent = new File(tempDir, Dates.format08(new Date()));
        Assertions.assertTrue(FileUtils.createDirectory(parent));

        // 先创建一个文件
        File file = new File(parent, "test_repeat_file.dat.tmp");
        Assertions.assertTrue(FileUtils.createFile(file));

        // 再创建一个不重名文件
        File newfile = FileUtils.allocate(parent, "test_repeat_file.dat.tmp");
        Assertions.assertNotEquals(newfile, file);
        Assertions.assertTrue(FileUtils.createFile(newfile));

        // 再创建一个不重名文件
        File newfile1 = FileUtils.allocate(parent, "test_repeat_file.dat.tmp");
        Assertions.assertNotEquals(newfile1, file);
        Assertions.assertNotEquals(newfile1, newfile);
        Assertions.assertTrue(FileUtils.createFile(newfile1));
    }

    @Test
    public void testcreateUniqueFile() {
        File tempDir = FileUtils.createTempDirectory(null);
        File newfile = FileUtils.allocate(tempDir, "test_repeat_file.dat");
        Assertions.assertTrue(FileUtils.createFile(newfile));
    }

    @Test
    public void testGetResourceAsStream() throws IOException {
        Assertions.assertNotNull(FileUtils.loadProperties("/testfile.properties"));
        Assertions.assertNotNull(FileUtils.loadProperties("testfile.properties"));
        Assertions.assertNotNull(FileUtils.loadProperties("classpath:testfile.properties"));
        Assertions.assertNotNull(FileUtils.loadProperties("classpath:/testfile.properties"));
    }

    @Test
    public void testCreateFileString() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertFalse(file.exists());

        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(file.exists());
    }

    @Test
    public void testCreateFileFile() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertFalse(file.exists());

        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(file.exists());
    }

    @Test
    public void testCreatefileFile() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createFile(file) && file.exists());
    }

    @Test
    public void testCreatefileString() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createFile(file) && file.exists());
    }

    @Test
    public void testCreatefileFileBoolean() throws IOException {
        Assertions.assertFalse(FileUtils.createFile(null, true));

        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createDirectory(file));
        Assertions.assertTrue(FileUtils.createFile(file, true)); // 测试强制新建文件
    }

    @Test
    public void testCreatefileFileBoolean1() throws IOException {
        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));

        String path = FileUtils.joinPath(file.getAbsolutePath(), "a", "b", "c");
        File dir = new File(path);
        Assertions.assertTrue(FileUtils.createDirectory(dir));
        Assertions.assertTrue(FileUtils.isDirectory(dir));
    }

    @Test
    public void testCreateDirecotryString() {
        File file = FileUtils.createTempDirectory(null);
        FileUtils.delete(file);
        Assertions.assertTrue(FileUtils.createDirectory(file));
        Assertions.assertTrue(file.exists() && file.isDirectory());
    }

    @Test
    public void testCreateDirecotryFile() {
        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createDirectory(file));
        Assertions.assertTrue(file.exists() && file.isDirectory());
    }

    @Test
    public void testCreateDirectoryString() {
        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createDirectory(file) && file.exists() && file.isDirectory());
    }

    @Test
    public void testCreateDirectoryFile() {
        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createDirectory(file) && file.exists() && file.isDirectory());
    }

    @Test
    public void testCreateDirectoryFileBoolean() throws IOException {
        File file = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(FileUtils.createDirectory(file, true) && file.exists() && file.isDirectory());

        File file1 = FileUtils.getTempDir("test", FileUtilsTest.class.getSimpleName(), "dirdir0000");
        Assertions.assertTrue(FileUtils.delete(file1));
        Assertions.assertTrue(file1.mkdirs());

        File f0 = new File(file1, FileUtils.joinPath("t1", "t2", "t3"));
        Assertions.assertTrue(FileUtils.createDirectory(f0) && f0.exists() && f0.isDirectory());
        Logs.info("f0: " + f0.getAbsolutePath());
    }

    @Test
    public void testTranslateSeperator() {
        String str1 = "/home/test/shell/qyzx/";
        String str2 = StringUtils.replaceAll(str1, "/", File.separator);
        Assertions.assertEquals(FileUtils.replaceFolderSeparator(str1), str2);
    }

    @Test
    public void testSpellFileStringString() {
        Assertions.assertEquals(FileUtils.joinPath("/home/test", "shell"), "/home/test" + File.separator + "shell");
        Assertions.assertEquals(FileUtils.joinPath("/home/test", "shell/qyzx"), "/home/test" + File.separator + "shell/qyzx");
    }

    @Test
    public void testSpellFileStringArray() {
        Assertions.assertEquals(FileUtils.joinPath(new String[]{"home", "test", "shell", "grzx"}), "home" + File.separator + "test" + File.separator + "shell" + File.separator + "grzx");
    }

    @Test
    public void testJoinPath() {
        Assertions.assertEquals(FileUtils.joinPath(File.separator + "home" + File.separator + "User", FileUtilsTest.class), "home" + File.separator + "User" + File.separator + "cn" + File.separator + "org" + File.separator + "expect" + File.separator + "util" + File.separator);
    }

    @Test
    public void testRemoveEndFileSeparator() {
        Assertions.assertEquals("\\home\\test\\shell\\mulu", FileUtils.rtrimFolderSeparator("\\home\\test\\shell\\mulu\\"));
        Assertions.assertEquals("\\home\\test\\shell\\mulu", FileUtils.rtrimFolderSeparator("\\home\\test\\shell\\mulu\\/"));
    }

    @Test
    public void testChangeFilenameExt() {
        Assertions.assertEquals("C:/test/ceshi/test.enc", FileUtils.changeFilenameExt("C:/test/ceshi/test.txt", "enc"));
        Assertions.assertEquals("C:/test/.ceshi/test.enc", FileUtils.changeFilenameExt("C:/test/.ceshi/test.txt", "enc"));
        Assertions.assertEquals("C:\\.test\\.ceshi\\test.enc", FileUtils.changeFilenameExt("C:\\.test\\.ceshi\\test.txt", "enc"));
        Assertions.assertEquals("C:/test/.ceshi/.test.enc", FileUtils.changeFilenameExt("C:/test/.ceshi/.test.txt", "enc"));
        Assertions.assertEquals("C:/test/.ceshi/.enc", FileUtils.changeFilenameExt("C:/test/.ceshi/.test", "enc"));
        Assertions.assertEquals("C:/test/.ceshi/test.enc", FileUtils.changeFilenameExt("C:/test/.ceshi/test", "enc"));
    }

    @Test
    public void testchangeFilename() {
        Assertions.assertEquals("", FileUtils.changeFilename("", ""));
        Assertions.assertEquals("test1", FileUtils.changeFilename("", "test1"));
        Assertions.assertEquals("", FileUtils.changeFilename("", ""));
        Assertions.assertEquals("C:/test/ceshi/test1.enc", FileUtils.changeFilename("C:/test/ceshi/test.enc", "test1"));
        Assertions.assertEquals("C:/test/ceshi/test1.txt.enc", FileUtils.changeFilename("C:/test/ceshi/test.enc", "test1.txt"));
        Assertions.assertEquals("/test1.txt.enc", FileUtils.changeFilename("/test.enc", "test1.txt"));
        Assertions.assertEquals("/", FileUtils.changeFilename("/", ""));
        Assertions.assertEquals("//test1.txt.enc", FileUtils.changeFilename("//test.enc", "test1.txt"));
        Assertions.assertEquals("/home/user/sample/test1.txt.enc", FileUtils.changeFilename("/home/user/sample/test.enc", "test1.txt"));
        Assertions.assertEquals("\\home\\user\\sample\\test1.txt.enc", FileUtils.changeFilename("\\home\\user\\sample\\test.enc", "test1.txt"));
    }

    @Test
    public void testLoadPropertiesFile() throws IOException {
        File file = FileUtils.createTempFile("a.properties");
        FileOutputStream out = new FileOutputStream(file);
        Properties p = new Properties();
        p.put("path", "/home/user/shell/test/execute.properties");
        p.store(out, "测试");

        Properties np = FileUtils.loadProperties(file.getAbsolutePath());
        Assertions.assertEquals("/home/user/shell/test/execute.properties", np.getProperty("path"));
    }

    @Test
    public void testWriteProperties() throws IOException {
        Properties p = new Properties();
        p.put("path", "/home/user/shell/test/config.properties");

        File file = FileUtils.createTempFile(".properties");
        Logs.info(file.getAbsolutePath());
        Assertions.assertTrue(FileUtils.store(p, file));

        Properties nc = FileUtils.loadProperties(file.getAbsolutePath());
        Assertions.assertEquals("/home/user/shell/test/config.properties", nc.getProperty("path"));
    }

    @Test
    public void testDelete() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertFalse(file.exists());

        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertTrue(FileUtils.createDirectory(file));
        Assertions.assertTrue(FileUtils.delete(file));
        Assertions.assertFalse(file.exists());
    }

    @Test
    public void testDeleteFileFile() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        FileUtils.createFile(f0);
        Assertions.assertTrue(FileUtils.deleteFile(f0) && !f0.exists());

        File f1 = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createDirectory(f1));
        Assertions.assertTrue(!FileUtils.deleteFile(f1) && f1.exists());
    }

    @Test
    public void testDeleteFileString() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        FileUtils.createFile(f0);
        Assertions.assertTrue(FileUtils.deleteFile(new File(f0.getAbsolutePath())) && !f0.exists());

        File f1 = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createDirectory(f1));
        Assertions.assertTrue(!FileUtils.deleteFile(new File(f1.getAbsolutePath())) && f1.exists());
    }

    @Test
    public void testDeleteDirectoryString() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.createFile(f0));
        Assertions.assertTrue(!FileUtils.deleteDirectory(new File(f0.getAbsolutePath())) && f0.exists());

        File f1 = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createDirectory(f1));
        Assertions.assertTrue(FileUtils.deleteDirectory(new File(f1.getAbsolutePath())) && !f1.exists());
    }

    @Test
    public void testDeleteDirectoryFile() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        FileUtils.assertCreateFile(f0);
        Assertions.assertTrue(!FileUtils.deleteDirectory(f0) && f0.exists());

        File dir1 = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createDirectory(dir1));
        Assertions.assertTrue(FileUtils.deleteDirectory(dir1) && !dir1.exists());
    }

    @Test
    public void testCleanDirectoryString() throws IOException {
        File dir = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createDirectory(dir));

        File cdir = new File(dir, "cdir");
        Assertions.assertTrue(FileUtils.createDirectory(cdir));

        File f1 = new File(cdir, "test.del");
        Assertions.assertTrue(FileUtils.createFile(f1));

        File f2 = new File(dir, "test.del");
        Assertions.assertTrue(FileUtils.createFile(f2));
        Assertions.assertTrue(FileUtils.clearDirectory(dir) && !cdir.exists() && !f1.exists() && !f2.exists());
    }

    @Test
    public void testCleanDirectoryString1() throws IOException {
        File dir = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.delete(dir));
        Assertions.assertTrue(FileUtils.clearDirectory(dir));

        Assertions.assertTrue(FileUtils.delete(dir));
        Assertions.assertTrue(FileUtils.createFile(dir));
        Assertions.assertFalse(FileUtils.clearDirectory(dir));
    }

    @Test
    public void testCleanDirectoryFile() throws IOException {
        File dir = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createDirectory(dir));

        File cdir = new File(dir, "cdir");
        Assertions.assertTrue(FileUtils.createDirectory(cdir));

        File f1 = new File(cdir, "test.del");
        Assertions.assertTrue(FileUtils.createFile(f1));

        File f2 = new File(dir, "test.del");
        Assertions.assertTrue(FileUtils.createFile(f2));
        Assertions.assertTrue(FileUtils.clearDirectory(dir) && !cdir.exists() && !f1.exists() && !f2.exists());
    }

    @Test
    public void testGetLineContent() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "l1\nl2\nl3"));
        Assertions.assertEquals("l1", FileUtils.readline(file, null, 1));
        Assertions.assertEquals("l2", FileUtils.readline(file, null, 2));
        Assertions.assertEquals("l3", FileUtils.readline(file, null, 3));
    }

    @Test
    public void testrename() throws IOException {
        File dir = FileUtils.getTempDir("test", Dates.format17());

        File f1 = new File(dir, "test1.txt");
        FileUtils.assertCreateFile(f1);
        File f2 = new File(dir, "test2.txt");
        FileUtils.assertDelete(f2);

        Assertions.assertTrue(FileUtils.rename(f1, f2, null));
        Assertions.assertFalse(f1.exists());
        Assertions.assertTrue(f2.exists());
    }

    @Test
    public void testrename1() throws IOException {
        File dir = FileUtils.getTempDir("test", Dates.format17());

        File f1 = new File(dir, "test1.txt");
        FileUtils.assertCreateFile(f1);

        File f2 = new File(dir, "test2.txt");
        FileUtils.assertCreateFile(f2);

        Assertions.assertTrue(FileUtils.rename(f1, f2, null));
        Assertions.assertFalse(f1.exists());
        Assertions.assertTrue(f2.exists());
    }

    @Test
    public void testrename2() throws IOException {
        File dir = FileUtils.getTempDir("test", Dates.format17());

        File dir1 = new File(dir, Dates.format17());
        FileUtils.assertCreateDirectory(dir1);
        Assertions.assertTrue(FileUtils.clearDirectory(dir1));

        File f1 = new File(dir, "test1.txt");
        FileUtils.assertCreateFile(f1);

        File f2 = new File(dir, "test2.txt");
        FileUtils.assertCreateFile(f2);

        File newfile2 = new File(dir1, f2.getName());
        Assertions.assertTrue(FileUtils.rename(f1, f2, newfile2));
        Assertions.assertFalse(f1.exists());
        Assertions.assertTrue(f2.exists());
        Assertions.assertTrue(newfile2.exists());
    }

    @Test
    public void testrename3() throws IOException {
        File dir = FileUtils.getTempDir("test", Dates.format17());

        File f1 = new File(dir, "test1.txt");
        FileUtils.assertCreateFile(f1);

        File dir1 = new File(dir, Dates.format17());
        FileUtils.assertCreateDirectory(dir1);

        File f2 = new File(dir1, "test2.txt");
        FileUtils.assertCreateFile(f2);
        Assertions.assertTrue(FileUtils.rename(f1, f2, null));
        Assertions.assertFalse(f1.exists());
        Assertions.assertTrue(f2.exists());
    }

    @Test
    public void testMoveFileToRecycle() throws IOException {
        File file = FileUtils.createTempFile(null);
        File dir = FileUtils.createTempDirectory(null);
        Assertions.assertTrue(FileUtils.createFile(file));
        Assertions.assertTrue(FileUtils.createDirectory(dir));
        Assertions.assertTrue(FileUtils.move2Recycle(file) && !file.exists());
    }

    @Test
    public void testcreateFileQuiet() throws IOException {
        File file = new FileProxy(FileUtils.createTempDirectory(null));
        Assertions.assertFalse(FileUtils.Atomic.createFileQuiet(file));
    }

    @Test
    public void createDirectory() {
        File parent = FileUtils.createTempDirectory(null);
        FileUtils.createDirectory(parent, "a", "b", "c", "d");

        parent = FileUtils.createTempDirectory(null);
        FileUtils.createDirectory(parent, "a");

        parent = FileUtils.createTempDirectory(null);
        FileUtils.createDirectory(parent);
    }

    @Test
    void findUpward() {
        File parent = FileUtils.createTempDirectory(null);
        File d = FileUtils.createDirectory(parent, "a", "b", "c", "d");
        File a = new File(parent, "a");
        File dest = FileUtils.createNewFile(a, "test.txt");
        File upward = FileUtils.findUpward(d, "test.txt");
        Assert.assertEquals(dest, upward);
    }

    @Test
    public void testReplaceFileSeparator() {
        Assertions.assertEquals("|", FileUtils.replaceFolderSeparator("/", '|'));
        Assertions.assertEquals("|", FileUtils.replaceFolderSeparator("\\", '|'));
        Assertions.assertEquals("|home", FileUtils.replaceFolderSeparator("/home", '|'));
        Assertions.assertEquals("|home|test|shell|mulu", FileUtils.replaceFolderSeparator("/home/test/shell/mulu", '|'));
        Assertions.assertEquals("|home|test|shell|mulu|", FileUtils.replaceFolderSeparator("/home/test/shell/mulu\\", '|'));
    }

    @Test
    public void testGetLineSeparator() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "a\nb\nc\n\n"));
        Assertions.assertEquals("\n", FileUtils.readLineSeparator(file));
    }

    @Test
    public void testGetSystemTempDir() {
        Assertions.assertTrue(FileUtils.getTempDir("test", FileUtilsTest.class.getSimpleName()).exists() && FileUtils.getTempDir("test", FileUtilsTest.class.getSimpleName()).isDirectory());
    }

    @Test
    public void testGetSystemRecycle() {
        Assertions.assertTrue(FileUtils.getRecycle().exists() && FileUtils.getRecycle().isDirectory());
    }

    @Test
    public void testCopy() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        FileUtils.write(f0, CharsetUtils.get(), false, "1,2,3,4,5");

        File f1 = new File(f0.getParentFile(), "clone_" + f0.getName());
        Assertions.assertTrue(FileUtils.copy(f0, f1) && FileUtils.readline(f1, null, 1).equals("1,2,3,4,5"));
    }

    @Test
    public void testWriteFileFileBooleanString() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "1\n"));
        Assertions.assertEquals("1", FileUtils.readline(file, CharsetUtils.get(), 1));

        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "1\n2"));
        Assertions.assertEquals("1", FileUtils.readline(file, CharsetUtils.get(), 1));

        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), true, "\n3"));
        Assertions.assertEquals("3", FileUtils.readline(file, CharsetUtils.get(), 3));
    }

    @Test
    public void testWriteFileFileString() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "1\n2\n3"));
        Assertions.assertEquals("3", FileUtils.readline(file, CharsetUtils.get(), 3));
    }

    @Test
    public void testwrite() throws IOException {
        File file = FileUtils.createTempFile(null);
        Assertions.assertTrue(FileUtils.write(file, CharsetUtils.get(), false, "1\n2\n3"));
        Assertions.assertEquals("3", FileUtils.readline(file, CharsetUtils.get(), 3));

        File file1 = FileUtils.createTempFile(null);
        FileInputStream in = new FileInputStream(file);
        Assertions.assertTrue(FileUtils.write(file1, CharsetUtils.get(), false, in));
        in.close();
        Assertions.assertEquals("3", FileUtils.readline(file1, CharsetUtils.get(), 3));
    }

    @Test
    public void testEqualsFileFileInt() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        File f1 = FileUtils.createTempFile(null);

        Assertions.assertTrue(FileUtils.write(f0, CharsetUtils.get(), false, ""));
        Assertions.assertTrue(FileUtils.write(f1, CharsetUtils.get(), false, ""));
        Assertions.assertTrue(FileUtils.equals(f0, f1, 0));

        Assertions.assertTrue(FileUtils.write(f0, CharsetUtils.get(), false, "1"));
        Assertions.assertTrue(FileUtils.write(f1, CharsetUtils.get(), false, "1"));
        Assertions.assertTrue(FileUtils.equals(f0, f1, 0));

        Assertions.assertTrue(FileUtils.write(f0, CharsetUtils.get(), false, "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        Assertions.assertTrue(FileUtils.write(f1, CharsetUtils.get(), false, "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        Assertions.assertTrue(FileUtils.equals(f0, f1, 0));

        Assertions.assertTrue(FileUtils.write(f0, CharsetUtils.get(), false, "1"));
        Assertions.assertTrue(FileUtils.write(f1, CharsetUtils.get(), false, "2"));
        Assertions.assertFalse(FileUtils.equals(f0, f1, 0));
    }

    @Test
    public void testEqualsIgnoreLineSeparator() throws IOException {
        File f0 = FileUtils.createTempFile(null);
        File f1 = FileUtils.createTempFile(null);

        Assertions.assertTrue(FileUtils.write(f0, CharsetUtils.get(), false, "1"));
        Assertions.assertTrue(FileUtils.write(f1, CharsetUtils.get(), false, "1"));
        Assertions.assertEquals(FileUtils.equalsIgnoreLineSeparator(f0, CharsetUtils.get(), f1, CharsetUtils.get(), 0), 0);

        Assertions.assertTrue(FileUtils.write(f0, CharsetUtils.get(), false, "1234567\n890123456789012345678\r90123456789012345\r\n678901234567890123456789012345\n678901234567890"));
        Assertions.assertTrue(FileUtils.write(f1, CharsetUtils.get(), false, "1234567\r\n890123456789012345678\n90123456789012345\n678901234567890123456789012345\r\n678901234567890"));
        Assertions.assertEquals(FileUtils.equalsIgnoreLineSeparator(f0, CharsetUtils.get(), f1, CharsetUtils.get(), 0), 0);
    }

    @Test
    public void test100() throws IOException, InterruptedException {
        File parent = FileUtils.createTempDirectory(null);
        FileUtils.assertClearDirectory(parent);

        final File file = new File(parent, "test.del");
        Assertions.assertTrue(FileUtils.write(file, "utf-8", false, "testset"));
        Logs.info("before: " + FileUtils.readline(file, "utf-8", 0));

        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    String str = "test" + Dates.currentTimeStamp();
                    FileUtils.write(file, "utf-8", false, str);
                } catch (Exception e) {
                    Logs.error(e.getLocalizedMessage(), e);
                    Assertions.fail();
                }
            }
        };
        thread.start();

        List<File> list = FileUtils.isWriting(parent, 2000);
        Logs.info("主线程恢复运行 ..");
        Logs.info(" after: " + FileUtils.readline(file, "utf-8", 0));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(file, list.get(0));
    }

    @Test
    public void testcreateTempfile() throws IOException {
        File tempfile = FileUtils.createTempFile("testfile.txt");
        Assertions.assertTrue(tempfile.exists());

        // 重复创建同一个文件来测试
        File newfile = FileUtils.createTempFile("testfile.txt");
        Assertions.assertNotEquals(tempfile, newfile);
    }

    @Test
    public void testCodepages() throws IOException {
        // 加载用户根目录下的 holidays.xml 配置文件进行测试
        File home = Codepage.CODEPAGE_CONFIG_DIR;
        FileUtils.delete(home);
        FileUtils.createDirectory(home);
        FileUtils.write(new File(home, "codepages.xml"), CharsetName.UTF_8, false, ClassUtils.getClassLoader().getResourceAsStream("homeFile/codepages.xml"));

        // 加载用户自定义目录下的国家法定假日配置文件
        File userDefine = new File(FileUtils.joinPath(Settings.getProjectHome().getAbsolutePath(), "userDefine"));
        FileUtils.delete(userDefine);
        FileUtils.createDirectory(userDefine);
        FileUtils.write(new File(userDefine, "codepages1.xml"), CharsetName.UTF_8, false, ClassUtils.getClassLoader().getResourceAsStream("user/codepages1.xml"));
        FileUtils.write(new File(userDefine, "codepages2.xml"), CharsetName.UTF_8, false, ClassUtils.getClassLoader().getResourceAsStream("user/codepages2.xml"));
        System.setProperty(NationalHolidays.PROPERTY_HOLIDAY, userDefine.getAbsolutePath());

        // 重新加载
        FileUtils.CODEPAGE.reload();

        Assertions.assertEquals("UTF-8", FileUtils.getCodepage("1208"));
        Assertions.assertEquals("UTF-8", FileUtils.getCodepage(1208));
        Assertions.assertEquals("GBK", FileUtils.getCodepage(1386));
        Assertions.assertEquals("1208", FileUtils.getCodepage("UTF-8"));
        Assertions.assertEquals("1386", FileUtils.getCodepage("GBK"));
        Assertions.assertEquals("377", FileUtils.getCodepage("IBM0377"));
        Assertions.assertEquals("378", FileUtils.getCodepage("IBM0378"));
        Assertions.assertEquals("379", FileUtils.getCodepage("IBM0379"));
        Assertions.assertEquals("380", FileUtils.getCodepage("IBM0380"));
    }

    private static class FileProxy extends File {
        public FileProxy(File file) {
            super(file.getAbsolutePath());
        }

        public boolean createNewFile() throws IOException {
            throw new IOException("test createNewFile()");
        }
    }
}
