package com.coravy.couch4j;

import java.io.Externalizable;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

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

    void saveDocument(Document doc, ResponseHandler<ServerResponse> response);

    void saveDocument(Map<String, ? super Object> doc, ResponseHandler<ServerResponse> response);

    void saveDocument(String json, ResponseHandler<ServerResponse> response);

    void saveDocument(Serializable json, ResponseHandler<ServerResponse> response);

    void saveDocument(Externalizable json, ResponseHandler<ServerResponse> response);

    void saveDocument(JsonExportable json, ResponseHandler<ServerResponse> response);

    void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response);

    void fetchAllDocuments(ResponseHandler<ViewResult> response);

    void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response);

    void fetchView(View v, ResponseHandler<ViewResult> response);
}
