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

import org.couch4j.exceptions.Couch4JException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A single row in a CouchDB view result.
 * 
 * @author Stefan Saasen
 */
public interface ViewResultRow extends JsonExportable {

    /**
     * The document id
     * 
     * @return document id
     */
    String getId();

    /**
     * The key of this result row.
     * 
     * @return key
     */
    String getKey();

    // ========================= value of the view result row. =================

    /**
     * If the value in the result row is the whole document or if the value
     * contains the document id this method returns a {@link Document} instance.
     * <p>
     * This method might lazily fetch the document if only the document id (and
     * optionally the document revision) is present.
     * 
     * @return Document
     */
    Document getDocument();

    /**
     * Return the value of the result row as an instance of {@code clazz}.
     * 
     * @param <T>
     * @param clazz
     * @return An instance of {@code clazz} populated with matching attributes
     *         in the wor value field or null if the row dows not contain a
     *         value field or if the value can not be turned into an instance of
     *         {@code clazz}.
     */
    <T> T getObject(Class<T> clazz);

    /**
     * Return the value of this row as a JSONObject.
     * 
     * @return JSONObject
     * @throws Couch4JException
     */
    JSONObject getValueAsObject();

    /**
     * Return the value of this row as a JSONArray.
     * 
     * @return JSONArray
     * @throws Couch4JException
     */
    JSONArray getValueAsArray();

    /**
     * Returns the boolean value of the row value.
     * 
     * @return value
     */
    boolean getValueAsBoolean();

    /**
     * Returns the double value of the row value.
     * 
     * @return value
     */
    double getValueAsDouble();

    /**
     * Returns the integer value of the row value.
     * 
     * @return value
     */
    int getValueAsInt();

    /**
     * Returns the String value of the row value.
     * 
     * @return value
     */
    String getValueAsString();

    /**
     * Returns the long value of the row value.
     * 
     * @return value
     */
    long getValueAsLong();
}
