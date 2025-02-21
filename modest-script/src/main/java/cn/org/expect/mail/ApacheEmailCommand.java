package cn.org.expect.mail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.SearchTerm;

import cn.org.expect.ModestRuntimeException;
import cn.org.expect.expression.GPatternExpression;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.CharsetUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.IO;
import cn.org.expect.util.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

/**
 * 邮件功能的接口实现类 <br>
 * <p>
 * SMTP:邮件发送协议 ssl对应端口465 非ssl对应端口25
 * <p>
 * IMAP:收邮件协议 ssl对应端口993 非ssl对应端口143
 *
 * @author jeremy8551@gmail.com
 */
public class ApacheEmailCommand implements MailCommand {
    private final static Log log = LogFactory.getLog(ApacheEmailCommand.class);

    public static String[] toString(Address[] from) throws UnsupportedEncodingException {
        String[] array = new String[from == null ? 0 : from.length];
        if (array.length > 0) {
            for (int j = 0; j < from.length; j++) {
                Address add = from[j];
                array[j] = MimeUtility.decodeText(add.toString());
            }
        }
        return array;
    }

    /**
     * &lt;name&gt; test@company.com <br>
     * <br>
     * The first place in the array is the name, the second place is the email address
     *
     * @param str 字符串
     * @return 数组
     */
    public static String[] toAddress(String str) {
        str = StringUtils.trimBlank(str);
        int index = StringUtils.lastIndexOfBlank(str, -1);
        if (index == -1) {
            return new String[]{str, str};
        } else {
            String[] array = new String[2];
            array[0] = StringUtils.trimBlank(str.substring(0, index));
            array[1] = StringUtils.trimBlank(str.substring(index));

            array[0] = StringUtils.trim(array[0], '\'');
            array[0] = StringUtils.trim(array[0], '"');
            array[0] = StringUtils.ltrim(array[0], '<');
            array[0] = StringUtils.rtrim(array[0], '>');
            array[0] = StringUtils.trim(array[0], '"');
            array[0] = StringUtils.trim(array[0], '\'');

            array[1] = StringUtils.ltrim(array[1], '<');
            array[1] = StringUtils.rtrim(array[1], '>');
            return array;
        }
    }

    private String host;
    private String username;
    private String password;
    private String charsetName;

    public String getCharsetName() {
        return this.charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @param sender  收件人
     * @param title   主题
     * @param content 内容
     */
    public void drafts(String protocol, int port, String sender, List<String> receivers, String title, CharSequence content, MailFile... attachments) {
        if (StringUtils.isBlank(protocol)) {
            protocol = "imap";
        }

        Store store = null;
        try {
            Properties config = new Properties();
            config.setProperty("mail.debug", String.valueOf(log.isDebugEnabled()));
            config.setProperty("mail.store.protocol", protocol);
            config.setProperty("mail." + protocol + ".host", host);
            config.setProperty("mail." + protocol + ".auth", "true");

            if (port > 0) {
                config.setProperty("mail." + protocol + ".port", String.valueOf(port));
            }
            config.setProperty("mail." + protocol + ".connectiontimeout", "100000");
            config.setProperty("mail." + protocol + ".timeout", "100000");

            Session session = Session.getInstance(config);
            store = session.getStore();
            store.connect(this.host, this.username, this.password);

            Folder folder = store.getFolder("Drafts");// 打开草稿箱
            folder.open(Folder.READ_WRITE);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receivers.get(0)));
            message.setSubject(title);

            if (attachments.length == 0) {
                message.setText(content.toString());
            } else {
                MimeMultipart mm = new MimeMultipart("related");

                MimeBodyPart body = new MimeBodyPart();
                body.setContent(content.toString(), "text/plain;charset=" + CharsetUtils.get());
                mm.addBodyPart(body);

                for (MailFile attchment : attachments) {
                    if (attchment != null) {
                        log.info(attchment.getFile().getAbsolutePath());
                        log.info(attchment.getName());
                        log.info(attchment.getDescription());
                        log.info(attchment.getDisposition());

                        MimeBodyPart part = new MimeBodyPart();
                        FileDataSource fds = new FileDataSource(attchment.getFile());
                        DataHandler dh = new DataHandler(fds);
                        part.setDataHandler(dh);
                        part.setFileName(fds.getName());
                        mm.addBodyPart(part);
                    }
                }

                mm.setSubType("mixed");
                message.setContent(mm);
            }

            message.setSentDate(new Date());
            message.setFlag(Flags.Flag.DRAFT, true);
            message.saveChanges();

            MimeMessage[] draftMessages = {message};
            folder.appendMessages(draftMessages);
            folder.close(false);
        } catch (Exception e) {
            throw new ModestRuntimeException("mail.stdout.message005", e);
        } finally {
            IO.close(store);
        }
    }

    public String send(String protocol, int port, boolean ssl, String sender, List<String> receivers, String title, CharSequence content, MailFile... attachments) {
        if (StringUtils.isNotBlank(protocol)) {
            Ensure.isTrue("smtp".equalsIgnoreCase(StringUtils.trimBlank(protocol)), protocol, port, ssl, sender, receivers, title, content, attachments);
        }

        try {
            if (attachments.length == 0) {
                SimpleEmail mail = new SimpleEmail();
                mail.setDebug(log.isDebugEnabled());
                mail.setHostName(this.host);
                mail.setAuthentication(this.username, this.password);// 邮件服务器验证：用户名/密码
                mail.setSSLOnConnect(ssl);
                mail.setCharset(StringUtils.coalesce(this.charsetName, CharsetName.UTF_8));// 必须放在前面，否则乱码
                if (ssl) {
                    mail.setSslSmtpPort(String.valueOf(port));
                } else {
                    mail.setSmtpPort(port);
                }

                for (String str : receivers) {
                    String[] array = toAddress(str);
                    mail.addTo(array[1], array[0]);
                }

                String[] array = toAddress(sender);
                mail.setFrom(array[1], array[0]);
                mail.setSubject(title);
                mail.setMsg(content.toString());

                String messageId = mail.send();
                if (log.isDebugEnabled()) {
                    log.debug("mail.stdout.message001");
                }
                return messageId;
            } else {
                // Create the email message
                MultiPartEmail mail = new MultiPartEmail();
                mail.setDebug(log.isDebugEnabled());
                mail.setHostName(this.host);
                mail.setAuthentication(this.username, this.password);// 邮件服务器验证：用户名/密码
                mail.setSSLOnConnect(ssl);
                mail.setCharset(StringUtils.coalesce(this.charsetName, CharsetName.UTF_8));// 必须放在前面，否则乱码

                for (String str : receivers) {
                    String[] array = toAddress(str);
                    mail.addTo(array[1], array[0]);
                }

                String[] array = toAddress(sender);
                mail.setFrom(array[1], array[0]);
                mail.setSubject(title);
                mail.setMsg(content.toString());

                // add the attachment
                for (MailFile file : attachments) {
                    if (file != null) {
                        EmailAttachment attachment = new EmailAttachment();
                        attachment.setPath(file.getPath());
                        attachment.setDisposition(file.getDisposition());
                        attachment.setDescription(file.getDescription());
                        attachment.setName(MimeUtility.encodeText(file.getName()));

                        if (log.isDebugEnabled()) {
                            log.debug("mail.stdout.message009", file.getPath(), file.getName());
                        }

                        mail.attach(attachment);
                    }
                }

                // send the email
                String messageId = mail.send();
                if (log.isDebugEnabled()) {
                    log.debug("mail.stdout.message001");
                }
                return messageId;
            }
        } catch (Exception e) {
            throw new ModestRuntimeException("mail.stdout.message002", e);
        }
    }

    public List<Mail> search(String protocol, int port, boolean ssl, String name, SearchTerm term) {
        if (StringUtils.isBlank(protocol)) {
            protocol = "pop3";
        }

        Store store = null;
        try {
            Properties config = new Properties();
            config.setProperty("mail.debug", String.valueOf(log.isDebugEnabled()));
            config.setProperty("mail.store.protocol", protocol);
            if (port > 0) {
                config.setProperty("mail." + protocol + ".port", String.valueOf(port));
            }

            Session session = Session.getInstance(config);
            store = session.getStore();
            store.connect(this.host, this.username, this.password);

            MailFolderImpl folder = this.getFolder(store, name, term);
            assert folder != null;
            folder.setProtocol(protocol);
            folder.setProtocolPort(port);
            folder.setSSL(true);
            return folder.getMails();
        } catch (Exception e) {
            throw new ModestRuntimeException("mail.stdout.message005", e);
        } finally {
            IO.close(store);
        }
    }

    private MailFolderImpl getFolder(Store store, String name, SearchTerm term) throws MessagingException, IOException {
        Folder folder;
        if (name == null) {
            folder = this.getDefaultFolder(store.getDefaultFolder());
            name = folder.getFullName();
            if (log.isDebugEnabled()) {
                log.debug("mail.stdout.message008", name);
            }
        } else {
            folder = store.getFolder(name);
        }

        if (folder == null) {
            return null;
        }
        if (term == null) {
            term = new DefaultSearchTerm();
        }

        try {
            folder.open(Folder.READ_ONLY);

            MailFolderImpl mailFolder = new MailFolderImpl();
            mailFolder.setName(name);
            mailFolder.setNewMailCount(folder.getNewMessageCount());
            mailFolder.setUnreadMailCount(folder.getUnreadMessageCount());

            List<Mail> mails = new ArrayList<Mail>();
            Message[] messages = folder.search(term);
            for (Message message : messages) {
                Mail mail = this.toMail(message, mailFolder);
                mails.add(mail);
            }
            mailFolder.setList(mails);
            return mailFolder;
        } finally {
            if (folder.isOpen()) {
                folder.close(false);
            }
        }
    }

    public Folder getDefaultFolder(Folder folder) throws MessagingException {
        if (folder.exists()) {
            try {
                Folder[] list = folder.list();
                for (Folder file : list) {
                    if (file != null) {
                        return file;
                    }
                }
            } catch (Exception e) {
                String msg = StringUtils.toString(e);
                if (!msg.contains("not a directory")) {
                    throw new MessagingException(folder.getFullName(), e);
                }
            }
        }
        return folder;
    }

    private Mail toMail(Message msg, MailFolder mailFolder) throws MessagingException, IOException {
        MailImpl mail = new MailImpl();
        mail.setFolder(mailFolder);
        mail.setFolderIndex(msg.getMessageNumber());
        mail.setId(StringUtils.toString(msg.getMessageNumber()));
        mail.setTitle(msg.getSubject());

        String[] from = toString(msg.getFrom());
        if (!ArrayUtils.isEmpty(from)) {
            String[] address = toAddress(from[0]);
            mail.setSenderName(address[0]);
            mail.setSenderAddress(address[1]);
        }

        List<String> receiverNames = new ArrayList<String>();
        List<String> receiverAddress = new ArrayList<String>();
        String[] allRecipients = toString(msg.getAllRecipients());
        for (String add : allRecipients) {
            String[] address = toAddress(add);
            receiverNames.add(address[0]);
            receiverAddress.add(address[1]);
        }

        mail.setReceiverNames(receiverNames);
        mail.setReceiverAddress(receiverAddress);
        mail.setSendTime(msg.getSentDate());
        mail.setReceivedTime(msg.getReceivedDate());
        mail.setAttachments(this.toMailAttachments(msg, mail));
        this.setMailContent(msg, mail);
        mail.setNew(this.isNew(msg));
        mail.setHasRead(this.hasRead(msg));
        return mail;
    }

    private List<MailAttachment> toMailAttachments(Message message, Mail mail) throws IOException, MessagingException {
        List<MailAttachment> attachements = new ArrayList<MailAttachment>();
        if (message.getContent() instanceof Multipart) {
            Multipart content = (Multipart) message.getContent();
            for (int j = 0; j < content.getCount(); j++) {
                BodyPart part = content.getBodyPart(j);

                if (part.getDisposition() != null) {
                    String filename = part.getFileName();
                    if (StringUtils.isBlank(filename)) {
                        continue;
                    }

                    if (filename.startsWith("=?")) {
                        filename = MimeUtility.decodeText(filename);
                    }

                    MailAttachmentImpl obj = new MailAttachmentImpl();
                    obj.setMail(mail);
                    obj.setName(filename);
                    obj.setDescription(part.getDescription());
                    attachements.add(obj);
                }
            }
        }
        return attachements;
    }

    private boolean hasRead(Message msg) {
        try {
            return msg.getFlags().contains(Flags.Flag.SEEN);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    private boolean isNew(Message msg) throws MessagingException {
        boolean isnew = false;
        Flags flags = msg.getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        for (Flags.Flag value : flag) {
            if (value == Flags.Flag.SEEN) {
                isnew = true;
                break;
            }
        }
        return isnew;
    }

    private void setMailContent(Part part, MailImpl mail) {
        try {
            String type = part.getContentType();
            int index = type.indexOf("name");
            boolean name = index != -1;

            if (part.isMimeType("text/plain") && !name) {
                mail.setText((String) part.getContent());
            } else if (part.isMimeType("text/html") && !name) {
                mail.setHtml((String) part.getContent());
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    this.setMailContent(multipart.getBodyPart(i), mail);
                }
            } else if (part.isMimeType("message/rfc822")) {
                this.setMailContent((Part) part.getContent(), mail);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public File download(MailAttachment attachment, File parent) {
        if (attachment == null) {
            return null;
        }

        Mail mail = attachment.getMail();
        int messageId = mail.getFolderIndex();
        MailFolder mailFolder = mail.getFolder();
        String folderName = mailFolder.getName();

        Store store = null;
        try {
            Properties config = new Properties();
            config.setProperty("mail.debug", String.valueOf(log.isDebugEnabled()));
            config.setProperty("mail.store.protocol", mailFolder.getProtocol());
            // mail.transport.protocol

            Session session = Session.getInstance(config);
            store = session.getStore();
            store.connect(this.host, this.username, this.password);

            Folder folder = store.getFolder(folderName);
            if (folder == null) {
                if (log.isDebugEnabled()) {
                    log.debug("mail.stdout.message007", folderName);
                }
                return null;
            }

            try {
                folder.open(Folder.READ_ONLY);
                Message message = folder.getMessage(messageId);
                if (message == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("mail.stdout.message006", messageId);
                    }
                    return null;
                } else {
                    return this.saveAttachment(message, parent, attachment.getName());
                }
            } finally {
                if (folder.isOpen()) {
                    folder.close(false);
                }
            }
        } catch (Exception e) {
            throw new ModestRuntimeException("mail.stdout.message005", e);
        } finally {
            IO.close(store);
        }
    }

    private File saveAttachment(Part message, File parent, String downfilename) throws MessagingException, IOException {
        if (message.getContent() instanceof Multipart) {
            Multipart content = (Multipart) message.getContent();
            for (int j = 0; j < content.getCount(); j++) {
                BodyPart part = content.getBodyPart(j);

                if (part.getDisposition() != null) {
                    String filename = part.getFileName();
                    if (filename.startsWith("=?")) {
                        filename = MimeUtility.decodeText(filename);
                    }

                    if (filename.equalsIgnoreCase(downfilename) || GPatternExpression.match(filename, downfilename)) {
                        return this.saveFile(parent, filename, part.getInputStream());
                    }
                }
            }
        }
        return null;
    }

    private File saveFile(File parent, String filename, InputStream in) {
        if (parent == null) {
            parent = FileUtils.getTempDir("mail", "attach");
        } else {
            Ensure.isTrue(parent.exists() && parent.isDirectory(), parent, filename, in);
        }

        File file = new File(parent, filename);
        if (log.isDebugEnabled()) {
            log.debug("mail.stdout.message003", file);
        }

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buf = new byte[1024];
            for (int length = in.read(buf, 0, buf.length); length != -1; length = in.read(buf, 0, buf.length)) {
                out.write(buf, 0, length);
                out.flush();
            }
            return file;
        } catch (Exception e) {
            throw new ModestRuntimeException("mail.stdout.message004", filename);
        } finally {
            IO.close(out);
        }
    }

    public static class DefaultSearchTerm extends SearchTerm {
        private final static long serialVersionUID = 1L;

        public boolean match(Message msg) {
            return msg.getMessageNumber() <= 10;
        }
    }
}
