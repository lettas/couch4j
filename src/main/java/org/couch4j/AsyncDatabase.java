package org.couch4j;

import java.io.InputStream;
import java.util.Collection;

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

    void saveAttachment(Document doc, String name, InputStream data, ResponseHandler<ServerResponse> response);

    void saveDocument(Object doc, ResponseHandler<ServerResponse> response);

    void saveDocument(String documentId, Object doc, ResponseHandler<ServerResponse> response);

    void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response);

    void fetchAllDocuments(ResponseHandler<ViewResult> response);

    void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response);

    void fetchView(ViewQuery v, ResponseHandler<ViewResult> response);
}
