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
package com.coravy.couch4j;

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
        assertEquals("_all_docs", new View("_all_docs").toString());
    }

    @Test
    public final void testDocument() {
        assertEquals("_design/design/_view/test", View.builder("test").document("design").toString());
    }

    @Test
    public final void testDocumentCombined() {
        assertEquals("_design/design/_view/test", View.builder("design/test").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDocumentCombinedInvalid() {
        View.builder("design/test/abc");
    }

    @Test
    public final void testKey() {
        final String key = "/web/test.jsp";
        View v = View.builder("design/test").key(key);
        assertEquals("_design/design/_view/test?key=%22%2Fweb%2Ftest.jsp%22", v.queryString());
    }

    //
    @Test
    public final void testEndkey() {
        final String key = "/web/test.jsp";
        View v = View.builder("design/test").endkey(key);
        assertEquals("_design/design/_view/test?endkey=%22%2Fweb%2Ftest.jsp%22", v.queryString());
    }

    //
    // @Test
    // public final void testDescending() {
    // fail("Not yet implemented");
    // }

    @Test
    public final void testIncludeDocs() {
        assertEquals("_all_docs?include_docs=true", View.builder("_all_docs").includeDocs(true).toString());
    }

    // @Test
    // public final void testGroup() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public final void testUpdate() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public final void testSkip() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public final void testCount() {
    // fail("Not yet implemented");
    // }
    //
    @Test
    public final void testStartkeyString() {
        final String key = "/web/test.jsp";
        View v = View.builder("design/test").startkey(key);
        assertEquals("_design/design/_view/test?startkey=%22%2Fweb%2Ftest.jsp%22", v.queryString());
    }

    // @Test
    // public final void testStartkeyStringArray() {
    // View v = View.builder("design/test").startkey("1", "2").build();
    // assertEquals("_design/design/_view/test?startkey=[%221%22,%222%22]",
    // v.queryString());
    // }
    //
    // @Test
    // public final void testStartkeyDocid() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public final void testToString() {
    // fail("Not yet implemented");
    // }

}
