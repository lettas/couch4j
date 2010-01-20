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

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.couch4j.exceptions.DocumentNotFoundException;


/**
 * Represents a single logical CouchDB database.
 * 
 * @author Stefan Saasen
 */
public interface Database {

    /**
     * @couchdb 0.10.?
     */
    public interface ChangeEvent {
        boolean isDeleted();

        String getId();

        String getSeq();

        List<String> changeRevs();
    }

    /**
     * @couchdb 0.10.?
     */
    public interface ChangeListener {
        void onChange(ChangeEvent event);
    }

    public interface StreamContext {
        void withInputStream(InputStream is) throws IOException;
    }

    /**
     * 
     * @param docId
     * @return
     * @throws DocumentNotFoundException
     */
    Document fetchDocument(String docId);

    /**
     * Fetch a particular revision of a document.
     * 
     * @param docId
     *            The document id
     * @param rev
     *            The document revision
     * @return
     */
    Document fetchDocument(String docId, String rev);

    /**
     * Returns an instance of clazz with the properties of the CouchDB document
     * with the id {@code docId}.
     * 
     * @param <T>
     * @param docId
     *            The document id
     * @param clazz
     * @return T
     * @see Database#fetchDocument(String)
     */
    <T> T fetchObject(String docId, Class<T> clazz);
    
    /**
     * Returns an instance of clazz with the properties of the CouchDB document
     * with the id {@code docId}.
     * 
     * @param <T>
     * @param docId
     *            The document id
     * @param rev
     *            The document revision
     * @param clazz
     * @return T
     * @see Database#fetchDocument(String, String)
     */
    <T> T fetchObject(String docId,  String rev, Class<T> clazz);

    ServerResponse saveAttachment(Document doc, String name, InputStream data);

    ServerResponse saveDocument(Document doc);

    ServerResponse saveDocument(Map<String, ? super Object> doc);

    ServerResponse saveDocument(String json);

    ServerResponse saveDocument(Serializable json);

    ServerResponse saveDocument(Externalizable json);

    ServerResponse saveDocument(JsonExportable json);

    ServerResponse bulkSave(Collection<Document> docs);

    ViewResult fetchAllDocuments();

    ViewResult fetchAllDocuments(boolean includeDocs);

    ViewResult fetchView(View v);

    ServerResponse delete();

    void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException;

    ServerResponse deleteDocument(Document doc);

    /**
     * Disconnect this database client. After calling {@code disconnect} the
     * database client cannot be used any more.
     */
    void disconnect();

    /**
     * @return The name of the database
     */
    String getName();

    /**
     * @couchdb 0.10.?
     */
    void addChangeListener(ChangeListener listener);

    /**
     * @couchdb 0.10.?
     */
    void removeChangeListener(ChangeListener listener);

    DatabaseInfo getDatabaseInfo();
}