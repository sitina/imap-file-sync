/**
 * Copyright (C) 2007-2012, GoodData(R) Corporation. All rights reserved.
 */
package net.sitina.sync;

import junit.framework.TestCase;

import org.junit.Before;

public class ImapProviderTest extends TestCase {

    private ImapProvider provider;
    private final String server = "server";
    private final String userName = "user";
    private final String password = "password";
    private final String folder = "folder";

    @Before
    public void setup() {
        provider = new ImapProvider(server, userName, password, folder);
    }

    public void testAAA() {

    }

}
