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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Represents a single CouchDB database.
 * <p>
 * 
 * @couchdbApi http://wiki.apache.org/couchdb/HTTP_database_API
 * @couchdbApi http://wiki.apache.org/couchdb/Compaction
 * @author Stefan Saasen
 */
public interface Database extends AsynchronousDatabase, SynchronousDatabase {

    /**
     * @couchdb 0.10.?
     */
    public interface ChangeEvent {
        boolean isDeleted();

        String getId();

        String getSeq();

        List<String> changeRevs();
    }

    /**
     * @couchdb 0.10.?
     */
    public interface ChangeListener {
        void onChange(ChangeEvent event);
    }

    public interface StreamContext {
        void withInputStream(InputStream is) throws IOException;
    }

    /**
     * Disconnect this database client. After calling {@code disconnect} the
     * database client cannot be used any more.
     */
    void disconnect();

    /**
     * @return The name of the database
     */
    String getName();

    /**
     * @couchdb 0.10.?
     */
    void addChangeListener(ChangeListener listener);

    /**
     * @couchdb 0.10.?
     */
    void removeChangeListener(ChangeListener listener);

    /**
     * Return a {@link DatabaseInfo} descriptor for this database.
     */
    DatabaseInfo getDatabaseInfo();
}
