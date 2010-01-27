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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.couch4j.Database;
import org.couch4j.ViewResult;
import org.couch4j.ViewResultRow;
import org.couch4j.annotations.Immutable;

/**
 * @author Stefan Saasen
 */
@Immutable
final class JsonViewResult implements ViewResult {
    private final int total_rows;
    private final int offset;
    private final List<ViewResultRow> rows;
    private final JsonAwareDatabase database;

    private final JSONObject json;

    JsonViewResult(final String jsonString, JsonAwareDatabase database) {
        this.json = JSONObject.fromObject(jsonString);
        rows = new ArrayList<ViewResultRow>();
        this.database = database;
        this.total_rows = json.getInt("total_rows");
        this.offset = (total_rows > 0 && json.has("offset")) ? json.getInt("offset") : 0;
    }

    public int getTotalRows() {
        return total_rows;
    }

    public int getOffset() {
        return offset;
    }

    public List<ViewResultRow> getRows() {
        loadRowsIfNecessary();
        return rows;
    }

    public Iterator<ViewResultRow> iterator() {
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
