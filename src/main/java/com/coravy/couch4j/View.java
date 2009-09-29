package com.coravy.couch4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import com.coravy.lib.core.text.StringUtils;

/**
 * @author Stefan Saasen
 */
public abstract class View {

	public static ViewBuilder builder() {
		return new ViewBuilder();
	}

	public static ViewBuilder builder(final String name) {
		return new ViewBuilder(name);
	}

	public abstract String queryString();

	private static class ViewImpl extends View {
		private final ViewBuilder builder;

		public ViewImpl(ViewBuilder viewBuilder) {
			builder = viewBuilder;
		}

		@Override
		public String queryString() {
			return builder.toString();
		}
	}

	public static class ViewBuilder {

		private String viewName;
		private String documentName;

		private final HashMap<String, String> params = new HashMap<String, String>();

		public ViewBuilder() {

		}

		public ViewBuilder(String name) {
			name(name);
		}

		public ViewBuilder name(final String name) {
			if (null != name && name.contains("/")) {
				String[] elems = name.split("/");
				if (elems.length != 2) {
					throw new IllegalArgumentException(
					        "Either supply only the view name and set the document by calling the document() method or use a single / to separate DESING/VIEW.");
				}
				documentName = elems[0];
				viewName = elems[1];
			}
			else {
				viewName = name;
			}
			return this;
		}

		public ViewBuilder document(final String name) {
			documentName = name;
			return this;
		}

		/**
		 * @param string
		 * @return
		 */
		public ViewBuilder key(final String key) {
			params.put("key", key);
			return this;
		}

		public ViewBuilder endkey(final String key) {
			params.put("endkey", key);
			return this;
		}

		public ViewBuilder descending(final boolean descending) {
			params.put("descending", String.valueOf(descending));
			return this;
		}

		public ViewBuilder includeDocs(final boolean includeDocs) {
			params.put("include_docs", String.valueOf(includeDocs));
			return this;
		}

		public ViewBuilder group(final boolean group) {
			params.put("group", String.valueOf(group));
			return this;
		}

		public ViewBuilder update(final boolean update) {
			params.put("update", String.valueOf(update));
			return this;
		}

		public ViewBuilder skip(final int skip) {
			params.put("skip", String.valueOf(skip));
			return this;
		}

		public ViewBuilder count(final int c) {
			params.put("count", String.valueOf(c));
			return this;
		}

		public ViewBuilder startkey(final String key) {
			params.put("startkey", key);
			return this;
		}

		public ViewBuilder startkey(final String... keyparts) {
			StringBuilder sb = new StringBuilder("[");
			sb.append(StringUtils.join(keyparts, ","));
			sb.append("]");
			params.put("startkey", sb.toString());
			return this;
		}

		public ViewBuilder startkeyDocid(final String... keyparts) {
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

					for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
						final String key = iterator.next();
						sb.append(URLEncoder.encode(key, "UTF-8"));
						sb.append("=");
						sb.append(URLEncoder.encode(params.get(key), "UTF-8"));
						if (iterator.hasNext()) {
							sb.append("&");
						}
					}

					// for (Map.Entry<String, String> e : params.entrySet()) {
					// sb.append(URLEncoder.encode(e.getKey(), "UTF-8"));
					// sb.append("=");
					// sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
					// sb.append("&");
					// }
				}
				catch (UnsupportedEncodingException ue) {
					// ignore
				}
			}
			return sb.toString();
		}

		public View build() {
			return new ViewImpl(this);
		}
	}

}
