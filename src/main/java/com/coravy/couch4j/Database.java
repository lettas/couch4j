package com.coravy.couch4j;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a single logical CouchDB database.
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public interface Database {

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
}
