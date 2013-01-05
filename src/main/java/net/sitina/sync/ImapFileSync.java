package net.sitina.sync;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImapFileSync {

    private static final Logger log = LoggerFactory.getLogger(ImapFileSync.class);

    public static void main( String[] args ) throws Exception {

        args = new String[]{"test.properties"};

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(args[0])));

        final String folder = properties.getProperty("folder");
        final String password = properties.getProperty("password");
        final String username = properties.getProperty("username");
        final String server = properties.getProperty("server", "imap.gmail.com");
        final String storageFolder = properties.getProperty("storageFolder");

        ImapProvider p = new ImapProvider(server, username, password, folder, storageFolder);

        final List<ImapSyncFile> files = p.getFiles();

        final Map<String, ImapSyncFile> filesMap = new HashMap<String, ImapSyncFile>();

        File storageFolderFile = new File(storageFolder);
        if (!storageFolderFile.exists()) {
            storageFolderFile.mkdirs();
        }

        for (ImapSyncFile f : files) {
            filesMap.put(f.getName(), f);

            // file exists only in email
            if (!f.exists()) {
                log.debug("Creating new file {}.", f.getName());
                try {
                    f.createNewFile();
                    BufferedWriter out = new BufferedWriter(new FileWriter(f));
                    out.write(f.getEmailBody());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            // file is older in folder
            } else if (f.getFileSystemUpdateTime().getTime() < f.getImapUpdateTime().getTime()) {

                if (!f.getValue().trim().equals(f.getEmailBody().trim())) {
                    log.debug("Overwriting old file with email's content (file {}).", f.getName());
                    f.setValue(f.getEmailBody());
                }
            // file is older in email
            } else if (f.getFileSystemUpdateTime().getTime() < f.getImapUpdateTime().getTime()) {
                log.debug("Content of the folder is newer, overwriting email's content (file {}).", f.getName());
                p.updateMessage(f);
            }

        }

        FileSystem fileSystem = FileSystems.getDefault();
        WatchService watcher = fileSystem.newWatchService();
        Path myDir = fileSystem.getPath(storageFolder);
        myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,  StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

        FolderWatcher folderWatcher = new FolderWatcher(p, watcher, filesMap, storageFolder);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(folderWatcher);

            // read IMAP folder and look for files
            // check that are those files in sync (eg. look at the files, load their meta and copy newer version from/to server depending on the latest date); create missing files

            // wait for a while

    }
}
