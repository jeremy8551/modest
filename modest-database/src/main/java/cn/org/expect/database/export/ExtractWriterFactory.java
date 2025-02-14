package cn.org.expect.database.export;

import cn.org.expect.database.export.inernal.ExtractFileWriter;
import cn.org.expect.database.export.inernal.FtpFileWriter;
import cn.org.expect.database.export.inernal.HttpRequestWriter;
import cn.org.expect.database.export.inernal.SftpFileWriter;
import cn.org.expect.expression.BaseAnalysis;
import cn.org.expect.expression.LoginExpression;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

/**
 * 输出流的工厂类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-02-18
 */
@EasyBean
public class ExtractWriterFactory implements EasyBeanFactory<ExtractWriter> {

    public ExtractWriter build(EasyContext context, Object... args) throws Exception {
        ExtracterContext cxt = ArrayUtils.indexOf(args, ExtracterContext.class, 0);
        assert cxt != null;
        ExtractMessage message = ArrayUtils.indexOf(args, ExtractMessage.class, 0);
        String target = StringUtils.trimBlank(cxt.getTarget());

        // bean://kind
        if (StringUtils.startsWith(target, "bean://", 0, true, true)) {
            String[] array = StringUtils.split(target, '/');
            EasyBeanEntry entry = context.getBeanEntry(ExtractWriter.class, array[2]);
            return context.newInstance(entry.getType());
        }

        // http://download/filename
        if (StringUtils.startsWith(target, "http://", 0, true, true)) {
            String[] list = StringUtils.split(target.substring("http://".length()), '/');
            String filename = ArrayUtils.last(list);
            return new HttpRequestWriter(cxt.getHttpServletRequest(), cxt.getHttpServletResponse(), filename, cxt.getFormat(), message);
        }

        // sftp://name@host:port?password=/filepath
        if (StringUtils.startsWith(target, "sftp://", 0, true, true)) {
            String[] list = StringUtils.split(target.substring("sftp://".length()), '/');
            LoginExpression login = new LoginExpression(new BaseAnalysis(), "sftp " + list[0]);
            String host = login.getLoginHost();
            String port = login.getLoginPort();
            String username = login.getLoginUsername();
            String password = login.getLoginPassword();
            return new SftpFileWriter(cxt, message, host, port, username, password, list[1]);
        }

        // ftp://name@host:port?password=/filepath
        if (StringUtils.startsWith(target, "ftp://", 0, true, true)) {
            String[] list = StringUtils.split(target.substring("ftp://".length()), '/');
            LoginExpression cmd = new LoginExpression(new BaseAnalysis(), "ftp " + list[0]);
            String host = cmd.getLoginHost();
            String port = cmd.getLoginPort();
            String username = cmd.getLoginUsername();
            String password = cmd.getLoginPassword();
            return new FtpFileWriter(cxt, message, host, port, username, password, list[1]);
        }

        // 文件绝对路径
        return new ExtractFileWriter(cxt, message);
    }
}
