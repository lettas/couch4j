package org.couch4j.http;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.couch4j.AsyncDatabase;
import org.couch4j.Document;
import org.couch4j.ServerResponse;
import org.couch4j.ViewQuery;
import org.couch4j.ViewResult;

/**
 * @author Stefan Saasen
 */
final class AsyncDatabaseImpl implements AsyncDatabase {

    private final ExecutorService pool;

    private final HttpConnectionManager connectionManager;
    private final UrlBuilder urlBuilder;
    private final JsonAwareDatabase database;

    AsyncDatabaseImpl(HttpConnectionManager connectionManager, UrlBuilder urlBuilder, final JsonAwareDatabase database) {
        this.connectionManager = connectionManager;
        this.urlBuilder = urlBuilder;
        this.database = database;
        pool = Executors.newFixedThreadPool(5);
    }

    @Override
    public void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response) {

    }

    @Override
    public void fetchAllDocuments(final ResponseHandler<ViewResult> response) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ViewResult result = new JsonViewResult(jsonForPath(ViewQuery.builder("_all_docs").includeDocs(true)
                        .toString()), database);
                response.completed(result, new AsyncToken() {
                });
            }
        };
        pool.execute(r);
    }

    private String jsonForPath(final String path) {
        return connectionManager.jsonGet(urlForPath(path)).toString();
    }

    private String urlForPath(final String path) {
        Map<String, String> p = Collections.emptyMap();
        return urlBuilder.urlForPath(path, p);
    }

    private String urlForPath(final String path, Map<String, String> params) {
        return urlBuilder.urlForPath(path, params);
    }

    @Override
    public void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fetchView(ViewQuery v, ResponseHandler<ViewResult> response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveAttachment(Document doc, String name, InputStream data, ResponseHandler<ServerResponse> response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveDocument(Object doc, ResponseHandler<ServerResponse> response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveDocument(String documentId, Object doc, ResponseHandler<ServerResponse> response) {
        // TODO Auto-generated method stub

    }

}
