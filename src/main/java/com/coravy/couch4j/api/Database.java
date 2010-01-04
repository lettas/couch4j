package com.coravy.couch4j.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.coravy.couch4j.View;
import com.coravy.couch4j.exceptions.DocumentNotFoundException;

/**
 * Represents a single logical CouchDB database.
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public interface Database<T> {

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
