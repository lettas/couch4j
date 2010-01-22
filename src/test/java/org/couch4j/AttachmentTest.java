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

import java.io.InputStream;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AttachmentTest extends Couch4jBase {

    private Database test;
    private Database empty;

    public AttachmentTest(CouchDbClient server) {
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
    public void documentWithoutAttachment() throws Exception {
        Document d = new Document();
        assertThat(d.getAttachmentNames().size(), is(0));
    }

    @Test
    public void addAttachment() throws Exception {
        final String name = "java-log.png";
        Document d = new Document();
        InputStream is = testInputStream();
        d.addAttachment(name, is);
        assertThat(d.getAttachmentNames().size(), is(1));
        assertThat(d.getAttachmentNames().iterator().next(), is(name));
    }

    @Test
    public void addAttachmentToJson() throws Exception {
        final String name = "java-log.png";
        Document d = new Document();
        InputStream is = testInputStream();
        d.addAttachment(name, is);
        
        empty.saveDocument(d);
        
        Document d2 = empty.fetchDocument(d.getId());
        Attachment a = d2.getAttachment(name);        
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/png"));
        assertThat(a.getLength(), is(6945L));
    }

    private InputStream testInputStream() {
        return AttachmentTest.class.getResourceAsStream("/fixtures/java.png");
    }

}
