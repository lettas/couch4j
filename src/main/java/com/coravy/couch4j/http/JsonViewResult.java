package com.coravy.couch4j.http;

import java.util.Iterator;
import java.util.List;

import com.coravy.couch4j.ViewResult;
import com.coravy.couch4j.ViewResultRow;
import com.google.gson.Gson;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
class JsonViewResult implements ViewResult {
    private int total_rows;
    private int offset;
    private List<ViewResultRow> rows;

    public int getTotalRows() {
        return total_rows;
    }

    public int getOffset() {
        return offset;
    }

    public List<ViewResultRow> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this); // TODO Do we need this? THis will be a wrapped response anyway
    }

    public Iterator<ViewResultRow> iterator() {
        return rows.iterator();
    }
}
