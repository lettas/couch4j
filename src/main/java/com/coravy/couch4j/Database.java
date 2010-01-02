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
public interface Database {

    public interface StreamContext {
        void withResponseStream(InputStream is) throws IOException;
    }

    /**
     * 
     * @param string
     * @return
     * @throws NotFoundException
     */
    Document fetchDocument(String string);

    ServerResponse saveDocument(Document doc);

    ServerResponse saveDocument(Map<String, Object> doc);

    ServerResponse saveDocument(String json);

    ServerResponse saveDocument(Object obj);

    ServerResponse bulkSave(Collection<Document> docs);

    ViewResult fetchAllDocuments();

    ViewResult fetchAllDocuments(boolean includeDocs);

    ViewResult fetchView(View v);

    ServerResponse delete();

    void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException;

    ServerResponse deleteDocument(Document doc);
}
