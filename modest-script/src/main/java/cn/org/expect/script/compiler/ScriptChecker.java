package cn.org.expect.script.compiler;

import java.util.Set;

import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.script.UniversalScriptChecker;
import cn.org.expect.util.StringUtils;

/**
 * 校验规则
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean("default")
public class ScriptChecker implements UniversalScriptChecker {

    /** 数据库的关键字 */
    private Set<String> databaseKeyword;

    /** 脚本引擎的关键字 */
    private Set<String> scriptKeyword;

    public ScriptChecker() {
    }

    public void setDatabaseKeywords(Set<String> set) {
        this.databaseKeyword = set;
    }

    public void setScriptEngineKeywords(Set<String> set) {
        this.scriptKeyword = set;
    }

    public boolean checkVariableName(String name) {
        // 变量名不能为空
        if (name.length() == 0) {
            return false;
        }

        // 变量名不能是 $ 或 _
        if (StringUtils.isBlank(StringUtils.replaceAll(name, "_", "")) || name.equals("$")) {
            return false;
        }

        //  变量名第一个字符不是：英文 或 _ 或 $
        if (!StringUtils.isLetter(name.charAt(0)) && !StringUtils.inArray(name.charAt(0), '_', '$')) {
            return false;
        }

        // 变量名只能包含英文字母，数字，下划线
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (StringUtils.isNumber(c) || StringUtils.isLetter(c) || c == '_') {
                continue;
            } else {
                return false;
            }
        }

        // 变量名不能是关键字
        if (this.scriptKeyword != null && !this.scriptKeyword.isEmpty()) {
            return !this.scriptKeyword.contains(name);
        }

        return true;
    }

    public boolean checkDatabaseKeyword(String name) {
        return this.databaseKeyword == null || !this.databaseKeyword.contains(name);
    }
}
