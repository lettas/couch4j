package com.coravy.couch4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.sf.json.util.JSONUtils;

import com.coravy.core.util.StringUtils;

/**
 * @author Stefan Saasen
 */
public class View {

    public static View builder() {
        return new View();
    }

    public static View builder(final String name) {
        return new View(name);
    }

    public String queryString() {
        return this.toString();
    }

    private String viewName;
    private String documentName;

    private final HashMap<String, String> params = new HashMap<String, String>();

    public View() {
    }

    public View(final String name) {
        name(name);
    }

    public View name(final String name) {
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

    public View document(final String name) {
        documentName = name;
        return this;
    }

    /**
     * @param string
     * @return
     */
    public View key(final String key) {
        params.put("key", JSONUtils.quote(key));
        return this;
    }

    public View endkey(final String key) {
        params.put("endkey", JSONUtils.quote(key));
        return this;
    }

    public View descending(final boolean descending) {
        params.put("descending", String.valueOf(descending));
        return this;
    }

    public View includeDocs(final boolean includeDocs) {
        params.put("include_docs", String.valueOf(includeDocs));
        return this;
    }

    public View group(final boolean group) {
        params.put("group", String.valueOf(group));
        return this;
    }

    public View update(final boolean update) {
        params.put("update", String.valueOf(update));
        return this;
    }

    public View skip(final int skip) {
        params.put("skip", String.valueOf(skip));
        return this;
    }

    public View count(final int c) {
        params.put("count", String.valueOf(c));
        return this;
    }

    public View startkey(final String key) {
        params.put("startkey", JSONUtils.quote(key));
        return this;
    }

    public View startkey(final String... keyparts) {
        StringBuilder sb = new StringBuilder("[");
        sb.append(StringUtils.join(keyparts, ","));
        sb.append("]");
        params.put("startkey", sb.toString());
        return this;
    }

    public View startkeyDocid(final String... keyparts) {
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
                // ignore
            }
        }
        return sb.toString();
    }

}
