package com.coravy.couch4j.http;

import static com.coravy.core.collections.CollectionUtils.map;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.coravy.core.annotations.ThreadSafe;
import com.coravy.core.io.StreamUtils;
import com.coravy.couch4j.Attachment;
import com.coravy.couch4j.Couch4JException;
import com.coravy.couch4j.CouchDB;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.DatabaseInfo;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.DocumentNotFoundException;
import com.coravy.couch4j.DocumentUpdateConflictException;
import com.coravy.couch4j.ServerResponse;
import com.coravy.couch4j.View;
import com.coravy.couch4j.ViewResult;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
@ThreadSafe
public class DatabaseImpl implements Database<Document> {
    private final static Logger logger = Logger.getLogger(DatabaseImpl.class.getName());
    private static final int MAX_HOST_CONNECTIONS = 10;

    private final String name;
    private final HttpClient client;

    private final UrlResolver urlResolver;

    public DatabaseImpl(CouchDB server, String name) {
        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerClass(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager.class);
        params.setIntParameter("maxHostConnections", MAX_HOST_CONNECTIONS);
        
        logger.info("Creating new database instance. Please reuse this object for the same CouchDB database.");

        client = new HttpClient(params);
        this.name = name;

        urlResolver = new UrlResolverImpl(server, name);

        // Check if the database exists
        HttpMethod m = null;
        try {
            m = new GetMethod(urlResolver.baseUrl());
            int statusCode = client.executeMethod(m);
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                m = new PutMethod(urlResolver.baseUrl());
                statusCode = client.executeMethod(m);
                if (statusCode != HttpStatus.SC_CREATED) {
                    logger.log(Level.WARNING, String.format("Failed to create the database %s on %s. "
                            + "Failed with status code %d", name, server, statusCode));
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to connect to database: " + name);
        } finally {
            if (null != m) {
                m.releaseConnection();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.Database#bulkSave(java.util.Collection)
     */
    public ServerResponse bulkSave(Collection<Document> docs) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServerResponse delete() {
        return executeMethod(new DeleteMethod(urlResolver.baseUrl()));
    }

    public ViewResult<Document> fetchAllDocuments() {
        return this.fetchAllDocuments(false);
    }

    public ViewResult<Document> fetchAllDocuments(boolean includeDocs) {
        return new JsonViewResult(jsonForPath(View.builder("_all_docs").includeDocs(true).toString()), this);
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.Database#fetchDocument(java.lang.String)
     */
    public Document fetchDocument(String id) {
        String url = urlForPath(id);
        char[] response = getResponseForUrl(url);
        ResponseDocument d = new ResponseDocument(JSONObject.fromObject(String.valueOf(response)));
        d.setDatabase(this);
        return d;
    }

    public ViewResult<Document> fetchView(View v) {
        return new JsonViewResult(jsonForPath(v.queryString()), this);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.coravy.couch4j.Database#saveDocument(com.coravy.couch4j.Document)
     */
    public ServerResponse saveDocument(Document doc) {

        EntityEnclosingMethod method;
        if (null == doc.getId()) {
            method = new PostMethod(urlResolver.baseUrl());
        } else {
            method = new PutMethod(urlForPath(doc.getId()));
        }
        RequestEntity re;
        try {
            re = new StringRequestEntity(doc.toJson(), "application/json", "UTF-8");
            method.setRequestEntity(re);
        } catch (UnsupportedEncodingException e) {
            throw new Couch4JException(e);
        }
        ServerResponse response = executeMethod(method);
        doc.put("_id", response.getId());
        doc.put("_rev", response.getRev());
        return response;
    }

    public ServerResponse saveDocument(Map<String, Object> doc) {
        EntityEnclosingMethod method;
        final String id = (String) (doc.get("id") != null ? doc.get("id") : doc.get("_id"));
        if (null == id) {
            method = new PostMethod(urlResolver.baseUrl());
        } else {
            method = new PutMethod(urlForPath(id));
        }
        try {
            RequestEntity re = new StringRequestEntity(JSONSerializer.toJSON(doc).toString(), "application/json",
                    "UTF-8");
            method.setRequestEntity(re);

            client.executeMethod(method);

            // int statusCode = client.executeMethod(method);

            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            JSONObject jsonObject = JSONObject.fromObject(new String(responseBody));
            JsonServerResponse response = JsonServerResponse.fromJson(jsonObject);
            doc.put("_id", response.getId());
            doc.put("_rev", response.getRev());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            method.releaseConnection();
        }
    }

    public ServerResponse saveDocument(String json) {
        EntityEnclosingMethod method = new PostMethod(urlResolver.baseUrl());
        RequestEntity re;
        try {
            re = new StringRequestEntity(json, "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Couch4JException(e);
        }
        method.setRequestEntity(re);
        return executeMethod(method);
    }

    private char[] getResponseForUrl(final String url) {
        // Create a method instance.
        GetMethod method = new GetMethod(url);
        // System.err.println("url: " + url);
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(2, false));

        Reader reader = null;
        CharArrayWriter w = null;
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                logger.warning("Method failed: " + method.getStatusLine());
            }
            if (HttpStatus.SC_NOT_FOUND == statusCode) {
                throw new DocumentNotFoundException(method.getStatusLine().toString());
            }
            reader = new InputStreamReader(method.getResponseBodyAsStream(), method.getResponseCharSet());
            w = new CharArrayWriter();
            StreamUtils.copy(reader, w);
            return w.toCharArray();
        } catch (HttpException e) {
            logger.warning("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            logger.warning("Fatal transport error: " + e.getMessage());
        } finally {
            // Release the connection.
            method.releaseConnection();
            StreamUtils.closeSilently(reader);
            StreamUtils.closeSilently(w);
        }
        return new char[0];
    }

    public void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException {
        GetMethod method = new GetMethod(urlResolver.urlForPath("/" + a.getContentId() + "/" + a.getName()));

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                logger.warning("Method failed: " + method.getStatusLine());
            }
            InputStream is = method.getResponseBodyAsStream();
            ctx.withInputStream(is);
            StreamUtils.closeSilently(is);
        } catch (HttpException e) {
            logger.warning("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            logger.warning("Fatal transport error: " + e.getMessage());
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
    }

    public ServerResponse deleteDocument(Document doc) {
        final String id = doc.getId();
        final String rev = doc.getRev();
        DeleteMethod method = new DeleteMethod(urlResolver.urlForPath(id, map("rev", rev)));
        ServerResponse response = executeMethod(method);
        doc.put("_id", id);
        doc.put("_rev", response.getRev());
        return response;
    }

    private ServerResponse executeMethod(HttpMethodBase method) {
        try {
            int statusCode = client.executeMethod(method);
            // Read the response body.
            JSONObject jsonObject = fromResponseStream(method.getResponseBodyAsStream(), method.getResponseCharSet());
            switch (statusCode) {
            case HttpStatus.SC_NOT_FOUND:
                throw new DocumentNotFoundException();
            case HttpStatus.SC_CONFLICT:
                throw new DocumentUpdateConflictException(jsonObject.getString("error"), jsonObject.getString("reason"));
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
                break;
            }
            return JsonServerResponse.fromJson(jsonObject);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            method.releaseConnection();
        }
    }

    private JSONObject fromResponseStream(InputStream is, String charset) throws IOException {
        Reader reader = new InputStreamReader(is, charset);
        CharArrayWriter w = new CharArrayWriter();
        StreamUtils.copy(reader, w);
        JSONObject json = JSONObject.fromObject(w.toString());
        StreamUtils.closeSilently(reader);
        StreamUtils.closeSilently(w);
        return json;
    }

    private String jsonForPath(final String path) {
        return String.valueOf(getResponseForUrl(urlForPath(path)));
    }

    private String urlForPath(final String path) {
        Map<String, String> p = Collections.emptyMap();
        return urlResolver.urlForPath(path, p);
    }

    public void disconnect() {
        ((MultiThreadedHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
    }

    public String getName() {
        return name;
    }

    public DatabaseInfo getDatabaseInfo() {
        final JSONObject json = JSONObject.fromObject(String.valueOf(getResponseForUrl(urlResolver.baseUrl())));

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

            public int getDocDelCountr() {
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

}
