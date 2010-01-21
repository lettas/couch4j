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
package org.couch4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.couch4j.util.StringUtils;

import net.sf.json.util.JSONUtils;

/**
 * Define a query for a particular view.
 * <p>
 * This class represents a query string needed to query the CouchDB server for a
 * particular view result.
 * <p>
 * The class uses the builder pattern to provide a fluent interface for creating
 * views.
 * 
 * <pre>
 * ViewQuery query = ViewQuery.builder(&quot;designdoc/viewname&quot;).key(&quot;test&quot;).descending(true);
 * </pre>
 * 
 * @author Stefan Saasen
 */
public class ViewQuery {

    /**
     * @return Simply returns an instance of an empty {@code ViewQuery}.
     */
    public static ViewQuery builder() {
        return new ViewQuery();
    }

    /**
     * @param name
     *            - The name of the view or the design document/view combination
     *            (e.g. "admin/by_id" with "DOCNAME/VIEWNAME" format).
     * @return Simply returns an instance of a {@code ViewQuery} for a
     *         particular view.
     */
    public static ViewQuery builder(final String name) {
        return new ViewQuery(name);
    }

    private String viewName;
    private String documentName;
    private final Map<String, String> params = new HashMap<String, String>();

    /**
     * Create an empty ViewQuery. A ViewQuery needs at least the name of the
     * view.
     * 
     * @see ViewQuery(String)
     */
    public ViewQuery() {
    }

    /**
     * Crreate a ViewQuery with a view name.
     * 
     * @param name
     *            - See {@link ViewQuery#builder(String)}
     */
    public ViewQuery(final String name) {
        name(name);
    }

    /**
     * @param name
     *            - See {@link ViewQuery#builder(String)}
     * @return ViewQuery
     */
    public ViewQuery name(final String name) {
        if (null != name && name.contains("/")) {
            String[] elems = name.split("/");
            if (elems.length != 2) {
                throw new IllegalArgumentException(
                        "Either supply only the view name and set the document by calling the document() method or use a single / to separate DESING/VIEW.");
            }
            documentName = elems[0];
            viewName = elems[1];
        } else {
            viewName = name;
        }
        return this;
    }

    /**
     * @param documentName
     *            The name of the design document
     * @return ViewQuery
     */
    public ViewQuery document(final String documentName) {
        this.documentName = documentName;
        return this;
    }

    public ViewQuery key(final String key) {
        params.put("key", JSONUtils.quote(key));
        return this;
    }

    public ViewQuery endkey(final String key) {
        params.put("endkey", JSONUtils.quote(key));
        return this;
    }

    public ViewQuery descending(final boolean descending) {
        params.put("descending", String.valueOf(descending));
        return this;
    }

    public ViewQuery includeDocs(final boolean includeDocs) {
        params.put("include_docs", String.valueOf(includeDocs));
        return this;
    }

    public ViewQuery group(final boolean group) {
        params.put("group", String.valueOf(group));
        return this;
    }

    public ViewQuery update(final boolean update) {
        params.put("update", String.valueOf(update));
        return this;
    }

    public ViewQuery skip(final int skip) {
        params.put("skip", String.valueOf(skip));
        return this;
    }

    public ViewQuery count(final int c) {
        params.put("count", String.valueOf(c));
        return this;
    }

    public ViewQuery startkey(final String key) {
        params.put("startkey", JSONUtils.quote(key));
        return this;
    }

    public ViewQuery startkey(final String... keyparts) {
        if (null == keyparts) {
            return this;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < keyparts.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(JSONUtils.quote(keyparts[i]));
        }
        sb.append("]");
        params.put("startkey", sb.toString());
        return this;
    }

    public ViewQuery startkeyDocid(final String... keyparts) {
        throw new UnsupportedOperationException("Implement me");
    }

    @Override
    public String toString() {
        // _design/content/_view/childs_with_lang
        StringBuilder sb = new StringBuilder();
        if (!viewName.startsWith("_")) {
            sb.append("_design/");
            sb.append(documentName);
            sb.append("/_view/");
        }
        sb.append(viewName);
        sb.append(StringUtils.createQueryString(this.params));
        return sb.toString();
    }

    /**
     * @return the query string to be used to fetch a CouchDB view result.
     */
    public String queryString() {
        return this.toString();
    }

}
