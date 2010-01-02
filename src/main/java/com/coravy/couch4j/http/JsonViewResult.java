package com.coravy.couch4j.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import com.coravy.couch4j.ViewResult;
import com.coravy.couch4j.ViewResultRow;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
final class JsonViewResult implements ViewResult {
    private int total_rows;
    private int offset;
    private List<ViewResultRow> rows;

    private final JSONObject json;

    public JsonViewResult(final JSONObject json) {
        this.json = json;
        this.offset = json.getInt("offset");
        this.total_rows = json.getInt("total_rows");
    }

    public int getTotalRows() {
        return total_rows;
    }

    public int getOffset() {
        return offset;
    }

    @SuppressWarnings("unchecked")
    public List<ViewResultRow> getRows() {
        List<ViewResultRow> r = new ArrayList<ViewResultRow>();
        for (Iterator iterator = json.getJSONArray("rows").iterator(); iterator.hasNext();) {
            JSONObject viewResultRow = (JSONObject) iterator.next();
            r.add(new JsonViewResultRow(viewResultRow));
        }
        return r;
    }

    public Iterator<ViewResultRow> iterator() {
        return rows.iterator();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setRows(List<ViewResultRow> rows) {
        this.rows = rows;
    }
}
