package cn.org.expect.script;

import java.util.HashMap;
import java.util.Map;

import cn.org.expect.script.compiler.ScriptAnalysis;
import cn.org.expect.script.session.ScriptMainProcess;
import cn.org.expect.script.session.ScriptSession;
import cn.org.expect.script.session.SessionFactory;
import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ScriptAnalysisTest {

    @Test
    public void test() {
        Assert.assertEquals(0, new ScriptAnalysis().indexOf("abc", "aBc", 0, 0, 0));
        Assert.assertEquals(-1, new ScriptAnalysis().indexOf("abc", "Bc", 0, 1, 0));
        Assert.assertEquals(-1, new ScriptAnalysis().indexOf(" bcd", "Bc", 0, 1, 1));
        Assert.assertEquals(1, new ScriptAnalysis().indexOf(" bc ", "Bc", 0, 1, 1));
        Assert.assertEquals(1, new ScriptAnalysis().indexOf("*bc<", "Bc", 0, 1, 1));
    }

    @Test
    public void test1() {
        Assert.assertEquals("abc", new ScriptAnalysis().trim("abc", 0, 0));
        Assert.assertEquals("abc", new ScriptAnalysis().trim(" abc ", 0, 0));
        Assert.assertEquals("abc", new ScriptAnalysis().trim("abc   ", 0, 0));
        Assert.assertEquals("abc", new ScriptAnalysis().trim("abc ; ", 0, 1));
        Assert.assertEquals("abc", new ScriptAnalysis().trim(" ; ; abc ; ", 1, 1));
    }

    @Test
    public void test11() {
        Assert.assertFalse(new ScriptAnalysis().containsQuotation(""));
        Assert.assertTrue(new ScriptAnalysis().containsQuotation("''"));
        Assert.assertTrue(new ScriptAnalysis().containsQuotation(" ' ' "));
        Assert.assertTrue(new ScriptAnalysis().containsQuotation(" '' "));
        Assert.assertTrue(new ScriptAnalysis().containsQuotation(" 'a' "));
        Assert.assertFalse(new ScriptAnalysis().containsQuotation(" 'a' \"b\" "));
        Assert.assertFalse(new ScriptAnalysis().containsQuotation(" 'a' '' "));
        Assert.assertFalse(new ScriptAnalysis().containsQuotation(" 'a' 'b' "));
        Assert.assertTrue(new ScriptAnalysis().containsQuotation("\" \""));
        Assert.assertFalse(new ScriptAnalysis().containsQuotation("\" \"\"\""));
        Assert.assertTrue(new ScriptAnalysis().containsQuotation("\"''\""));
    }

    @Test
    public void test1123() {
        Assert.assertFalse(new ScriptAnalysis().containsSide("", '\'', '\''));
        Assert.assertTrue(new ScriptAnalysis().containsSide("''", '\'', '\''));
        Assert.assertTrue(new ScriptAnalysis().containsSide(" ' ' ", '\'', '\''));
        Assert.assertTrue(new ScriptAnalysis().containsSide(" '' ", '\'', '\''));
        Assert.assertTrue(new ScriptAnalysis().containsSide(" 'a' ", '\'', '\''));
        Assert.assertFalse(new ScriptAnalysis().containsSide(" 'a' \"b\" ", '\'', '\''));
        Assert.assertFalse(new ScriptAnalysis().containsSide(" 'a' '' ", '\'', '\''));
        Assert.assertFalse(new ScriptAnalysis().containsSide(" 'a' 'b' ", '\'', '\''));
        Assert.assertTrue(new ScriptAnalysis().containsSide("\" \"", '\"', '\"'));
        Assert.assertFalse(new ScriptAnalysis().containsSide("\" \"\"\"", '\"', '\"'));
        Assert.assertTrue(new ScriptAnalysis().containsSide("\"''\"", '\"', '\"'));
    }

    @Test
    public void test112() {
        Assert.assertEquals("", new ScriptAnalysis().unQuotation(""));
        Assert.assertEquals("", new ScriptAnalysis().unQuotation("''"));
        Assert.assertEquals(" ", new ScriptAnalysis().unQuotation(" ' ' "));
        Assert.assertEquals("", new ScriptAnalysis().unQuotation(" '' "));
        Assert.assertEquals("a", new ScriptAnalysis().unQuotation(" 'a' "));
        Assert.assertEquals(" 'a' \"b\" ", new ScriptAnalysis().unQuotation(" 'a' \"b\" "));
        Assert.assertEquals(" 'a' '' ", new ScriptAnalysis().unQuotation(" 'a' '' "));
        Assert.assertEquals(" 'a' 'b' ", new ScriptAnalysis().unQuotation(" 'a' 'b' "));
        Assert.assertEquals(" ", new ScriptAnalysis().unQuotation("\" \""));
        Assert.assertEquals("\" \"\"\"", new ScriptAnalysis().unQuotation("\" \"\"\""));
        Assert.assertEquals("''", new ScriptAnalysis().unQuotation("\"''\""));
        Assert.assertEquals(" '' ", new ScriptAnalysis().unQuotation("\" '' \""));
    }

    @Test
    public void test11267() {
        Assert.assertEquals("", new ScriptAnalysis().removeSide("", '\'', '\''));
        Assert.assertEquals("", new ScriptAnalysis().removeSide("''", '\'', '\''));
        Assert.assertEquals(" ", new ScriptAnalysis().removeSide(" ' ' ", '\'', '\''));
        Assert.assertEquals("", new ScriptAnalysis().removeSide(" '' ", '\'', '\''));
        Assert.assertEquals("a", new ScriptAnalysis().removeSide(" 'a' ", '\'', '\''));
        Assert.assertEquals(" 'a' \"b\" ", new ScriptAnalysis().removeSide(" 'a' \"b\" ", '\'', '\''));
        Assert.assertEquals(" 'a' '' ", new ScriptAnalysis().removeSide(" 'a' '' ", '\'', '\''));
        Assert.assertEquals(" 'a' 'b' ", new ScriptAnalysis().removeSide(" 'a' 'b' ", '\'', '\''));
        Assert.assertEquals(" ", new ScriptAnalysis().removeSide("\" \"", '"', '"'));
        Assert.assertEquals("\" \"\"\"", new ScriptAnalysis().removeSide("\" \"\"\"", '"', '"'));
        Assert.assertEquals("''", new ScriptAnalysis().removeSide("\"''\"", '"', '"'));
        Assert.assertEquals(" '' ", new ScriptAnalysis().removeSide("\" '' \"", '"', '"'));
    }

    @Test
    public void testReplaceShellFunctionVariable() {
        ScriptAnalysis obj = new ScriptAnalysis();

        UniversalScriptSession session = new ScriptSession("engine001", new SessionFactory()) {

            public String getId() {
                return "sessionid";
            }

            public ScriptMainProcess getMainProcess() {
                return new ScriptMainProcess() {

                    public Integer getExitcode() {
                        return -1;
                    }
                };
            }

            public String[] getFunctionParameter() {
                return new String[]{"test", "1", "2", "3"};
            }
        };
        String str = "$1 is equals $2 [$3$ $4 $d";
        Assert.assertEquals("1 is equals 2 [3$ $4 $d", obj.replaceShellSpecialVariable(session, str, true));

        session = new ScriptSession("engine001", new SessionFactory()) {

            public String getId() {
                return "sessionid";
            }

            public ScriptMainProcess getMainProcess() {
                return new ScriptMainProcess() {

                    public Integer getExitcode() {
                        return -1;
                    }
                };
            }

            public String[] getFunctionParameter() {
                return new String[]{"funcs", "PROC_QYZX_SBC_BAOHANS"};
            }
        };
        Assert.assertEquals("call TESTADM.PROC_QYZX_SBC_BAOHANS('2017-07-31', ?); 1 funcs", obj.replaceShellSpecialVariable(session, "call TESTADM.$1('2017-07-31', ?); $# $0", false));

        session = new ScriptSession("engine001", new SessionFactory()) {

            public String getId() {
                return "sessionid";
            }

            public ScriptMainProcess getMainProcess() {
                return new ScriptMainProcess() {

                    public Integer getExitcode() {
                        return -1;
                    }
                };
            }

            public String[] getFunctionParameter() {
                return new String[]{"funcs", "PROC_QYZX_SBC_BAOHANS"};
            }
        };
        Assert.assertEquals("call TESTADM.PROC_QYZX_SBC_BAOHANS('2017-07-31', ?); 1 funcs -1", obj.replaceShellSpecialVariable(session, "call TESTADM.$1('2017-07-31', ?); $# $0 $?", true));

        session = new ScriptSession("engine001", new SessionFactory()) {

            public String getId() {
                return "sessionid";
            }

            public ScriptMainProcess getMainProcess() {
                return new ScriptMainProcess() {

                    public Integer getExitcode() {
                        return -1;
                    }
                };
            }

            public String[] getFunctionParameter() {
                return new String[]{"funcs", "PROC_QYZX_SBC_BAOHANS"};
            }
        };
        Assert.assertEquals("call 'TESTADM.$1(2017-07-31, ?)'; 1 funcs -1", obj.replaceShellSpecialVariable(session, "call 'TESTADM.$1(2017-07-31, ?)'; $# $0 $?", true));

        session = new ScriptSession("engine001", new SessionFactory()) {

            public String getId() {
                return "sessionid";
            }

            public ScriptMainProcess getMainProcess() {
                return new ScriptMainProcess() {

                    public Integer getExitcode() {
                        return -1;
                    }
                };
            }

            public String[] getFunctionParameter() {
                return StringUtils.splitByBlank("test 1 2 3 4 5 6 7 8 9 10 11");
            }
        };
        Assert.assertEquals("11 -1 test 1 2 11 10", obj.replaceShellSpecialVariable(session, "$# $? $0 $1 $2 $11 $10", true));
    }

    @Test
    public void testreplaceShellVariable() {
        ScriptAnalysis obj = new ScriptAnalysis();
        Map<String, Object> b = new HashMap<String, Object>();
        b.put("name", "2");
        b.put("key", "51");
        b.put("jdbc", "C:\\Users\\etl\\rpt\\lib\\jdbc.properties");

        Assert.assertEquals("", obj.replaceShellVariable("", b, null, true, true));
        Assert.assertEquals("${name1}", obj.replaceShellVariable("${name1}", b, null, true, true));
        Assert.assertEquals("123", obj.replaceShellVariable("123", b, null, true, true));
        Assert.assertEquals("2", obj.replaceShellVariable("${name}", b, null, true, true));
        Assert.assertEquals("51", obj.replaceShellVariable("${key}", b, null, true, true));
        Assert.assertEquals("123", obj.replaceShellVariable("1${name}3", b, null, true, true));
        Assert.assertEquals("C:\\Users\\etl\\rpt\\lib\\jdbc.properties", obj.replaceShellVariable("${jdbc}", b, null, true, true));

        Assert.assertEquals("12512", obj.replaceShellVariable("1${name}${key}${name}", b, null, true, true));

        b.put("current_table_columns_msg", "");
        b.put("tmp_colname", "reqID");
        Assert.assertEquals("reqID", obj.replaceShellVariable("${current_table_columns_msg}${tmp_colname}", b, null, true, true));
        Assert.assertEquals(" reqID", obj.replaceShellVariable("${current_table_columns_msg} ${tmp_colname}", b, null, true, true));

        Assert.assertEquals("2", obj.replaceShellVariable("$name", b, null, true, true));
        Assert.assertEquals("2+51", obj.replaceShellVariable("$name+${key}", b, null, true, true));
        Assert.assertEquals("251", obj.replaceShellVariable("$name${key}", b, null, true, true));
        Assert.assertEquals("251", obj.replaceShellVariable("$name$key", b, null, true, true));
        Assert.assertEquals("$n", obj.replaceShellVariable("$n", b, null, true, true));
        Assert.assertEquals("${n}", obj.replaceShellVariable("${n}", b, null, true, true));
        Assert.assertEquals("${n}$n", obj.replaceShellVariable("${n}$n", b, null, true, true));
        Assert.assertEquals("${n}$n ", obj.replaceShellVariable("${n}$n ", b, null, true, true));
        Assert.assertEquals("$n ", obj.replaceShellVariable("${current_table_columns_msg}$n ", b, null, true, true));
        Assert.assertEquals("$n ", obj.replaceShellVariable("$current_table_columns_msg$n ", b, null, true, true));
        Assert.assertEquals("$$n ", obj.replaceShellVariable("$$current_table_columns_msg$n ", b, null, true, true));

        Assert.assertEquals("'$$current_table_columns_msg'$n ", obj.replaceShellVariable("'$$current_table_columns_msg'$n ", b, null, true, true));
        Assert.assertEquals("'$current_table_columns_msg ' ", obj.replaceShellVariable("'$current_table_columns_msg '$current_table_columns_msg ", b, null, true, true));
        Assert.assertEquals("'$$current_table_columns_msg'reqID ", obj.replaceShellVariable("'$$current_table_columns_msg'$tmp_colname ", b, null, true, true));

        b.clear();
        Assert.assertEquals("", obj.replaceShellVariable("$test", b, null, true, false));
        Assert.assertEquals(" ", obj.replaceShellVariable("$test ", b, null, true, false));
        Assert.assertEquals(" 2", obj.replaceShellVariable("$test 2", b, null, true, false));
        Assert.assertEquals("'$test' 2", obj.replaceShellVariable("'$test'${t} 2", b, null, true, false));
        Assert.assertEquals("'$test'", obj.replaceShellVariable("'$test'${t}", b, null, true, false));
        Assert.assertEquals("\"\"", obj.replaceShellVariable("\"$test\"${t}", b, null, true, false));
        Assert.assertEquals("\"\"1", obj.replaceShellVariable("\"$test\"${t}1", b, null, true, false));
        Assert.assertEquals("\"\"", obj.replaceShellVariable("\"$test\"$t", b, null, true, false));
    }

    @Test
    public void test1121() {
        Assert.assertEquals("", new ScriptAnalysis().unescapeString(""));
        Assert.assertEquals("$", new ScriptAnalysis().unescapeString("\\$"));
        Assert.assertEquals("' \\ '", new ScriptAnalysis().unescapeString("\\' \\ '"));
        Assert.assertEquals("\" \\ \"", new ScriptAnalysis().unescapeString("\\\" \\ \""));
    }
}
