import java.util.HashMap;
import java.util.Map;

import com.coravy.couch4j.CouchDB;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.ServerResponse;
import com.coravy.couch4j.ViewResult;
import com.coravy.couch4j.ViewResultRow;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class Test {
    public static void main(String[] args) {
    	
        Database db = CouchDB.localServerInstance().getDatabase("couch4j");
        
        Document doc = db.fetchDocument("088af8c4144ae7dfbb01e0d3e6a57c17");
        
        System.err.println(doc.get("map"));
        System.err.println(doc.get("ary"));
        
        System.out.println(doc);
        doc = new Document();
        doc.put("thekey", "the Value");
        ServerResponse response = db.saveDocument(doc);
        System.out.println(response);

        System.out.println(doc);

        doc.put("another value", "Test 2");
        db.saveDocument(doc);

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("name", "Stefan & Sarah");
        db.saveDocument(m);
        // List<Document> docs = new ArrayList<Document>();
        // db.bulkSave(docs);

        ViewResult result = db.fetchAllDocuments();
        for (ViewResultRow r : result) {
            System.out.println(r);
        }
		
		
		/*
        System.err.println("==========================================");
        Database db = CouchDB.serverInstance("corelin1.contact-energy.co.nz").getDatabase("notes");
        ViewResult result = db.fetchAllDocuments(true);
        for (ViewResultRow r : result) {
            System.out.println(r);
        }
        
        System.err.println("==========================================");
        View view = View.builder().name("notes/by_ref").build();
        result = db.fetchView(view);
        System.out.println(result);
        */
    }
}
