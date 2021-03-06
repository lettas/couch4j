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

import java.util.Collections;
import java.util.Map;

import org.couch4j.api.CouchDbClient;
import org.couch4j.util.StringUtils;

/**
 * @author Stefan Saasen
 */
final class UrlBuilder {

    private String baseUrl;

    UrlBuilder(CouchDbClient client, String databaseName) {
        StringBuilder sb = new StringBuilder();
        sb.append("http");
        /*
         * if (useSsl) { sb.append("s"); }
         */
        sb.append("://");
        sb.append(client.getRemoteHost());
        sb.append(":");
        sb.append(client.getRemotePort());
        sb.append("/");
        sb.append(databaseName);
        baseUrl = sb.toString();
    }

    public String baseUrl() {
        return baseUrl;
    }

    /*
     * (non-Javadoc)
     * @see org.couch4j.http.UrlResolver#urlForPath(java.lang.String)
     */
    public String urlForPath(String path) {
        Map<String, String> m = Collections.emptyMap();
        return urlForPath(path, m);
    }

    /*
     * (non-Javadoc)
     * @see org.couch4j.http.UrlResolver#urlForPath(java.lang.String,
     * java.util.Map)
     */
    public String urlForPath(String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append("/");
        sb.append(path);
        sb.append(StringUtils.createQueryString(params));
        return sb.toString();
    }

}
