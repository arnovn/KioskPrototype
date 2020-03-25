package com.example.kioskprototype.MailSender;

import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class in charge of sending mail using GMAIL
 */
public class GmailSender extends Authenticator {

    /**
     * User of the mail address
     */
    private String user;

    /**
     * Password for logging into the GMail account
     */
    private String password;

    /**
     * Used to get communication with a secure element
     */
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    /**
     * Constructor of the GMail sender
     * @param user
     *              Username of the GMail account
     * @param password
     *              Password of the GMail account
     */
    public GmailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        String mailhost = "smtp.gmail.com";
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    /**
     * Repository for the username and password
     * @return
     *          The repository
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    /**
     * Method in charge of sending the mail.
     * @param subject
     *              Subject of the mail
     * @param body
     *              Body of the mail
     * @param sender
     *              Sender of the mail
     * @param recipients
     *              Recipients of the mail
     * @throws Exception
     *              Exception when something goes wrong
     */
    public synchronized void sendMail(String subject, String body,
                                      String sender, String recipients) throws Exception {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        Transport.send(message);
    }
}
