package com.coravy.couch4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.coravy.couch4j.api.Database;
import com.coravy.couch4j.api.Database.ChangeEvent;
import com.coravy.couch4j.api.Database.ChangeListener;

@Ignore
public class ChangesNotificationTest {

    private CouchDB server;
    private Database<Document> test;

    @Before
    public final void setup() {
        server = Couch4jTest.testDbInstance();
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
        
        Thread.sleep(1000); // Wait for the _changes connection to be established...
        
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
        
        Thread.sleep(1000); // Wait for the _changes connection to be established...
        
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
        //assertEquals(7, receivedChangeEvents.size());
        for(ChangeEvent event : receivedChangeEvents) {
            assertTrue(event.getId().startsWith("test-2-"));
        }
    }
}
