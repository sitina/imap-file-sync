package net.sitina.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.imap.IMAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImapProvider {

    private static final String STORE = "imaps";
    private final String server;
    private final String userName;
    private final String password;
    private final String folderName;
    private final String localFolder;

    private static final Logger log = LoggerFactory.getLogger(ImapProvider.class);

    public ImapProvider(String server, String userName, String password, String folder, String localFolder) {
        this.server = server;
        this.userName = userName;
        this.password = password;
        this.folderName = folder;
        this.localFolder = localFolder;
    }

    public List<ImapSyncFile> getFiles() {
        List<ImapSyncFile> result = new ArrayList<ImapSyncFile>();
        Store store = null;

        try {
            final Session session = getSession();
            store = getStore(session);
            Folder folder = store.getDefaultFolder().getFolder(folderName);
            openFolder(folder);

            Message[] msgs = folder.getMessages();

            for (Message msg : msgs) {
                result.add(createImapSyncFile(msg));
            }
        } catch (MessagingException | IOException e) {
            log.error("Problem fetching content of messages folder.", e);
        } finally {
            closeStore(store);
        }

        return result;
    }

    public void updateMessage(ImapSyncFile message) {
        Store store = null;

        try {
            final Session session = getSession();
            store = getStore(session);
            Folder folder = store.getDefaultFolder().getFolder(folderName);
            openFolder(folder);

            Message[] msgs = folder.getMessages();

            for (Message msg : msgs) {
                if (msg.getSubject().equals(message.getName())) {
                    msg.setFlag(Flags.Flag.DELETED, true);

                    Message newMsg = new MimeMessage(session);

                    Address from = new InternetAddress(userName);
                    Address to = new InternetAddress(userName);

                    newMsg.setContent(message.getValue(), "text/plain");
                    newMsg.setFrom(from);
                    newMsg.setRecipient(Message.RecipientType.TO, to);
                    newMsg.setSubject(message.getSubject());
                    newMsg.setFlag(Flags.Flag.SEEN, true);

                    folder.appendMessages(new Message[]{newMsg});
                }
            }
        } catch (MessagingException e) {
            log.error("Problem updating email message.", e);
        } finally {
            closeStore(store);
        }
    }

    public void deleteMessage(ImapSyncFile message) {
        Store store = null;

        try {
            final Session session = getSession();
            store = getStore(session);
            Folder folder = store.getDefaultFolder().getFolder(folderName);
            openFolder(folder);

            Message[] msgs = folder.getMessages();

            for (Message msg : msgs) {
                if (msg.getSubject().equals(message.getName())) {
                    msg.setFlag(Flags.Flag.DELETED, true);
                }
            }
        } catch (MessagingException e) {
            log.error("Problem deleting email message.", e);
        } finally {
            closeStore(store);
        }
    }

    public void createMessage(ImapSyncFile message) {
        Store store = null;

        try {
            final Session session = getSession();
            store = getStore(session);
            Folder folder = store.getDefaultFolder().getFolder(folderName);
            openFolder(folder);

            Message newMsg = new MimeMessage(session);

            Address from = new InternetAddress(userName);
            Address to = new InternetAddress(userName);

            newMsg.setContent(message.getValue(), "text/plain");
            newMsg.setFrom(from);
            newMsg.setRecipient(Message.RecipientType.TO, to);
            newMsg.setSubject(message.getSubject());
            newMsg.setFlag(Flags.Flag.SEEN, true);

            folder.appendMessages(new Message[]{newMsg});
        } catch (MessagingException e) {
            log.error("Problem uploading email message.", e);
        } finally {
            closeStore(store);
        }
    }

    public ImapSyncFile getMessage(String subject) {
        Store store = null;

        try {
            final Session session = getSession();
            store = getStore(session);
            Folder folder = store.getDefaultFolder().getFolder(folderName);
            openFolder(folder);

            Message[] msgs = folder.getMessages();

            for (Message msg : msgs) {
                if (msg.getSubject().equals(subject)) {
                    return createImapSyncFile(msg);
                }
            }
        } catch (MessagingException | IOException e) {
            log.error("Problem fetching content of the message.", e);
        } finally {
            closeStore(store);
        }

        return null;
    }

    private void openFolder(Folder folder) throws MessagingException {
        if (!folder.exists()) {
            folder.create(Folder.READ_WRITE);
        }
        folder.open(Folder.READ_WRITE);
    }

    private ImapSyncFile createImapSyncFile(Message msg) throws MessagingException, IOException {
        ImapSyncFile result = new ImapSyncFile(localFolder + msg.getSubject(), msg.getSubject());
        result.setImapUpdateTime(msg.getReceivedDate());
        result.setEmailBody(msg.getContent().toString());

        return result;
    }

    protected void closeStore(Store store) {
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
                log.error("Could not close the store in final block.", e);
            }
        }
    }

    protected Session getSession() {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", STORE);

        return Session.getDefaultInstance(props, null);
    }

    private Store getStore(final Session session) throws MessagingException {
        Store store = session.getStore(STORE);
        store.connect(server, userName, password);

        return store;
    }

}
