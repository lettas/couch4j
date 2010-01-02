package com.coravy.couch4j;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Run on the same machine
 * 
 * <p>
 * Check established connections:
 * <pre>
 * sudo netstat -tap tcp
 * </pre>
 */
public class PerfTest {
    
    @Test(timeout=7000) // ~ 2700 ms on a 3.06 Core 2 Duo
    public void fetchMultipleDocuments() throws Exception {
        Database<Document> test = CouchDB.localServerInstance().getDatabase("couch4j");
        final int UPPER = 1000;
        long start = System.currentTimeMillis();
        for (int i = 0; i <= UPPER; i++) {
            Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
            assertNotNull(d.getRev());
            assertEquals(Couch4jTest.VALID_DOC_ID, d.getId());
        }
        long duration = System.currentTimeMillis() - start;
        System.out.format("Fetching %d documents took %d ms", UPPER, duration);
    }
}
