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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

public class DocumentTest extends Couch4jBase {

    private Database test;

    public DocumentTest(CouchDb server) {
        super(server);
    }

    @Before
    public void setUp() throws Exception {
        test = server.getDatabase("couch4j");
        assertNotNull(test);
    }

    @Test
    public void testDocumentToJson() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = JSONObject.fromObject(d.toJson());
        assertEquals(d.toJson(), json.toString());
    }

    @Test
    public void testDocumentToJSONObject() throws Exception {
        Document d = test.fetchDocument(Couch4jTest.VALID_DOC_ID);
        JSONObject json = d.toJSONObject();
        assertEquals(d.getId(), json.getString("_id"));
    }
    
    
    @Test
    public void testDocumentConstructorMap() throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        map.put("k1", "value1");
        Document d = new Document(map);
    }
}
