package cn.org.expect.script.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.org.expect.expression.LoginExpression;
import cn.org.expect.expression.WordIterator;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.mail.MailFile;
import cn.org.expect.script.UniversalScriptAnalysis;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptParser;
import cn.org.expect.script.UniversalScriptReader;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.annotation.EasyCommandCompiler;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * email send filepath of protocal modified by ssl title= attachments= sender= charset=UTF-8 to receivers by user@ip:port?password=
 *
 * @author jeremy8551@gmail.com
 * @createtime 2022-01-12
 */
@EasyCommandCompiler(name = "email", keywords = {"email"})
public class EmailSendCommandCompiler extends AbstractTraceCommandCompiler {

    public String read(UniversalScriptReader in, UniversalScriptAnalysis analysis) throws IOException {
        return in.readSinglelineScript();
    }

    public AbstractTraceCommand compile(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptParser parser, UniversalScriptAnalysis analysis, String orginalScript, String command) throws Exception {
        WordIterator it = analysis.parse(command);
        it.assertNext("email");
        it.assertNext("send");
        String filepathExpr = it.readUntil("of");
        String filepath = analysis.replaceShellVariable(session, context, analysis.unQuotation(filepathExpr), true, !analysis.containsQuotation(filepathExpr));
        String protocal = analysis.replaceShellVariable(session, context, it.next(), true, true);
        it.assertNext("modified");
        it.assertNext("by");

        CommandAttribute attrs = new CommandAttribute(session, context, "ssl", "title:", "attach:", "sender:", "charset:");
        while (!it.isNext("to")) {
            String str = it.next();
            String[] array = StringUtils.splitProperty(str);
            if (array == null) {
                attrs.setAttribute(str, "");
            } else {
                attrs.setAttribute(array[0], array[1]);
            }
        }

        it.assertNext("to");
        String receiversExpr = it.next();
        String receivers = analysis.replaceShellVariable(session, context, analysis.unQuotation(receiversExpr), true, !analysis.containsQuotation(receiversExpr));
        List<String> receiverList = new ArrayList<String>(); // 接收地址
        StringUtils.split(receivers, ',', receiverList);

        it.assertNext("by");
        String loginExpr = it.next();
        String login = analysis.replaceShellVariable(session, context, analysis.unQuotation(loginExpr), true, !analysis.containsQuotation(loginExpr));
        it.assertOver();

        boolean ssl = attrs.contains("ssl"); // 是否使用安全连接
        String title = attrs.getAttribute("title"); // 标题
        String sender = attrs.getAttribute("sender"); // 发送地址
        String charsetName = attrs.getAttribute("charset"); // 邮件服务器字符集
        String content = FileUtils.readline(new File(filepath), charsetName, 0); // 正文
        EasyContext ioc = context.getContainer();

        // 附件
        String[] attaches = StringUtils.split(attrs.getAttribute("attach"), ',');
        MailFile[] mfs = new MailFile[attaches.length];
        for (int i = 0; i < attaches.length; i++) {
            mfs[i] = new MailFile(ioc, new File(attaches[i]));
        }

        // 登陆表达式
        LoginExpression loginExpression = new LoginExpression(analysis, "email " + login);
        String username = loginExpression.getLoginUsername();
        String password = loginExpression.getLoginPassword();
        int port = StringUtils.parseInt(loginExpression.getLoginPort(), -1); // 默认值 -1
        String host = loginExpression.getLoginHost();

        return new EmailSendCommand(this, orginalScript, host, username, password, charsetName, port, protocal, ssl, sender, receiverList, title, content, mfs);
    }
}
