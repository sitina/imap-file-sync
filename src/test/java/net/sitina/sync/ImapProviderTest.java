/**
 * Copyright (C) 2007-2012, GoodData(R) Corporation. All rights reserved.
 */
package net.sitina.sync;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class ImapProviderTest extends TestCase {

    private ImapProvider provider;

    @Before
    public void setup() throws Exception {
        provider = getProvider();
    }

    @Test
    public void testGetMessages() throws Exception {
        provider = getProvider();
        assertNotNull(provider);

        List<ImapSyncFile> files = provider.getFiles();
        assertNotNull(files);
        // assertEquals(1, files.size()); this depends on the state of server
    }

    public void testGetSingleMessage() throws Exception {
        provider = getProvider();
        assertNotNull(provider);

        ImapSyncFile message = provider.getMessage("tmp.txt");

        assertNotNull(message);
        assertTrue(message.getName().endsWith("tmp.txt"));
    }

    public void testUpdateMessage() throws Exception {
        provider = getProvider();
        assertNotNull(provider);

        ImapSyncFile message = provider.getMessage("tmp.txt");
        assertNotNull(message);
        assertTrue(message.getName().endsWith("tmp.txt"));

        String text = "Updated message " + System.currentTimeMillis();


        message.setValue(text);
        provider.updateMessage(message);

        ImapSyncFile updatedMessage = provider.getMessage("tmp.txt");
        assertNotNull(updatedMessage);
        assertEquals(text.trim(), updatedMessage.getEmailBody().trim());
    }

    private ImapProvider getProvider() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("test.properties")));

        final String folder = properties.getProperty("folder");
        final String password = properties.getProperty("password");
        final String username = properties.getProperty("username");
        final String server = properties.getProperty("server", "imap.gmail.com");
        final String storageFolder = properties.getProperty("storageFolder");

        return new ImapProvider(server, username, password, folder, storageFolder);
    }

}
