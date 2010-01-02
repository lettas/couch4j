package com.coravy.couch4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a single logical CouchDB database.
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public interface Database<T> {

    public interface StreamContext {
        void withInputStream(InputStream is) throws IOException;
    }

    /**
     * 
     * @param string
     * @return
     * @throws DocumentNotFoundException
     */
    T fetchDocument(String string);

    ServerResponse saveDocument(T doc);

    ServerResponse saveDocument(Map<String, Object> doc);

    ServerResponse saveDocument(String json);

    ServerResponse bulkSave(Collection<T> docs);

    ViewResult<T> fetchAllDocuments();

    ViewResult<T> fetchAllDocuments(boolean includeDocs);

    ViewResult<T> fetchView(View v);

    ServerResponse delete();

    void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException;

    ServerResponse deleteDocument(T doc);
}
