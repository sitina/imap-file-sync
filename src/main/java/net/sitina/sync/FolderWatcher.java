package net.sitina.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;

public class FolderWatcher implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(FolderWatcher.class);

    private final WatchService watcher;
    private final ImapProvider p;
    private final Map<String, ImapSyncFile> filesMap;
    private final String storageFolder;


    public FolderWatcher(final ImapProvider p, final WatchService watcher, final Map<String, ImapSyncFile> filesMap, final String storageFolder) {
        this.p = p;
        this.watcher = watcher;
        this.filesMap = filesMap;
        this.storageFolder = storageFolder;
    }

    public void run() {
        try {
            log.debug("Starting watching folder '{}'.", storageFolder);
            WatchKey watckKey = watcher.take();

            for (;;) {
                List<WatchEvent<?>> events = watckKey.pollEvents();
                for (WatchEvent event : events) {
                    if (!event.context().toString().startsWith(".")) {
                        log.debug("Event {} happeded, context = '{}'.", event.kind().toString(), event.context().toString());

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            ImapSyncFile file = new ImapSyncFile(storageFolder + event.context().toString(), event.context().toString());
                            p.createMessage(file);
                            log.debug("File {} stored in imap folder.", event.context().toString());
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            if (filesMap.containsKey(event.context().toString())) {
                                ImapSyncFile file = filesMap.get(event.context().toString());
                                p.deleteMessage(file);
                                log.debug("File {} deleted.", event.context().toString());
                            }
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            if (filesMap.containsKey(event.context().toString())) {
                                ImapSyncFile file = filesMap.get(event.context().toString());
                                p.updateMessage(file);
                                log.debug("File {} updated.", event.context().toString());
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("The execution has been interrupted.", e);
        }

    }

}
