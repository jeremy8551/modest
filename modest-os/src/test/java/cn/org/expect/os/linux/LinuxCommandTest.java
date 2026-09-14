package cn.org.expect.os.linux;

import cn.org.expect.os.OSCommandException;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Linux 本地命令执行测试
 */
public class LinuxCommandTest {

    /**
     * 验证标准输出与错误输出分别写入对应缓冲区
     */
    @Test
    public void testStdoutAndStderr() {
        LinuxCommand command = new LinuxCommand();
        int exitValue = command.execute("printf stdout; printf stderr >&2");

        Assert.assertEquals(0, exitValue);
        Assert.assertEquals("stdout", StringUtils.trimBlank(command.getStdout()));
        Assert.assertEquals("stderr", StringUtils.trimBlank(command.getStderr()));
    }

    /**
     * 验证超时命令会被及时终止
     */
    @Test
    public void testTimeout() {
        LinuxCommand command = new LinuxCommand();
        long start = System.currentTimeMillis();
        try {
            command.execute("sleep 1", 50);
            Assert.fail("命令执行超时后应抛出异常");
        } catch (OSCommandException expected) {
            Assert.assertTrue(System.currentTimeMillis() - start < 900);
        }
    }
}
