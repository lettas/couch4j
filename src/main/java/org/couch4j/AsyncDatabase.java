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

import java.io.InputStream;
import java.util.Collection;

import org.couch4j.exceptions.DocumentNotFoundException;

/**
 * @author Stefan Saasen
 */
public interface AsyncDatabase {

    // TODO provide a limited set of predefined tokens
    static interface AsyncToken {
    }

    static interface ResponseHandler<T> {
        void completed(T response, AsyncToken token);

        void failed(Exception e);
    }

    /**
     * Bulk save a collection of documents.
     * 
     * @param Collection
     *            of {@link Document}s.
     * @return ServerResponse
     */
    void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response);

    /**
     * Fetch all documents.
     * 
     * @param includeDocs
     *            include the document if true, otherwise the document will be
     *            fetched lazily.
     * @return ViewResult
     */
    void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response);

    /**
     * Fetch all documents in this database.
     */
    void fetchAllDocuments(ResponseHandler<ViewResult> response);

    /**
     * Fetch a document.
     * 
     * @param docId
     * @throws DocumentNotFoundException
     */
    void fetchDocument(String docId, ResponseHandler<Document> response);

    /**
     * Fetch a particular revision of a document.
     * 
     * @param docId
     *            The document id
     * @param rev
     *            The document revision
     */
    void fetchDocument(String docId, String rev, ResponseHandler<Document> response);

    /**
     * @param v
     *            - A ViewQuery
     * @return ViewResult for this {@link ViewQuery}
     */
    void fetchView(ViewQuery v, ResponseHandler<ViewResult> response);

    /**
     * Save the given object as a CouchDB document.
     * <p>
     * 
     * @param object
     *            - Accepts JSON formatted strings, Maps, DynaBeans, JavaBeans
     *            and {@link Document} instances.
     */
    void saveDocument(Object doc, ResponseHandler<ServerResponse> response);

    /**
     * Save the given object as a CouchDB document using the given document id.
     * <p>
     * 
     * @param documentId
     *            The document id to use. If the document already exists it will
     *            be updated, otherwise a new document with this document id
     *            will be created.
     * @param object
     *            - Accepts JSON formatted strings, Maps, DynaBeans, JavaBeans
     *            and {@link Document} instances.
     */
    void saveDocument(String documentId, Object doc, ResponseHandler<ServerResponse> response);

    /**
     * Stores the content of the {@link InputStream} is using for a given
     * document (if the document does not exists it will be created) using the
     * attachmentName.
     * 
     * @param documentId
     * @param attachmentName
     * @param is
     * @param response
     */
    void storeAttachment(String documentId, String attachmentName, InputStream is,
            ResponseHandler<ServerResponse> response);
}
