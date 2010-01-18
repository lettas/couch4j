package com.coravy.couch4j.http;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;

import com.coravy.core.io.StreamUtils;
import com.coravy.couch4j.ServerResponse;
import com.coravy.couch4j.exceptions.DocumentNotFoundException;
import com.coravy.couch4j.exceptions.DocumentUpdateConflictException;

/**
 * 
 * 
 * @author Stefan Saasen
 */
class HttpConnectionManager {

    private final HttpClient client;

    HttpConnectionManager(HttpClient client) {
        this.client = client;
    }
    
    HttpClient getHttpClient() {
        return this.client;
    }

    ServerResponse executeMethod(HttpMethodBase method) {
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

}
