/*
 * The MIT License
 *
 * Copyright (c) 2009, 2010 Stefan Saasen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.couch4j.http;

import static org.couch4j.util.CollectionUtils.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertySetStrategy;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.entity.StringEntity;
import org.couch4j.AsynchronousDatabase;
import org.couch4j.Attachment;
import org.couch4j.CouchDbClient;
import org.couch4j.Database;
import org.couch4j.DatabaseInfo;
import org.couch4j.Document;
import org.couch4j.JsonExportable;
import org.couch4j.ServerResponse;
import org.couch4j.ViewQuery;
import org.couch4j.ViewResult;
import org.couch4j.annotations.ThreadSafe;
import org.couch4j.exceptions.DocumentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stefan Saasen
 */
@ThreadSafe
final class DatabaseImpl implements Database, JsonAwareDatabase {

    private static final String UTF_8 = "UTF-8";

    private final static Logger logger = LoggerFactory.getLogger(Database.class);

    private final HttpConnectionManager client;
    private final String name;

    private DatabaseChangeNotificationService changesService;
    private final UrlBuilder urlResolver;
    private final CouchDbClient couchDb;

    private AsynchronousDatabase asyncDatabase;

    private final JsonConfig config;

    public DatabaseImpl(CouchDbClient couchDb, HttpConnectionManager ht, String databaseName) {
        this.couchDb = couchDb;
        this.client = ht;
        this.name = databaseName;

        this.urlResolver = new UrlBuilder(couchDb, databaseName);
        changesService = new DatabaseChangeNotificationService(ht.getHttpClient(), urlResolver, this);

        // Check if the database exists, create if not
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Check for database named {}", databaseName);
            }
            ht.jsonGet(urlResolver.baseUrl());
        } catch (DocumentNotFoundException dnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug("Database not found. Create new database '{}'", databaseName);
            }
            ht.jsonPut(urlResolver.baseUrl());
        }

        config = new JsonConfig();
        config.setPropertySetStrategy(new PropertySetStrategy() {
            @Override
            public void setProperty(Object bean, String key, Object value) throws JSONException {
                try {
                    Field field = bean.getClass().getField(key);
                    if (field != null)
                        field.set(bean, value);
                } catch (Exception e) {
                    try {
                        PropertyUtils.setSimpleProperty(bean, key, value);
                    } catch (Exception ex) {
                        // Ignore - We simply ignore attributes in the
                        // CouchDB document that are not available in the
                        // class.
                    }
                }
            }
        });

        this.asyncDatabase = new AsyncDatabaseImpl(this);
    }

    public void addChangeListener(ChangeListener listener) {
        changesService.addChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.couch4j.Database#bulkSave(java.util.Collection)
     */
    public ServerResponse bulkSave(Collection<Document> docs) {
        throw new UnsupportedOperationException("Implement!");
    }

    public ServerResponse delete() {
        return client.delete(urlResolver.baseUrl());
    }

    public ServerResponse deleteDocument(Document doc) {
        final String id = doc.getId();
        final String rev = doc.getRev();
        ServerResponse response = client.delete(urlResolver.urlForPath(id, map("rev", rev)));
        doc.put("_id", id);
        doc.put("_rev", response.getRev());
        return response;
    }

    public void disconnect() {
        // TODO Introduce another interface that exposes the
        // disconnect(Database) method?
        if (this.couchDb instanceof DefaultCouchDbClient) {
            ((DefaultCouchDbClient) this.couchDb).disconnect(this);
        }
    }

    public ViewResult fetchAllDocuments() {
        return this.fetchAllDocuments(false);
    }

    public ViewResult fetchAllDocuments(boolean includeDocs) {
        return new JsonViewResult(jsonForPath(ViewQuery.builder("_all_docs").includeDocs(true).build().toString()), this);
    }

    /*
     * (non-Javadoc)
     * @see org.couch4j.Database#fetchDocument(java.lang.String)
     */
    public Document fetchDocument(String docId) {
        String url = urlForPath(docId);
        ResponseDocument d = new ResponseDocument(this.client.jsonGet(url));
        d.setDatabase(this);
        return d;
    }

    public Document fetchDocument(String docId, String rev) {
        String url = urlForPath(docId, map("rev", rev));
        ResponseDocument d = new ResponseDocument(this.client.jsonGet(url));
        d.setDatabase(this);
        return d;
    }

    @SuppressWarnings("unchecked")
    public <T> T fetchObject(String docId, Class<T> clazz) {
        String url = urlForPath(docId);
        return (T) JSONObject.toBean(this.client.jsonGet(url), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T fetchObject(String docId, String rev, Class<T> clazz) {
        String url = urlForPath(docId, map("rev", rev));
        return (T) JSONObject.toBean(this.client.jsonGet(url), clazz);
    }

    public ViewResult fetchView(ViewQuery v) {
        return new JsonViewResult(jsonForPath(v.queryString()), this);
    }

    public DatabaseInfo getDatabaseInfo() {
        final JSONObject json = client.jsonGet(urlResolver.baseUrl());

        /*
         * {"db_name":"couch4j","doc_count":9,"doc_del_count":7,"update_seq":65,
         * "purge_seq"
         * :0,"compact_running":false,"disk_size":192601,"instance_start_time"
         * :"1262491923790998", "disk_format_version":4}
         */

        return new DatabaseInfo() {

            public int getDiskFormatVersion() {
                return json.getInt("disk_format_version");
            }

            public int getDiskSize() {
                return json.getInt("disk_size");
            }

            public int getDocCount() {
                return json.getInt("doc_count");
            }

            public int getDocDelCount() {
                return json.getInt("doc_del_count");
            }

            public Date getInstanceStartTime() {
                return new Date(json.getLong("instance_start_time"));
            }

            public String getName() {
                return json.getString("db_name");
            }

            public int getPurgeSeq() {
                return json.getInt("purge_seq");
            }

            public int getUpdateSeq() {
                return json.getInt("update_seq");
            }

            public boolean isCompactRunning() {
                return json.getBoolean("compact_running");
            }
        };
    }

    public String getName() {
        return name;
    }

    public void removeChangeListener(ChangeListener listener) {
        changesService.removeChangeListener(listener);
    }

    private ServerResponse saveDocument(Document doc) {

        if (logger.isDebugEnabled()) {
            logger.debug("saveDocument JSON: " + doc.toJson());
        }

        StringEntity entity;
        try {
            entity = new StringEntity(doc.toJson(), UTF_8);
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }

        ServerResponse response;
        if (null == doc.getId()) {
            response = client.post(urlResolver.baseUrl(), entity);
        } else {
            response = client.put(urlForPath(doc.getId()), entity);
        }

        doc.put("_id", response.getId());
        doc.put("_rev", response.getRev());
        return response;
    }

    public ServerResponse saveDocument(Object obj) {

        if (obj instanceof Document) {
            return this.saveDocument((Document) obj);
        }

        JSONObject json = JSONObject.fromObject(obj);
        try {
            StringEntity e = new StringEntity(json.toString(), UTF_8);
            e.setContentType("application/json");
            return client.post(urlResolver.baseUrl(), e);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e); // Should not happen as UTF-8 is
            // supported on every JVM
        }
    }

    @Override
    public ServerResponse saveDocument(String documentId, Object obj) {
        StringEntity entity;
        try {
            JSONObject json;
            if (obj instanceof JsonExportable) {
                json = ((JsonExportable) obj).toJSONObject();
            } else {
                json = JSONObject.fromObject(obj);
            }
            entity = new StringEntity(json.toString(), UTF_8);
            entity.setContentType("application/json");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        return client.put(urlForPath(documentId), entity);
    }

    @Override
    public ServerResponse storeAttachment(String documentId, String attachmentName, InputStream is) {
        throw new UnsupportedOperationException("Implement!");
    }

    public void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException {
        this.client.withAttachmentAsStream(urlResolver.urlForPath("/" + a.getDocumentId() + "/" + a.getName()), ctx);
    }

    private String jsonForPath(final String path) {
        return client.jsonGet(urlForPath(path)).toString();
    }

    private String urlForPath(final String path) {
        Map<String, String> p = Collections.emptyMap();
        return urlResolver.urlForPath(path, p);
    }

    private String urlForPath(final String path, Map<String, String> params) {
        return urlResolver.urlForPath(path, params);
    }

    public JsonConfig getConfig() {
        return config;
    }

    // ~~ ======================== AsyncDatabase ========================

    public void bulkSave(Collection<Document> docs, ResponseHandler<ServerResponse> response) {
        asyncDatabase.bulkSave(docs, response);
    }

    public void fetchAllDocuments(boolean includeDocs, ResponseHandler<ViewResult> response) {
        asyncDatabase.fetchAllDocuments(includeDocs, response);
    }

    public void fetchAllDocuments(ResponseHandler<ViewResult> response) {
        asyncDatabase.fetchAllDocuments(response);
    }

    public void fetchView(ViewQuery v, ResponseHandler<ViewResult> response) {
        asyncDatabase.fetchView(v, response);
    }

    public void saveDocument(Object doc, ResponseHandler<ServerResponse> response) {
        asyncDatabase.saveDocument(doc, response);
    }

    public void saveDocument(String documentId, Object doc, ResponseHandler<ServerResponse> response) {
        asyncDatabase.saveDocument(documentId, doc, response);
    }

    public void fetchDocument(String docId, ResponseHandler<Document> response) {
        asyncDatabase.fetchDocument(docId, response);
    }

    public void fetchDocument(String docId, String rev, ResponseHandler<Document> response) {
        asyncDatabase.fetchDocument(docId, rev, response);
    }

    public void storeAttachment(String documentId, String attachmentName, InputStream is,
            ResponseHandler<ServerResponse> response) {
        asyncDatabase.storeAttachment(documentId, attachmentName, is, response);
    }

}
