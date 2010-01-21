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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DocumentTest extends Couch4jBase {

    private Database test;
    private Database empty;

    public DocumentTest(CouchDbClient server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        test = server.getDatabase("couch4j");
        assertNotNull(test);
        empty = server.getDatabase("couch4j-" + UUID.randomUUID().toString().substring(0, 6));
        assertNotNull(empty);
    }

    @After
    public void teardown() {
        empty.delete();
    }

    @Test
    public void emptyDocumentHasNoIdAndRev() throws Exception {
        Document d = new Document();
        assertNull(d.getId());
        assertNull(d.getRev());
    }

    @Test
    public void documentToJson() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = JSONObject.fromObject(d.toJson());
        assertThat(d.toJson(), is(json.toString()));
    }

    @Test
    public void documentToJSONObject() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = d.toJSONObject();
        assertEquals(d.getId(), json.getString("_id"));
        assertEquals(d.getRev(), json.getString("_rev"));
    }

    @Test
    public void documentConstructorMap() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("k1", "value1");
        Document d = new Document(map);
        assertNotNull(d);
    }

    @Test
    public void documentEmptyId() throws Exception {
        Document d = new Document();
        empty.saveDocument(d);

        assertNotNull(d.getId());
        assertTrue(d.getId().length() > 10);
    }

    @Test
    public void documentPutAll() throws Exception {
        Document d = new Document();
        Map<String, String> map = new HashMap<String, String>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        map.put("k3", "v3");
        d.putAll(map);
        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(d.getId());

        assertThat(d.getId(), is(d2.getId()));
        assertThat((String) d2.get("k2"), is("v2"));
    }

    @Test
    public void emptyDocumentMap() throws Exception {
        Document d = new Document();

        d.put("k1", "v1");
        assertThat((String) d.get("k1"), is("v1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void emptyDocumentAttributes() throws Exception {
        Document d = new Document();

        d.put("k1", "v1");
        d.put("k2", "v2");

        Map map = new HashMap();
        map.put("k1", "v1");
        map.put("k2", "v2");

        assertThat(d.getAttributes(), is(map));
    }

    @Test
    public void emptyDocumentRevision() throws Exception {
        Document d = new Document();
        assertNotNull(d.getRevisions());
        assertTrue(d.getRevisions().isEmpty());
    }

}
