package net.sitina.sync;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.List;

public class ImapFileSync {

    public static void main( String[] args ) throws Exception {

        ImapProvider p = new ImapProvider();
        final List<ImapSyncFile> files = p.getFiles();

        FileSystem fileSystem = FileSystems.getDefault();
        WatchService watcher = fileSystem.newWatchService();

        for (ImapSyncFile f : files) {
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    BufferedWriter out = new BufferedWriter(new FileWriter(f));
                    out.write(f.getEmailBody());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (f.getFileSystemUpdateTime().getTime() < f.getImapUpdateTime().getTime()) {
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                out.write(f.getEmailBody());
                out.close();
            }

            Path myDir = fileSystem.getPath(f.getParent());
            myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,  StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        for (;;) {
            WatchKey watckKey = watcher.take();

            List<WatchEvent<?>> events = watckKey.pollEvents();
            for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("Created: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Delete: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Modify: " + event.context().toString());
                }
            }
        }

            // read IMAP folder and look for files
            // check that are those files in sync (eg. look at the files, load their meta and copy newer version from/to server depending on the latest date); create missing files

            // wait for a while

    }
}
