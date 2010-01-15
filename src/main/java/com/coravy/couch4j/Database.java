package com.coravy.couch4j;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.coravy.couch4j.exceptions.DocumentNotFoundException;

/**
 * Represents a single logical CouchDB database.
 * 
 * @author Stefan Saasen (stefan@coravy.com)
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
     * @param string
     * @return
     * @throws DocumentNotFoundException
     */
    Document fetchDocument(String string);

    ServerResponse saveDocument(Document doc);

    ServerResponse saveDocument(Map<String, ? super Object> doc);

    ServerResponse saveDocument(String json);

    ServerResponse saveDocument(Serializable json);

    ServerResponse saveDocument(Externalizable json);

    ServerResponse bulkSave(Collection<Document> docs);

    ViewResult fetchAllDocuments();

    ViewResult fetchAllDocuments(boolean includeDocs);

    ViewResult fetchView(View v);

    ServerResponse delete();

    void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException;

    ServerResponse deleteDocument(Document doc);

    /**
     * Disconnect this database client. After calling {@code disconnect} the
     * database client can not be used any more.
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
