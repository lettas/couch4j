package com.coravy.couch4j;

import java.util.Collection;
import java.util.Map;

/**
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
    
    ServerResponse delete();
}
