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
package org.couch4j;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Stefan Saasen
 */
public class ViewBuilderTest {

    @Test
    public final void testViewBuilder() {
        assertThat("_all_docs", is(new ViewQuery("_all_docs").toString()));
    }

    @Test
    public final void testViewBuilderName() {
        assertThat("_all_docs", is(new ViewQuery().name("_all_docs").toString()));
    }
    
    @Test
    public final void testDocument() {
        assertThat("_design/design/_view/test", is(ViewQuery.builder("test").document("design").toString()));
    }

    @Test
    public final void testDocumentCombined() {
        assertThat("_design/design/_view/test", is(ViewQuery.builder("design/test").toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDocumentCombinedInvalid() {
        ViewQuery.builder("design/test/abc");
    }

    @Test
    public final void testKey() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").key(key);
        assertThat("_design/design/_view/test?key=%22%2Fweb%2Ftest.jsp%22", is(v.queryString()));
    }

    //
    @Test
    public final void testEndkey() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").endkey(key);
        assertThat("_design/design/_view/test?endkey=%22%2Fweb%2Ftest.jsp%22", is(v.queryString()));
    }

    @Test
    public final void testDescendingTrue() {
        ViewQuery v = ViewQuery.builder("design/test").descending(true);
        assertThat("_design/design/_view/test?descending=true", is(v.queryString()));
    }

    @Test
    public final void testDescendingFalse() {
        ViewQuery v = ViewQuery.builder("design/test").descending(false);
        assertThat("_design/design/_view/test?descending=false", is(v.queryString()));
    }

    @Test
    public final void testIncludeDocs() {
        assertThat("_all_docs?include_docs=true", is(ViewQuery.builder("_all_docs").includeDocs(true).toString()));
    }

    @Test
    public final void testGroupTrue() {
        ViewQuery v = ViewQuery.builder("design/test").group(true);
        assertThat("_design/design/_view/test?group=true", is(v.queryString()));
    }

    @Test
    public final void testGroupFalse() {
        ViewQuery v = ViewQuery.builder("design/test").group(false);
        assertThat("_design/design/_view/test?group=false", is(v.queryString()));
    }

    @Test
    public final void testUpdateTrue() {
        ViewQuery v = ViewQuery.builder("design/test").update(true);
        assertThat("_design/design/_view/test?update=true", is(v.queryString()));
    }

    @Test
    public final void testUpdateFalse() {
        ViewQuery v = ViewQuery.builder("design/test").update(false);
        assertThat("_design/design/_view/test?update=false", is(v.queryString()));
    }

    @Test
    public final void testSkip() {
        ViewQuery v = ViewQuery.builder("design/test").skip(10);
        assertThat("_design/design/_view/test?skip=10", is(v.queryString()));
    }

    @Test
    public final void testCount() {
        ViewQuery v = ViewQuery.builder("design/test").count(13);
        assertThat("_design/design/_view/test?count=13", is(v.queryString()));
    }

    @Test
    public final void testStartkeyString() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").startkey(key);
        assertThat("_design/design/_view/test?startkey=%22%2Fweb%2Ftest.jsp%22", is(v.queryString()));
    }

//    @Test
//    public final void testStartkeyStringArray() {
//        View v = View.builder("design/test").startkey("1", "2");
//        assertThat("_design/design/_view/test?startkey=[%221%22,%222%22]", is(v.queryString()));
//    }

    // @Test
    // public final void testStartkeyDocid() {
    // fail("Not yet implemented");
    // }
    @Test
    public final void testToString() {
        ViewQuery v = ViewQuery.builder("design/test").count(13);
        assertThat("_design/design/_view/test?count=13", is(v.toString()));
        assertThat(v.queryString(), is(v.toString()));
    }

}
