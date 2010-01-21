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
package org.couch4j;

import java.util.Date;

/**
 * This class provides database meta information.
 * 
 * @author Stefan Saasen
 */
public interface DatabaseInfo {

    /**
     * @return the name of the database.
     */
    String getName();

    /**
     * @return the number of documents in this database.
     */
    int getDocCount();

    /**
     * @return the number of deleted documents.
     */
    // TODO verify that this returns the number of all the deleted documents.
    int getDocDelCount();

    /**
     * @return The update sequence.
     */
    int getUpdateSeq();

    int getPurgeSeq();

    /**
     * @return true if a compaction process is running, false otherwise.
     */
    boolean isCompactRunning();

    /**
     * @return size of the database file in bytes.
     */
    int getDiskSize();

    /**
     * @return Startup time of the CouchDB server.
     */
    // TODO verify
    Date getInstanceStartTime();

    int getDiskFormatVersion();
}
