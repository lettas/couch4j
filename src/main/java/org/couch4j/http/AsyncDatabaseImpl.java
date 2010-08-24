package org.couch4j.http;

//import static org.couch4j.util.CollectionUtils.map;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.couch4j.AsynchronousDatabase;
import org.couch4j.Document;
import org.couch4j.ServerResponse;
import org.couch4j.ViewQuery;
import org.couch4j.ViewResult;
import org.couch4j.exceptions.Couch4JException;

/**
 * @author Stefan Saasen
 */
final class AsyncDatabaseImpl implements AsynchronousDatabase {

    private final static class RequestToken implements AsyncToken {
        RequestToken(Object... args) {

        }
    }

    private final ExecutorService pool;

    private final JsonAwareDatabase database;

    AsyncDatabaseImpl(final JsonAwareDatabase database) {
        this.database = database;
        pool = Executors.newFixedThreadPool(5);
    }

    @Override
    public void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response) {
        throw new UnsupportedOperationException("Implement!");
    }

    @Override
    public void fetchAllDocuments(final ResponseHandler<ViewResult> response) {
        this.fetchAllDocuments(true, response);
    }

    @Override
    public void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response) {
        this.fetchView(ViewQuery.builder("_all_docs").includeDocs(includeDocs).build(), response);
    }

    @Override
    public void fetchView(final ViewQuery v, final ResponseHandler<ViewResult> response) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ViewResult result = database.fetchView(v);
                response.completed(result, new AsyncToken() {
                });
            }
        };
        pool.execute(r);
    }

    @Override
    public void saveDocument(final Object doc, final ResponseHandler<ServerResponse> response) {
        Callable<Void> c = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    ServerResponse sr = database.saveDocument(doc);
                    response.completed(sr, new RequestToken(doc));
                } catch (Couch4JException e) {
                    response.failed(e);
                }
                return null;
            }
        };

        pool.submit(c);
    }

    @Override
    public void saveDocument(final String documentId, final Object doc, final ResponseHandler<ServerResponse> response) {
        Callable<Void> r = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    ServerResponse sr = database.saveDocument(documentId, doc);
                    response.completed(sr, new RequestToken(documentId, doc));
                } catch (Couch4JException e) {
                    response.failed(e);
                }
                return null;
            }
        };
        pool.submit(r);
    }

    @Override
    public void fetchDocument(final String docId, final ResponseHandler<Document> response) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Document d = database.fetchDocument(docId);
                response.completed(d, new RequestToken(d.getId(), d.getRev()));
            }
        };
        pool.submit(r);
    }

    @Override
    public void fetchDocument(final String docId, final String rev, final ResponseHandler<Document> response) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Document d = database.fetchDocument(docId, rev);
                response.completed(d, new RequestToken(d.getId(), d.getRev()));
            }
        };
        pool.submit(r);
    }

    @Override
    public void storeAttachment(final String documentId, final String attachmentName, final InputStream is,
            final ResponseHandler<ServerResponse> response) {
        Callable<Void> r = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    ServerResponse sr = database.storeAttachment(documentId, attachmentName, is);
                    response.completed(sr, new RequestToken(documentId, attachmentName));
                } catch (Couch4JException e) {
                    response.failed(e);
                }
                return null;
            }
        };
        pool.submit(r);
    }
}
