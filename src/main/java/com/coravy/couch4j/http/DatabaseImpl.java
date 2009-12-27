package com.coravy.couch4j.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.coravy.core.io.StreamUtils;
import com.coravy.couch4j.CouchDB;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.JsonExportable;
import com.coravy.couch4j.ServerResponse;
import com.coravy.couch4j.View;
import com.coravy.couch4j.ViewResult;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class DatabaseImpl implements Database {

    // TODO replace with Log4J
    private final static Logger logger = Logger.getLogger(DatabaseImpl.class
            .getName());


    private final String name;
    private final CouchDB server;
    private final HttpClient client = new HttpClient();

    private String url;

    public DatabaseImpl(CouchDB server, String name) {
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
                System.err.println(statusCode);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        // TODO Auto-generated method stub
        return null;
    }

    public ViewResult fetchAllDocuments() {
        // _all_docs
        // return gson.fromJson(jsonForPath("_all_docs"), JsonViewResult.class);
        return new JsonViewResultWrapper(jsonForPath("_all_docs"));
    }

    public ViewResult fetchAllDocuments(boolean includeDocs) {
        return new JsonViewResultWrapper(jsonForPath(View.builder("_all_docs")
                .includeDocs(true).toString()));
        /*
         * return gson.fromJson(jsonForPath(View.builder("_all_docs")
         * .includeDocs(true).toString()), JsonViewResult.class);
         */
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.Database#fetchDocument(java.lang.String)
     */
    public Document fetchDocument(String id) {
        String url = urlForPath(id);
        if (null == url) {
            return null;
        }
        try {
            byte[] data = getResponseForUrl(url);
            if (null != data) {
                return new ResponseDocument(new String(data));
            }
        } catch (NotFoundException nfe) {
            logger.log(Level.FINER, nfe.getLocalizedMessage());
            return null;
        }
        return null;
    }

    public ViewResult fetchView(View v) {
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
            
            String json = "";
            if(doc instanceof JsonExportable) {
                json = ((JsonExportable)doc).toJson();
            } else {
                json = JSONSerializer.toJSON(doc.getAttributes()).toString();
            }
            
            RequestEntity re = new StringRequestEntity(json, "application/json", "UTF-8");
            method.setRequestEntity(re);

            client.executeMethod(method);

            // int statusCode = client.executeMethod(method);

            // Read the response body.
            byte[] responseBody = method.getResponseBody();
            JSONObject jsonObject = JSONObject.fromObject(new String(responseBody));
            JsonServerResponse response = (JsonServerResponse)JSONObject.toBean(jsonObject, JsonServerResponse.class);
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
        final String id = (String) (doc.get("id") != null ? doc.get("id") : doc
                .get("_id"));
        if (null == id) {
            method = new PostMethod(getUrl());
        } else {
            method = new PutMethod(urlForPath(id));
        }
        try {
            RequestEntity re = new StringRequestEntity(JSONSerializer.toJSON(doc).toString(),
                    "application/json", "UTF-8");
            method.setRequestEntity(re);

            client.executeMethod(method);

            // int statusCode = client.executeMethod(method);

            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            JSONObject jsonObject = JSONObject.fromObject(new String(responseBody));
            JsonServerResponse response = (JsonServerResponse) JSONObject.toBean(jsonObject, JsonServerResponse.class);
            doc.put("_id", response.getId());
            doc.put("_rev", response.getRev());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            method.releaseConnection();
        }
    }

    public ServerResponse saveDocument(Object object) {
        return saveDocument(JSONSerializer.toJSON(object).toString());
    }

    public ServerResponse saveDocument(String json) {
        EntityEnclosingMethod method = new PostMethod(getUrl());
        try {
            RequestEntity re = new StringRequestEntity(json,
                    "application/json", "UTF-8");
            method.setRequestEntity(re);

            client.executeMethod(method);

            // int statusCode = client.executeMethod(method);

            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            JSONObject jsonObject = JSONObject.fromObject(new String(responseBody));
            return (JsonServerResponse) JSONObject.toBean(jsonObject, JsonServerResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            method.releaseConnection();
        }
    }

    private byte[] getResponseForUrl(final String url) {
        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(url);
        // System.err.println("url: " + url);
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                logger.warning("Method failed: " + method.getStatusLine());
            }
            if (HttpStatus.SC_NOT_FOUND == statusCode) {
                throw new NotFoundException(method.getStatusLine().toString());
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamUtils.copy(method.getResponseBodyAsStream(), bos);
            return bos.toByteArray();
        } catch (HttpException e) {
            logger.warning("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            logger.warning("Fatal transport error: " + e.getMessage());
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return null;
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

    private String jsonForPath(final String path) {
        return new String(getResponseForUrl(urlForPath(path)));
    }

    // private String jsonForUrl(final String url) {
    // return new String(getResponseForUrl(url));
    // }

    private String urlForPath(final String path) {
        return getUrl() + "/" + path;
    }

}
