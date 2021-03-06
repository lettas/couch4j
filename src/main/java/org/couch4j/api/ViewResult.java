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

import java.util.List;

/**
 * A CouchDB view result.
 * <p>
 * Has meta information (number of total rows, offset) and a {@link List} of {@link ViewResultRow}s.
 * 
 * @author Stefan Saasen
 */
public interface ViewResult extends Iterable<ViewResultRow>, JsonExportable {
    
    /**
     * The number of rows in this view result.
     * 
     * @return number of rows
     */
    int getTotalRows();

    /**
     * The offset.
     * @return offset
     */
    int getOffset();

    /**
     * Returns a list of ViewResultRows.
     * 
     * @return List of view result rows or an empty list. Never returns null.
     */
    List<ViewResultRow> getRows();
}
