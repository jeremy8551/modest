package cn.org.expect.mail;

import cn.org.expect.util.Dates;
import org.apache.commons.mail.SimpleEmail;

public class SendEmailExample {

    public void test() {
        try {
            SimpleEmail mail = new SimpleEmail();
            mail.setHostName("mail.foxmail.com");
            mail.setAuthentication("lvzhaojun", "scfeymoohqyubhci");// 邮件服务器验证：用户名/密码
            mail.setCharset("UTF-8");// 必须放在前面，否则乱码
            mail.addTo("410336929@qq.com");
            mail.setSSLOnConnect(true);
            mail.setFrom("etl@foxmail.com", "测试 邮件");
            mail.setSubject("测试单统计信息-" + Dates.currentTimeStamp());
            mail.setDebug(false);

            String msg = "测试单统计信息如下：" + "\r\n\t" + //
                    "1、测试单数量: 4" + "\r\n\t" + //
                    "2、测试单数量：5" + "\r\n\t" + //
                    "3、测试单成功数量：6" + "\r\n\t" + //
                    "统计时间：1 " + Dates.currentTimeStamp();

            mail.setMsg(msg);
            mail.send();
            System.out.println("邮件发送完毕！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
