package com.coravy.couch4j.http;

import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import com.coravy.couch4j.ViewResult;
import com.coravy.couch4j.ViewResultRow;

class JsonViewResultWrapper implements ViewResult {

	private JsonViewResult wrappedResult;
	private final String json;

	JsonViewResultWrapper(String json) {
		this.json = json;
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
            JSONObject jsonObject = JSONObject.fromObject(this.json);
            wrappedResult = new JsonViewResult(jsonObject);
		}
	}

}
