package apps.dabinu.com.tourapp.utils;

import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailSender {
    private Session session = null;
    private String user_email, password, rec_email, subject, message_body;

    public GmailSender(String rec_email, String subject, String message_body){
        this.user_email = "Rex.mytour@gmail.com";
        this.password = "Rexopakt_Solutions";
        this.rec_email = rec_email;
        this.subject = subject;
        this.message_body = message_body;

        String mail_host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", mail_host);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator()  {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user_email, password);
            }
        });

        new ReceiveFeedback().execute();
    }

    private class ReceiveFeedback extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user_email));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec_email));
                message.setSubject(subject);
                message.setContent(message_body, "text/html; charset=utf-8");

                Transport.send(message);

            }catch (MessagingException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
