/*
 * The MIT License
 *
 * Copyright (c) 2009, 2010 Stefan Saasen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.coravy.couch4j.couchdb0_10_1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.coravy.couch4j.Couch4jBase;
import com.coravy.couch4j.CouchDbClient;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.Database.ChangeEvent;
import com.coravy.couch4j.Database.ChangeListener;

@Ignore
// FIXME add this back in
public class ChangesNotificationTest extends Couch4jBase {

    private Database test;

    public ChangesNotificationTest(CouchDbClient server) {
        super(server);
    }

    @Before
    public final void setup() {
        test = server.getDatabase("couch4j-changes");
        assertNotNull(test);

        test.saveDocument(new Document("test-000"));
        test.saveDocument(new Document("test-001"));
        test.saveDocument(new Document("test-002"));
    }

    @After
    public final void teardown() {
        test.delete();
        server.disconnect();
    }

    @Test
    public void testReceiveChangesNotifications() throws Exception {
        final List<ChangeEvent> receivedChangeEvents = new ArrayList<ChangeEvent>();

        test.addChangeListener(new ChangeListener() {
            public void onChange(ChangeEvent event) {
                receivedChangeEvents.add(event);
            }
        });

        Thread.sleep(1000); // Wait for the _changes connection to be
        // established...

        final int NEW = 5;
        for (int i = 0; i < NEW; i++) {
            test.saveDocument(new Document("test-" + i));
        }
        Thread.sleep(1000);
        assertEquals(NEW, receivedChangeEvents.size());
    }

    @Test
    public void testUnsubscribeFromChangesNotifications() throws Exception {
        // Before
        for (int i = 0; i < 3; i++) {
            test.saveDocument(new Document("test-1-" + i));
        }

        final List<ChangeEvent> receivedChangeEvents = new ArrayList<ChangeEvent>();
        final ChangeListener l = new ChangeListener() {
            public void onChange(ChangeEvent event) {
                receivedChangeEvents.add(event);
            }
        };

        test.addChangeListener(l);

        Thread.sleep(1000); // Wait for the _changes connection to be
        // established...

        // While subscribed
        for (int i = 0; i < 7; i++) {
            test.saveDocument(new Document("test-2-" + i));
        }

        test.removeChangeListener(l);

        // After unsubscribing
        for (int i = 0; i < 11; i++) {
            test.saveDocument(new Document("test-3-" + i));
        }

        Thread.sleep(1000);

        // We should have received 7 notifications...
        // assertEquals(7, receivedChangeEvents.size());
        for (ChangeEvent event : receivedChangeEvents) {
            assertTrue(event.getId().startsWith("test-2-"));
        }
    }
}
