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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.couch4j.ServerResponse;
import org.couch4j.Database.StreamContext;
import org.couch4j.exceptions.Couch4JException;
import org.couch4j.exceptions.DocumentNotFoundException;
import org.couch4j.exceptions.DocumentUpdateConflictException;
import org.couch4j.util.StreamUtils;

/**
 * @author Stefan Saasen
 */
class HttpConnectionManager {

    static enum Method {
        GET, POST, PUT, DELETE
    }

    private static final int MAX_TOTAL_CONNECTIONS = 100;

    private final HttpClient client;

    public HttpConnectionManager() {
        // Create and initialize HTTP parameters
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(http);

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        this.client = new DefaultHttpClient(cm, params);
    }

    HttpClient getHttpClient() {
        return this.client;
    }

    private ServerResponse executeMethod(HttpRequestBase method) {
        try {
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            // Read the response body.
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = fromResponseStream(entity);
            switch (statusCode) {
            case HttpStatus.SC_NOT_FOUND:
                throw new DocumentNotFoundException(jsonObject);
            case HttpStatus.SC_CONFLICT:
                throw new DocumentUpdateConflictException(jsonObject);
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
                break;
            default:
                throw new Couch4JException(jsonObject);
            }
            return JsonServerResponse.fromJson(jsonObject);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            // method.releaseConnection();
        }
    }

    JSONObject jsonPut(String url) {
        HttpRequestBase method = new HttpPut(url);
        return this.jsonExecute(method);
    }

    private JSONObject jsonExecute(HttpRequestBase method) {
        try {
            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            // Read the response body.
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = fromResponseStream(entity);
            switch (statusCode) {
            case HttpStatus.SC_CONFLICT:
                throw new DocumentUpdateConflictException(jsonObject);
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
                break;
            case HttpStatus.SC_NOT_FOUND:
            default:
                throw new DocumentNotFoundException(jsonObject);
//                throw new Couch4JException(jsonObject, statusCode);
            }
            return jsonObject;
        } catch (ConnectException re) {
            throw new Couch4JException(re);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO replace
        } finally {
            // method.releaseConnection();
        }
    }

    JSONObject jsonGet(String url) {
        return jsonExecute(new HttpGet(url));
    }

    ServerResponse post(final String url) {
        return executeMethod(new HttpPost(url));
    }

    ServerResponse get(final String url) {
        return executeMethod(new HttpGet(url));
    }

    ServerResponse put(final String url) {
        return executeMethod(new HttpPut(url));
    }

    ServerResponse delete(final String url) {
        return executeMethod(new HttpDelete(url));
    }

    ServerResponse post(final String url, HttpEntity re) {
        HttpEntityEnclosingRequestBase m = new HttpPost(url);
        m.setEntity(re);
        return executeMethod(m);
    }

    ServerResponse put(final String url, HttpEntity re) {
        HttpEntityEnclosingRequestBase m = new HttpPut(url);
        m.setEntity(re);
        return executeMethod(m);
    }

    ServerResponse execute(final String url, Method method) {
        return this.execute(url, method, null);
    }

    ServerResponse execute(final String url, Method method, HttpEntity entity) {
        HttpRequestBase m = null;
        switch (method) {
        case POST:
            m = new HttpPost(url);
            if (null != entity) {
                ((HttpPost) m).setEntity(entity);
            }
            break;
        case PUT:
            m = new HttpPut(url);
            if (null != entity) {
                ((HttpPut) m).setEntity(entity);
            }
            break;
        case DELETE:
            m = new HttpDelete(url);
            break;
        default:
            m = new HttpGet(url);
            break;
        }
        return executeMethod(m);
    }

    void withAttachmentAsStream(final String url, final StreamContext ctx) throws IOException {
        HttpGet method = new HttpGet(url);

        HttpResponse response = client.execute(method);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            ctx.withInputStream(is);
            StreamUtils.closeSilently(is);
        }
    }

    // char[] getResponseForUrl(final String url) {
    // // Create a method instance.
    // HttpGet method = new HttpGet(url);
    //
    // HttpResponse response;
    // Reader reader = null;
    // CharArrayWriter w = null;
    // try {
    // response = client.execute(method);
    // HttpEntity entity = response.getEntity();
    // if (entity != null) {
    // reader = new InputStreamReader(entity.getContent(),
    // EntityUtils.getContentCharSet(entity));
    // w = new CharArrayWriter();
    // StreamUtils.copy(reader, w);
    // return w.toCharArray();
    // }
    // } catch (ClientProtocolException e) {
    // throw new Couch4JException(e);
    // } catch (IOException e) {
    // throw new Couch4JException(e);
    // } finally {
    // StreamUtils.closeSilently(reader);
    // StreamUtils.closeSilently(w);
    // }
    // return new char[0];
    // }

    private JSONObject fromResponseStream(HttpEntity entity) throws IOException {
        InputStream is = entity.getContent();
        Reader reader = new InputStreamReader(is, EntityUtils.getContentCharSet(entity));
        CharArrayWriter w = new CharArrayWriter();
        StreamUtils.copy(reader, w);
        JSONObject json = JSONObject.fromObject(w.toString());
        StreamUtils.closeSilently(reader);
        StreamUtils.closeSilently(w);
        entity.consumeContent(); // finish
        return json;
    }

    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
