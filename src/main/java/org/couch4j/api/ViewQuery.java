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
package org.couch4j.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.util.JSONUtils;
import org.couch4j.annotations.Immutable;

/**
 * Define a query for a particular view.
 * <p/>
 * This class represents a query string required to query the CouchDB server for a
 * particular view result.
 * <p/>
 * The class uses the builder pattern to provide a fluent interface for creating
 * view queries.
 * <p/>
 * <pre>
 * ViewQuery query = ViewQuery.builder(&quot;designdoc/viewname&quot;).key(&quot;test&quot;).descending(true);
 * </pre>
 *
 * @author Stefan Saasen
 */
@Immutable
public final class ViewQuery {

    private static class EncodedParam extends Param {
        EncodedParam(Object key, Object value) {
            super(key, value);
        }

        void addToQueryString(StringBuilder sb) {
            if (null == key) {
                return;
            }
            sb.append(key);
            sb.append("=");
            if (null != value) {
                sb.append(value);
            }
        }
    }

    private static class Param {
        final Object key;
        final Object value;

        Param(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        void addToQueryString(StringBuilder sb) {
            if (null == key) {
                return;
            }
            try {
                sb.append(URLEncoder.encode(key.toString(), "UTF-8"));
                sb.append("=");
                if (null != value) {
                    sb.append(URLEncoder.encode(value.toString(), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                // IGNORE
            }
        }
    }

    public static class ViewQueryBuilder {
        private String viewName;
        private String documentName;
        private final List<Param> params = new ArrayList<Param>();

        public ViewQueryBuilder() {
        }

        public ViewQueryBuilder(final String name) {
            name(name);
        }

        public ViewQueryBuilder limit(final int limit) {
            params.add(new Param("limit", String.valueOf(limit)));
            return this;
        }

        public ViewQueryBuilder count(final int count) {
            params.add(new Param("count", String.valueOf(count)));
            return this;
        }

        public ViewQueryBuilder groupLevel(int level) {
            params.add(new Param("group_level", String.valueOf(level)));
            return this;
        }

        public ViewQueryBuilder descending(final boolean descending) {
            params.add(new Param("descending", String.valueOf(descending)));
            return this;
        }

        /**
         * @param documentName The name of the design document
         * @return ViewQuery
         */
        public ViewQueryBuilder document(final String documentName) {
            this.documentName = documentName;
            return this;
        }

        public ViewQueryBuilder endkey(String... keyparts) {
            if (null != keyparts) {
                params.add(new EncodedParam("endkey", toArray(keyparts)));
            }
            return this;
        }

        public ViewQueryBuilder group(final boolean group) {
            params.add(new Param("group", String.valueOf(group)));
            return this;
        }

        public ViewQueryBuilder includeDocs(final boolean includeDocs) {
            params.add(new Param("include_docs", String.valueOf(includeDocs)));
            return this;
        }

        public ViewQueryBuilder inclusiveEnd(boolean b) {
            params.add(new Param("inclusive_end", String.valueOf(b)));
            return this;
        }

        public ViewQueryBuilder key(String... keyparts) {
            if (null != keyparts) {
                params.add(new EncodedParam("key", toArray(keyparts)));
            }
            return this;
        }

        /**
         * @param name - See {@link ViewQuery#builder(String)}
         * @return ViewQuery
         */
        public ViewQueryBuilder name(final String name) {
            if (null != name && name.contains("/")) {
                String[] elems = name.split("/");
                if (elems.length != 2) {
                    throw new IllegalArgumentException(
                            "Either supply only the view name and set the document by calling the document() method or use a single / to separate DESIGN/VIEW (where your document id= _design/DESIGN, and your views property has a function named VIEW) ");
                }
                documentName = elems[0];
                viewName = elems[1];
            } else {
                viewName = name;
            }
            return this;
        }

        public ViewQueryBuilder reduce(boolean b) {
            params.add(new Param("reduce", String.valueOf(b)));
            return this;
        }

        public ViewQueryBuilder skip(final int skip) {
            params.add(new Param("skip", String.valueOf(skip)));
            return this;
        }

        public ViewQueryBuilder stale(boolean b) {
            if (b) {
                params.add(new Param("stale", "ok"));
            }
            return this;
        }

        public ViewQueryBuilder startkey(final String... keyparts) {
            if (null != keyparts) {
                params.add(new EncodedParam("startkey", toArray(keyparts)));
            }
            return this;
        }

        public ViewQueryBuilder startkeyDocid(final String... keyparts) {
            if (null != keyparts) {
                params.add(new EncodedParam("startkey_docid", toArray(keyparts)));
            }
            return this;
        }

        public ViewQueryBuilder endkeyDocid(final String... keyparts) {
            if (null != keyparts) {
                params.add(new EncodedParam("endkey_docid", toArray(keyparts)));
            }
            return this;
        }

        public ViewQueryBuilder update(final boolean update) {
            params.add(new Param("update", String.valueOf(update)));
            return this;
        }

        public ViewQuery build() {
            return new ViewQuery(this);
        }

        private String toArray(String... parts) {
            if (null == parts) {
                return "";
            }

            if (parts.length == 1) {
                try {
                    return URLEncoder.encode(JSONUtils.quote(parts[0]), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Ignore - UTF-8 is always present
                }
            }

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                try {
                    sb.append(URLEncoder.encode(JSONUtils.quote(parts[i]), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Ignore - UTF-8 is always present
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    /**
     * @return Simply returns an instance of an empty {@code ViewQuery}.
     */
    public static ViewQueryBuilder builder() {
        return new ViewQueryBuilder();
    }

    /**
     * @param name - The name of the view or the design document/view combination
     *             (e.g. "admin/by_id" with "DOCNAME/VIEWNAME" format).
     * @return Simply returns an instance of a {@code ViewQuery} for a
     *         particular view.
     */
    public static ViewQueryBuilder builder(final String name) {
        return new ViewQueryBuilder(name);
    }

    private final String viewName;
    private final String documentName;
    private final List<Param> params;

    private ViewQuery(final ViewQueryBuilder builder) {
        this.viewName = builder.viewName;
        this.documentName = builder.documentName;
        this.params = builder.params;
    }


    /**
     * @return the query string to be used to fetch a CouchDB view result.
     */
    public String queryString() {
        return this.toString();
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
        if (params.size() > 0) {
            sb.append("?");
            for (final Iterator<Param> iterator = params.iterator(); iterator.hasNext();) {
                Param p = iterator.next();
                p.addToQueryString(sb);
                if (iterator.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }


}
