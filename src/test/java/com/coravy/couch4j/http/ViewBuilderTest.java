package com.coravy.couch4j.http;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coravy.couch4j.View;


public class ViewBuilderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testViewBuilder() {
        assertEquals("_all_docs", View.builder("_all_docs").toString());
    }

    @Test
    public final void testDocument() {
        assertEquals("_design/design/_view/test", View.builder("test").document("design").toString());
    }
    
    @Test
    public final void testDocumentCombined() {
        assertEquals("_design/design/_view/test", View.builder("design/test").toString());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public final void testDocumentCombinedInvalid() {
        View.builder("design/test/abc");
    }

    @Test
    public final void testKey() {
        final String key = "/web/test.jsp";
        View v = View.builder("design/test").key(key).build();
        assertEquals("_design/design/_view/test?key=%22%2Fweb%2Ftest.jsp%22", v.queryString());
    }
//
//    @Test
//    public final void testEndkey() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testDescending() {
//        fail("Not yet implemented");
//    }

    @Test
    public final void testIncludeDocs() {
        assertEquals("_all_docs?include_docs=true", View.builder("_all_docs").includeDocs(true).toString());
    }

//    @Test
//    public final void testGroup() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testUpdate() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testSkip() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testCount() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testStartkeyString() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testStartkeyStringArray() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testStartkeyDocid() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public final void testToString() {
//        fail("Not yet implemented");
//    }

}
