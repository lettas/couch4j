package org.couch4j;

import java.io.InputStream;
import java.util.Collection;

import org.couch4j.exceptions.DocumentNotFoundException;

/**
 * @author Stefan Saasen
 */
public interface AsyncDatabase {

    static interface AsyncToken {
    }

    static interface ResponseHandler<T> {
        void completed(T response, AsyncToken token);

        void failed(Exception e);
    }

    /**
     * 
     * @param docId
     * @return
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
     * @return
     */
    void fetchDocument(String docId, String rev, ResponseHandler<Document> response);

    void storeAttachment(String documentId, String attachmentName, InputStream is, ResponseHandler<ServerResponse> response);

    void saveDocument(Object doc, ResponseHandler<ServerResponse> response);

    void saveDocument(String documentId, Object doc, ResponseHandler<ServerResponse> response);

    void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response);

    void fetchAllDocuments(ResponseHandler<ViewResult> response);

    void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response);

    void fetchView(ViewQuery v, ResponseHandler<ViewResult> response);
}
