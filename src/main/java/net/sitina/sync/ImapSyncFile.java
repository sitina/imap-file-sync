package net.sitina.sync;

import java.io.File;
import java.util.Date;

public class ImapSyncFile extends File {

    private static final long serialVersionUID = 3746874166888787764L;

    public ImapSyncFile(String pathname) {
        super(pathname);
    }

    private Date imapUpdateTime;
    private Date fileSystemUpdateTime;
    private String emailBody;

    public Date getImapUpdateTime() {
        return imapUpdateTime;
    }

    public void setImapUpdateTime(Date imapUpdateTime) {
        this.imapUpdateTime = imapUpdateTime;
    }

    public Date getFileSystemUpdateTime() {
        return fileSystemUpdateTime;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getValue() {
        // TODO: read value of the file we are speaking about
        return null;
    }

}
