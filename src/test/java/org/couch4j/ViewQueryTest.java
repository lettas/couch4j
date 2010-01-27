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
public class ViewQueryTest {

    @Test
    public final void testViewBuilder() {
        assertThat(new ViewQuery("_all_docs").toString(), is("_all_docs"));
    }

    @Test
    public final void testViewBuilderName() {
        assertThat(new ViewQuery().name("_all_docs").toString(), is("_all_docs"));
    }

    @Test
    public final void testDocument() {
        assertThat(ViewQuery.builder("test").document("design").toString(), is("_design/design/_view/test"));
    }

    @Test
    public final void testDocumentCombined() {
        assertThat(ViewQuery.builder("design/test").toString(), is("_design/design/_view/test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDocumentCombinedInvalid() {
        ViewQuery.builder("design/test/abc");
    }

    @Test
    public final void testCombined() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").key(key).count(4).skip(10);
        assertThat(v.queryString(), is("_design/design/_view/test?key=%22%2Fweb%2Ftest.jsp%22&count=4&skip=10"));
    }
    
    @Test
    public final void testKey() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").key(key);
        assertThat(v.queryString(), is("_design/design/_view/test?key=%22%2Fweb%2Ftest.jsp%22"));
    }

    @Test
    public final void testKeyVarArgs() {
        ViewQuery v = ViewQuery.builder("design/test").key("de", "/about/");
        assertThat(v.queryString(), is("_design/design/_view/test?key=[%22de%22,%22%2Fabout%2F%22]"));
    }

    //
    @Test
    public final void testEndkey() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").endkey(key);
        assertThat(v.queryString(), is("_design/design/_view/test?endkey=%22%2Fweb%2Ftest.jsp%22"));
    }

    @Test
    public final void testDescendingTrue() {
        ViewQuery v = ViewQuery.builder("design/test").descending(true);
        assertThat(v.queryString(), is("_design/design/_view/test?descending=true"));
    }

    @Test
    public final void testDescendingFalse() {
        ViewQuery v = ViewQuery.builder("design/test").descending(false);
        assertThat(v.queryString(), is("_design/design/_view/test?descending=false"));
    }

    @Test
    public final void testStale() {
        ViewQuery v = ViewQuery.builder("design/test").stale(true);
        assertThat(v.queryString(), is("_design/design/_view/test?stale=ok"));
    }

    @Test
    public final void testReduceTrue() {
        ViewQuery v = ViewQuery.builder("design/test").reduce(true);
        assertThat(v.queryString(), is("_design/design/_view/test?reduce=true"));
    }

    @Test
    public final void testReduceFalse() {
        ViewQuery v = ViewQuery.builder("design/test").reduce(false);
        assertThat(v.queryString(), is("_design/design/_view/test?reduce=false"));
    }

    @Test
    public final void testInclusiveEndTrue() {
        ViewQuery v = ViewQuery.builder("design/test").inclusiveEnd(true);
        assertThat(v.queryString(), is("_design/design/_view/test?inclusive_end=true"));
    }

    @Test
    public final void testInclusiveEndFalse() {
        ViewQuery v = ViewQuery.builder("design/test").inclusiveEnd(false);
        assertThat(v.queryString(), is("_design/design/_view/test?inclusive_end=false"));
    }

    @Test
    public final void testIncludeDocs() {
        assertThat(ViewQuery.builder("_all_docs").includeDocs(true).toString(), is("_all_docs?include_docs=true"));
    }

    @Test
    public final void testGroupTrue() {
        ViewQuery v = ViewQuery.builder("design/test").group(true);
        assertThat(v.queryString(), is("_design/design/_view/test?group=true"));
    }

    @Test
    public final void testGroupFalse() {
        ViewQuery v = ViewQuery.builder("design/test").group(false);
        assertThat(v.queryString(), is("_design/design/_view/test?group=false"));
    }
    
    @Test
    public final void testGroupLevel() {
        ViewQuery v = ViewQuery.builder("design/test").groupLevel(3);
        assertThat(v.queryString(), is("_design/design/_view/test?group_level=3"));
    }

    @Test
    public final void testUpdateTrue() {
        ViewQuery v = ViewQuery.builder("design/test").update(true);
        assertThat(v.queryString(), is("_design/design/_view/test?update=true"));
    }

    @Test
    public final void testUpdateFalse() {
        ViewQuery v = ViewQuery.builder("design/test").update(false);
        assertThat(v.queryString(), is("_design/design/_view/test?update=false"));
    }

    @Test
    public final void testSkip() {
        ViewQuery v = ViewQuery.builder("design/test").skip(10);
        assertThat(v.queryString(), is("_design/design/_view/test?skip=10"));
    }

    @Test
    public final void testCount() {
        ViewQuery v = ViewQuery.builder("design/test").count(13);
        assertThat(v.queryString(), is("_design/design/_view/test?count=13"));
    }

    @Test
    public final void testStartkeyString() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").startkey(key);
        assertThat(v.queryString(), is("_design/design/_view/test?startkey=%22%2Fweb%2Ftest.jsp%22"));
    }

    @Test
    public final void testStartkeyStringArray() {
        ViewQuery v = ViewQuery.builder("design/test").startkey("1", "2");
        assertThat(v.queryString(), is("_design/design/_view/test?startkey=[%221%22,%222%22]"));
    }

    @Test
    public final void testEndkeyString() {
        final String key = "/web/test.jsp";
        ViewQuery v = ViewQuery.builder("design/test").endkey(key);
        assertThat(v.queryString(), is("_design/design/_view/test?endkey=%22%2Fweb%2Ftest.jsp%22"));
    }

    @Test
    public final void testEndkeyStringArray() {
        ViewQuery v = ViewQuery.builder("design/test").endkey("1", "2");
        assertThat(v.queryString(), is("_design/design/_view/test?endkey=[%221%22,%222%22]"));
    }

    @Test
    public final void testStartkeyDocid() {
        ViewQuery v = ViewQuery.builder("design/test").startkeyDocid("2");
        assertThat(v.queryString(), is("_design/design/_view/test?startkey_docid=%222%22"));
    }
    
    @Test
    public final void testEndkeyDocid() {
        ViewQuery v = ViewQuery.builder("design/test").endkeyDocid("2");
        assertThat(v.queryString(), is("_design/design/_view/test?endkey_docid=%222%22"));
    }

    @Test
    public final void testToString() {
        ViewQuery v = ViewQuery.builder("design/test").count(13);
        assertThat(v.queryString(), is("_design/design/_view/test?count=13"));
        assertThat(v.queryString(), is(v.toString()));
    }

}
