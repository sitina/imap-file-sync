package net.sitina.sync;

import com.sun.mail.imap.IMAPMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

public class InboxReader {

    public static void main(String args[]) {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "jirka.sitina@gmail.com", "*****");
            System.out.println(store);

            Folder inbox = store.getFolder("Inbox");

            inbox = store.getFolder("Notes");

            Message m = new MIMEMessage(session);

            Address a = new InternetAddress("a@a.com", "A a");
            Address b = new InternetAddress("fake@java2s.com");

            m.setContent("Mail content", "text/plain");
            m.setFrom(a);
            m.setRecipient(Message.RecipientType.TO, b);
            m.setSubject("subject");

            inbox.appendMessages(new Message[]{m});

            inbox.open(Folder.READ_ONLY);
            Message messages[] = inbox.getMessages();
            for(Message message:messages) {
                InternetAddress from = (InternetAddress)message.getFrom()[0];
                System.out.println(from.getAddress());
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }

    }

}