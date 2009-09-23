package com.coravy.couch4j.http;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ViewBuilderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testViewBuilder() {
        assertEquals("_all_docs", new ViewBuilder("_all_docs").toString());
    }

    @Test
    public final void testDocument() {
        assertEquals("_design/design/_view/test", new ViewBuilder("test").document("design").toString());
    }
    
    @Test
    public final void testDocumentCombined() {
        assertEquals("_design/design/_view/test", new ViewBuilder("design/test").toString());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public final void testDocumentCombinedInvalid() {
        new ViewBuilder("design/test/abc");
    }

//    @Test
//    public final void testKey() {
//        fail("Not yet implemented");
//    }
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
        assertEquals("_all_docs?include_docs=true", new ViewBuilder("_all_docs").includeDocs(true).toString());
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
