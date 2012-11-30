package net.sitina.sync;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class InboxReader {

    public static void main(String args[]) {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "jirka.sitina@gmail.com", "*****");

            Folder inbox = store.getFolder("Inbox");
            inbox = store.getFolder("Notes");

            Folder folder = store.getDefaultFolder().getFolder("IMAP File Sync");
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_MESSAGES);
            }
            folder.open(Folder.HOLDS_MESSAGES);

            Message message = new MimeMessage(session);

            Address from = new InternetAddress("jirka.sitina@gmail.com");
            Address to = new InternetAddress("jirka.sitina@gmail.com");

            message.setContent("Content of the note", "text/plain");
            message.setFrom(from);
            message.setRecipient(Message.RecipientType.TO, to);
            message.setSubject("/Users/jirka/Documents/workspace/imap-file-sync/tmp.txt");

            inbox.appendMessages(new Message[] { message });
            inbox.open(Folder.READ_WRITE);

            // inbox.open(Folder.READ_ONLY);
            Message messages[] = inbox.getMessages();
            for (Message m : messages) {
                InternetAddress fromAddress = (InternetAddress) m.getFrom()[0];
                System.out.println(fromAddress.getAddress());

                if (fromAddress.getAddress().toString().equals("jirka.sitina@gmail.com")) {
                    m.setFlag(Flag.SEEN, true);
                    //                    Flags flags = new Flags(Flag.DRAFT);
                    //                    boolean arg2 = true;
                    //                    inbox.setFlags(new Message[] { m }, flags, arg2);
                }

            }

            folder.appendMessages(messages);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }

    }

}