package net.sitina.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class ImapProvider {

    private static final String STORE = "imaps";
    private final String server;
    private final String userName;
    private final String password;
    private final String folder;

    public ImapProvider(String server, String userName, String password, String folder) {
        this.server = server;
        this.userName = userName;
        this.password = password;
        this.folder = folder;
    }

    public List<ImapSyncFile> getFiles() {
        List<ImapSyncFile> result = new ArrayList<ImapSyncFile>();

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", STORE);

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore(STORE);
            store.connect(server, userName, password);

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

    public void updateMessage(ImapSyncFile message) {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", STORE);

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore(STORE);
            store.connect(server, userName, password);

            Folder folder = store.getDefaultFolder().getFolder("IMAP File Sync");
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_MESSAGES);
            }
            folder.open(Folder.HOLDS_MESSAGES);

            Message[] msgs = folder.getMessages();

            for (Message msg : msgs) {
                if (msg.getSubject().equals(message.getName())) {
                    msg.setText(message.getValue());
                }
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
    }

}
