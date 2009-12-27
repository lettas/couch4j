package com.coravy.couch4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.sf.json.JSONArray;

import org.junit.Before;
import org.junit.Test;

public class Couch4jTest {

    private Database test;
    
    @Before
    public void setUp() throws Exception {
        test = CouchDB.localServerInstance().getDatabase("couch4j");
        assertNotNull(test);
    }
    
    @Test
    public void testFetchView() throws Exception {
        View v = View.builder("test/t1").build();
        List<ViewResultRow> l = test.fetchView(v).getRows();
        assertNotNull(l);
        assertEquals(1, l.size());

        ViewResultRow row = l.get(0);
        assertEquals("test1", row.getId());
        
        Document d = row.getDocument();
        assertEquals("test1", d.getId());
        
        
        JSONArray ary = (JSONArray) d.get("a");
        assertEquals(1, ary.get(0));
        assertEquals(2, ary.get(1));
        assertEquals(3, ary.get(2));
        
        assertEquals("test", d.get("b"));
    }
    
    @Test
    public void testSaveDocument() throws Exception {
        Document d = test.fetchDocument("test2");
        final String rand = UUID.randomUUID().toString();
        final String key = "rand_test_str";
        d.put(key, rand);
        
        System.out.println(d.getClass());
        
        // Save
        test.saveDocument(d);
        
        // Fetch again
        d = test.fetchDocument("test2");
        assertEquals(rand, d.get(key));
    }

}
