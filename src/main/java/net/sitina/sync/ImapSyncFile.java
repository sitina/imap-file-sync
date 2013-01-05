package net.sitina.sync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImapSyncFile extends File {


    private static final long serialVersionUID = 3746874166888787764L;
    private static final String LINE_SEPARATOR = "line.separator";
    private static final Logger log = LoggerFactory.getLogger(ImapSyncFile.class);
    private final String subject;

    public ImapSyncFile(String pathname,  String subject) {
        super(pathname);
        this.subject = subject;
    }

    private Date imapUpdateTime;
    private String emailBody;

    public Date getImapUpdateTime() {
        return imapUpdateTime;
    }

    public void setImapUpdateTime(Date imapUpdateTime) {
        this.imapUpdateTime = imapUpdateTime;
    }

    public Date getFileSystemUpdateTime() {
        return new Date(this.lastModified());
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getValue() {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this));

            String line;
            String ls = System.getProperty(LINE_SEPARATOR);

            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append(ls);
            }
        } catch (IOException e) {
            log.error("Problem reading contents of the file", e);
        }
        return result.toString();
    }

    public void setValue(String value) {
        try {
            if (!this.exists()) {
                this.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(this));
            out.write(value);
            out.close();
        } catch (IOException e) {
            log.error("Problem writing value into the file", e);
        }
    }

    public String getSubject() {
        return subject;
    }

}
