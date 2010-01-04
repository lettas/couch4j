package com.coravy.couch4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coravy.couch4j.api.Database;

public class CouchDBTest {

    private CouchDB server;
    private Database<Document> test;

    @Before
    public void setUp() throws Exception {
        server = Couch4jTest.testDbInstance();
        test = server.getDatabase(Couch4jTest.TEST_DATABASE_NAME);
        assertNotNull(test);
    }

    @After
    public void teardown() {
        server.disconnect();
    }

    @Test
    public void testGetDatabaseReturnsSameInstance() throws Exception {
        assertSame(test, server.getDatabase(Couch4jTest.TEST_DATABASE_NAME));
    }
    
    @Test
    public void testFactoryMethod() throws Exception {
        server = CouchDB.localServerInstance();
        assertEquals(5984, server.getPort());
        assertEquals("localhost", server.getHost());
        
        CouchDB server2 = new CouchDB();
        assertEquals(server, server2);
    }
    
    
    @Test
    public void testFactoryMethodHost() throws Exception {
        server = CouchDB.serverInstance("localhost");
        assertEquals(5984, server.getPort());
    }
    
    @Test
    public void testFactoryMethodsHostPort() throws Exception {
        server = CouchDB.serverInstance("localhost", 1234);
    }

}
