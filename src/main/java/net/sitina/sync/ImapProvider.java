package net.sitina.sync;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ImapProvider {

    public List<ImapSyncFile> getFiles() {
        List<ImapSyncFile> result = new ArrayList<ImapSyncFile>();

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "jirka.sitina@gmail.com", "******");

            Folder folder = store.getDefaultFolder().getFolder("IMAP File Sync");
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_MESSAGES);
            }
            folder.open(Folder.HOLDS_MESSAGES);

            Message[] msgs = folder.getMessages();

            for (Message msg : msgs) {
                ImapSyncFile f = new ImapSyncFile(msg.getSubject());
                f.setImapUpdateTime(msg.getReceivedDate());
                f.setEmailBody(msg.getContent().toString());
                result.add(f);
            }

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
