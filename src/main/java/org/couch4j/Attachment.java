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

import org.couch4j.Database.StreamContext;

/**
 * An attachment is a blob that is attached to a document in a CouchDB database.
 * <p>
 * While in CouchDB there is a distinction between an attachment placeholder in
 * the document (the "stub") and the actual binary data, couch4j retrieves the
 * content automatically.
 * 
 * @author Stefan Saasen
 */
public interface Attachment {

    /**
     * Return the Content-Type (e.g "image/jpeg") of the attachment.
     */
    String getContentType();

    /**
     * @return the size of the attachment in bytes
     */
    long getLength();

    /**
     * @return the file name of the attachment
     */
    String getName();

    /**
     * @return The id of the enclosing document
     * @see Attachment#getDocumentId()
     */
    @Deprecated
    String getContentId();

    /**
     * @return The id of the enclosing document
     */
    String getDocumentId();

    /**
     * Stream the attachment bytes.
     * 
     * @param StreamContext
     * @throws IOException
     */
    void retrieve(StreamContext sc) throws IOException;
}
