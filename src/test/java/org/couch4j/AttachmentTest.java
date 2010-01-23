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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.couch4j.Database.StreamContext;
import org.couch4j.util.StreamUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AttachmentTest extends Couch4jBase {

    private static final long ATTACHMENT_1_FILESIZE = 6945L;
    private static final String ATTACHMENT_1_NAME = "java.png";
    private static final long ATTACHMENT_2_FILESIZE = 17424L;
    private static final String ATTACHMENT_2_NAME = "couchdb-logo.jpg";

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
        InputStream is = testInputStream(ATTACHMENT_1_NAME);
        d.addAttachment(name, is);
        assertThat(d.getAttachmentNames().size(), is(1));
        assertThat(d.getAttachmentNames().iterator().next(), is(name));
    }

    @Test
    public void addAttachmentToDocument() throws Exception {
        final String name = "java-log.png";
        Document d = new Document();
        InputStream is = testInputStream(ATTACHMENT_1_NAME);
        d.addAttachment(name, is);

        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(d.getId());
        Attachment a = d2.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/png"));
        assertThat(a.getLength(), is(ATTACHMENT_1_FILESIZE));
        
        
        a.retrieve(new StreamContext() {
            public void withInputStream(InputStream is) throws IOException {
                assertNotNull(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamUtils.copy(is, baos);
                byte[] b = baos.toByteArray();
                assertEquals(ATTACHMENT_1_FILESIZE, b.length);
                byte[] localFile = StreamUtils.toByteArray(testInputStream(ATTACHMENT_1_NAME));
                assertArrayEquals(localFile, b);
                // is will be closed automatically
            }
        });
    }

    @Test
    public void replaceExistingAttachment() throws Exception {
        final String name = "java-log.png";
        Document d = new Document();
        InputStream is = testInputStream(ATTACHMENT_1_NAME);
        d.addAttachment(name, is);

        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(d.getId());
        Attachment a = d2.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/png"));
        assertThat(a.getLength(), is(ATTACHMENT_1_FILESIZE));

        // Replace the attachment
        d2.addAttachment(name, testInputStream(ATTACHMENT_2_NAME));
        empty.saveDocument(d2);

        Document d3 = empty.fetchDocument(d2.getId());
        a = d3.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/jpeg"));
        assertThat(a.getName(), is(name));
        assertThat(a.getLength(), is(ATTACHMENT_2_FILESIZE));
    }

    @Test
    public void addAnotherAttachment() throws Exception {
        final String name = "java-log.png";
        Document d = new Document();
        InputStream is = testInputStream(ATTACHMENT_1_NAME);
        d.addAttachment(name, is);

        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(d.getId());
        Attachment a = d2.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/png"));
        assertThat(a.getLength(), is(ATTACHMENT_1_FILESIZE));

        // Add another attachment
        d2.addAttachment(ATTACHMENT_2_NAME, testInputStream(ATTACHMENT_2_NAME));
        empty.saveDocument(d2);

        Document d3 = empty.fetchDocument(d2.getId());
        // The 1st
        a = d3.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getLength(), is(ATTACHMENT_1_FILESIZE));

        // The 2nd
        a = d3.getAttachment(ATTACHMENT_2_NAME);
        assertNotNull(a);
        assertThat(a.getLength(), is(ATTACHMENT_2_FILESIZE));
    }

    @Test
    public void keepExistingAttachment() throws Exception {
        final String name = "java-log.png";
        Document d = new Document();
        InputStream is = testInputStream(ATTACHMENT_1_NAME);
        d.addAttachment(name, is);

        empty.saveDocument(d);

        Document d2 = empty.fetchDocument(d.getId());
        Attachment a = d2.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/png"));
        assertThat(a.getLength(), is(ATTACHMENT_1_FILESIZE));

        d2.put("new_attr", "val");
        empty.saveDocument(d2);

        Document d3 = empty.fetchDocument(d2.getId());
        a = d3.getAttachment(name);
        assertNotNull(a);
        assertThat(a.getContentType(), is("image/png"));
        assertThat(a.getName(), is(name));
        assertThat(a.getLength(), is(ATTACHMENT_1_FILESIZE));
    }

    private InputStream testInputStream(String name) {
        return AttachmentTest.class.getResourceAsStream("/fixtures/" + name);
    }

}
