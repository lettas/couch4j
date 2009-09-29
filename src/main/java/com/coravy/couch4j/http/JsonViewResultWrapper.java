package com.coravy.couch4j.http;

import java.util.Iterator;
import java.util.List;

import com.coravy.couch4j.ViewResult;
import com.coravy.couch4j.ViewResultRow;
import com.google.gson.Gson;

class JsonViewResultWrapper implements ViewResult {

	private JsonViewResult wrappedResult;
	private final String json;
	private final Gson gson;

	JsonViewResultWrapper(String json, Gson gson) {
		this.json = json;
		this.gson = gson;
	}

	public int getOffset() {
		checkWrappedResult();
		return wrappedResult.getOffset();
	}

	public List<ViewResultRow> getRows() {
		checkWrappedResult();
		return wrappedResult.getRows();
	}

	public int getTotalRows() {
		checkWrappedResult();
		return wrappedResult.getTotalRows();
	}

	public Iterator<ViewResultRow> iterator() {
		checkWrappedResult();
		return wrappedResult.iterator();
	}

	@Override
	public String toString() {
		return this.json;
	}

	private void checkWrappedResult() {
		if (null == wrappedResult) {
			wrappedResult = gson.fromJson(json, JsonViewResult.class);
		}
	}

}
