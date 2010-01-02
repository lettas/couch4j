package com.coravy.couch4j.http;

import static com.coravy.core.collections.CollectionUtils.map;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
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
import com.coravy.couch4j.CouchDB;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.DocumentNotFoundException;
import com.coravy.couch4j.ServerResponse;
import com.coravy.couch4j.View;
import com.coravy.couch4j.ViewResult;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
@ThreadSafe
public class DatabaseImpl implements Database<Document> {
    private final static Logger logger = Logger.getLogger(DatabaseImpl.class.getName());

    private final String name;
    private final CouchDB server;
    private final HttpClient client;

    private String url;

    public DatabaseImpl(CouchDB server, String name) {
        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerClass(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager.class);
        params.setIntParameter("maxHostConnections", 10);
        
        logger.info("Creating new database instance. Please reuse the CouchDB instance - there should only be a single database instance per CouchDB database.");
        
        client = new HttpClient(params);
        
        this.name = name;
        this.server = server;
        // Check if the database exists
        HttpMethod m = null;
        try {
            m = new GetMethod(getUrl());
            int statusCode = client.executeMethod(m);
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                m = new PutMethod(getUrl());
                statusCode = client.executeMethod(m);
                logger.log(Level.WARNING, String.format("Failed to create the database %s on %s. "
                        + "Failed with status code %d", name, server, statusCode));
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
        return null;
    }

    public ViewResult<Document> fetchAllDocuments() {
        // _all_docs
        // return gson.fromJson(jsonForPath("_all_docs"), JsonViewResult.class);
        return new JsonViewResultWrapper(jsonForPath("_all_docs"));
    }

    public ViewResult<Document> fetchAllDocuments(boolean includeDocs) {
        return new JsonViewResultWrapper(jsonForPath(View.builder("_all_docs").includeDocs(true).toString()));
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.Database#fetchDocument(java.lang.String)
     */
    public Document fetchDocument(String id) {
        String url = urlForPath(id);
        char[] response = getResponseForUrl(url);
        ResponseDocument d = new ResponseDocument(String.valueOf(response));
        d.setDatabase(this);
        return d;
    }

    public ViewResult<Document> fetchView(View v) {
        return new JsonViewResultWrapper(jsonForPath(v.queryString()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.coravy.couch4j.Database#saveDocument(com.coravy.couch4j.Document)
     */
    public ServerResponse saveDocument(Document doc) {

        EntityEnclosingMethod method;
        if (null == doc.getId()) {
            method = new PostMethod(getUrl());
        } else {
            method = new PutMethod(urlForPath(doc.getId()));
        }
        try {
            RequestEntity re = new StringRequestEntity(doc.toJson(), "application/json", "UTF-8");
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

    public ServerResponse saveDocument(Map<String, Object> doc) {
        EntityEnclosingMethod method;
        final String id = (String) (doc.get("id") != null ? doc.get("id") : doc.get("_id"));
        if (null == id) {
            method = new PostMethod(getUrl());
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
        EntityEnclosingMethod method = new PostMethod(getUrl());
        try {
            RequestEntity re = new StringRequestEntity(json, "application/json", "UTF-8");
            method.setRequestEntity(re);

            client.executeMethod(method);

            // int statusCode = client.executeMethod(method);

            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            JSONObject jsonObject = JSONObject.fromObject(new String(responseBody));
            return JsonServerResponse.fromJson(jsonObject);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            method.releaseConnection();
        }
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
            w = new  CharArrayWriter();
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

    private String getUrl() {
        if (null == url) {
            StringBuilder sb = new StringBuilder();
            sb.append("http");
            /*
             * if (useSsl) { sb.append("s"); }
             */
            sb.append("://");
            sb.append(server.getHost());
            sb.append(":");
            sb.append(server.getPort());
            sb.append("/");
            sb.append(this.name);
            url = sb.toString();
        }
        return url;
    }

    public void withAttachmentAsStream(final Attachment a, final StreamContext ctx) throws IOException {
        // Create an instance of HttpClient.
        StringBuilder sb = new StringBuilder();
        sb.append(this.getUrl());
        sb.append("/");
        sb.append(a.getContentId());
        sb.append("/");
        sb.append(a.getName());
        GetMethod method = new GetMethod(sb.toString());

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
        DeleteMethod method = new DeleteMethod(urlForPath(id, map("rev", rev)));
        try {
            int statusCode = client.executeMethod(method);
            switch (statusCode) {
            case HttpStatus.SC_NOT_FOUND:
                throw new DocumentNotFoundException();
            case HttpStatus.SC_OK:

                break;
            default:
                throw new RuntimeException(); // TODO change
            }

            // Read the response body.
            JSONObject jsonObject = fromResponseStream(method.getResponseBodyAsStream(), method.getResponseCharSet());
            JsonServerResponse response = JsonServerResponse.fromJson(jsonObject);
            doc.put("_id", id);
            doc.put("_rev", response.getRev());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            method.releaseConnection();
        }
    }

    private JSONObject fromResponseStream(InputStream is, String charset) throws IOException {
        Reader reader = new InputStreamReader(is, charset); 
        CharArrayWriter w = new  CharArrayWriter();
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
        return this.urlForPath(path, p);
    }

    private String urlForPath(final String path, final Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(getUrl());
        sb.append("/");
        sb.append(path);
        if (!params.isEmpty()) {
            sb.append("?");
            try {

                for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
                    final Entry<String, String> entry = iterator.next();
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    if (iterator.hasNext()) {
                        sb.append("&");
                    }
                }
            } catch (UnsupportedEncodingException ue) {
                // ignore - UTF-8 is mandatory
            }
        }
        return sb.toString();
    }

}
