package com.coravy.couch4j.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.coravy.couch4j.CouchDB;

/**
 * @author Stefan Saasen
 */
class UrlResolverImpl implements UrlResolver {

    private String baseUrl;

    UrlResolverImpl(CouchDB server, String databaseName) {
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
        sb.append(databaseName);
        baseUrl = sb.toString();
    }

    public String baseUrl() {
        return baseUrl;
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.http.UrlResolver#urlForPath(java.lang.String)
     */
    public String urlForPath(String path) {
        Map<String, String> m = Collections.emptyMap();
        return urlForPath(path, m);
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.http.UrlResolver#urlForPath(java.lang.String,
     * java.util.Map)
     */
    public String urlForPath(String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
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
