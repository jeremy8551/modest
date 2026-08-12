package cn.org.expect.script.command;

import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalCommandCompilerResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * 验证存在同名候选项的命令编译器匹配规则
 */
public class CommandCompilerMatchTest {

    /**
     * 验证普通 while 与 while read 匹配规则互斥且忽略大小写
     */
    @Test
    public void testWhileCompilerMatches() {
        WhileCommandCompiler whileCompiler = new WhileCommandCompiler();
        ReadCommandCompiler readCompiler = new ReadCommandCompiler();

        String whileScript = "  WHILE index < 10 LOOP echo ${index}; END LOOP";
        this.assertMatches(whileCompiler, whileScript);
        this.assertIgnores(readCompiler, whileScript);

        String readScript = "  WHILE READ line DO echo ${line}; DONE < file.txt";
        this.assertIgnores(whileCompiler, readScript);
        this.assertMatches(readCompiler, readScript);
    }

    /**
     * 验证数据库命令支持缩进和 client 选项
     */
    @Test
    public void testDatabaseCompilerMatches() {
        this.assertMatches(new DBLoadCommandCompiler(), "  DB LOAD CLIENT FROM data.del OF del");
        this.assertMatches(new DBLoadCommandCompiler(), "  db load from data.del of del");
        this.assertMatches(new DBExportCommandCompiler(), "  DB EXPORT TO data.del OF del SELECT 1");
        this.assertMatches(new DBGetCfgForCommandCompiler(), "  DB GET CFG FOR catalog");
        this.assertMatches(new DBConnectCommandCompiler(), "  DB CONNECT TO catalog");
        this.assertIgnores(new DBConnectCommandCompiler(), "db connect invalid");
    }

    /**
     * 验证 declare 命令不会接受残缺或拼错的关键字
     */
    @Test
    public void testDeclareCompilerRejectsMalformedKeywords() {
        DeclareCatalogCommandCompiler catalogCompiler = new DeclareCatalogCommandCompiler();
        this.assertMatches(catalogCompiler, "DECLARE GLOBAL test CATALOG CONFIGURATION USE driver driverClass");
        this.assertIgnores(catalogCompiler, "declare g test catalog configuration use driver driverClass");
        this.assertIgnores(catalogCompiler, "declare test catalog configuration u driver driverClass");

        DeclareHandlerCommandCompiler handlerCompiler = new DeclareHandlerCommandCompiler();
        this.assertMatches(handlerCompiler, "DECLARE GLOBAL CONTINUE HANDLER FOR exception BEGIN echo error; END");
        this.assertIgnores(handlerCompiler, "declare g continue handler for exception begin echo error; end");
    }

    /**
     * 验证空脚本不会导致通配 Compiler 抛出越界异常
     */
    @Test
    public void testSubCommandCompilerIgnoresEmptyScript() {
        this.assertIgnores(new SubCommandCompiler(), "");
    }

    /**
     * 断言编译器接受脚本
     *
     * @param compiler 命令编译器
     * @param script   脚本内容
     */
    private void assertMatches(UniversalCommandCompiler compiler, String script) {
        Assert.assertEquals(UniversalCommandCompilerResult.NEUTRAL, compiler.match(null, "", script));
    }

    /**
     * 断言编译器忽略脚本
     *
     * @param compiler 命令编译器
     * @param script   脚本内容
     */
    private void assertIgnores(UniversalCommandCompiler compiler, String script) {
        Assert.assertEquals(UniversalCommandCompilerResult.IGNORE, compiler.match(null, "", script));
    }
}
