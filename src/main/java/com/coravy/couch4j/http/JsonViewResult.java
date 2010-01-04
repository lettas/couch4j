package com.coravy.couch4j.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import com.coravy.core.annotations.Immutable;
import com.coravy.couch4j.Document;
import com.coravy.couch4j.api.Database;
import com.coravy.couch4j.api.ViewResult;
import com.coravy.couch4j.api.ViewResultRow;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
@Immutable
final class JsonViewResult implements ViewResult<Document> {
    private final int total_rows;
    private final int offset;
    private final List<ViewResultRow<Document>> rows;
    private final Database<Document> database;

    private final JSONObject json;

    JsonViewResult(final String jsonString, Database<Document> database) {
        this.json = JSONObject.fromObject(jsonString);
        this.offset = json.getInt("offset");
        this.total_rows = json.getInt("total_rows");
        rows = new ArrayList<ViewResultRow<Document>>();
        this.database = database;
    }

    public int getTotalRows() {
        return total_rows;
    }

    public int getOffset() {
        return offset;
    }

    public List<ViewResultRow<Document>> getRows() {
        loadRowsIfNecessary();
        return rows;
    }

    public Iterator<ViewResultRow<Document>> iterator() {
        loadRowsIfNecessary();
        return rows.iterator();
    }

    public String toJson() {
        return json.toString();
    }

    @SuppressWarnings("unchecked")
    private void loadRowsIfNecessary() {
        if (rows.isEmpty() && total_rows > 0) {
            for (Iterator<JSONObject> iterator = json.getJSONArray("rows").iterator(); iterator.hasNext();) {
                JSONObject viewResultRow = iterator.next();
                rows.add(new JsonViewResultRow(viewResultRow, this.database));
            }
        }
    }

    public JSONObject toJSONObject() {
        return json;
    }
}
