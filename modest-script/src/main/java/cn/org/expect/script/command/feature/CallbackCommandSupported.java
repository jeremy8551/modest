package cn.org.expect.script.command.feature;

import cn.org.expect.script.command.CallbackCommandCompiler;

/**
 * 脚本命令的回调函数接口, 实现该接口的脚本命令都支持回调函数 {@linkplain CallbackCommandCompiler}
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-11-10
 */
public interface CallbackCommandSupported {

    /**
     * 返回脚本命令的输入参数
     *
     * @return 输入参数
     */
    String[] getArguments();
}
