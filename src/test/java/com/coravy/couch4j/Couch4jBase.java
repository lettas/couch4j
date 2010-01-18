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
package com.coravy.couch4j;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coravy.couch4j.exceptions.Couch4JException;

/**
 * For the test to work at least one running CouchDB instance needs to be there.
 * <p>
 * List of ports used in the test case:
 * <ul>
 *      <li><strike>5080,      // CouchDB 0.8.0</strike></li>
 *      <li><strike>5081,      // CouchDB 0.8.1</strike></li>
 *      <li>5090,      // CouchDB 0.9.0  </li>
 *      <li>5091,      // CouchDB 0.9.1  </li>
 *      <li>5092,      // CouchDB 0.9.2  </li>
 *      <li>50100,     // CouchDB 0.10.0 </li>
 *      <li>50101      // CouchDB 0.10.1 </li>
 * </ul>
 * 
 * @author Stefan Saasen
 */
@RunWith(Parameterized.class)
public abstract class Couch4jBase {

    protected final static Logger logger = LoggerFactory.getLogger(Couch4jBase.class);
    
    protected CouchDb server;

    public Couch4jBase(CouchDb server) {
        this.server = server;
    }

    private static final int[] PORTS = {
//        5080,      // CouchDB 0.8.0
//        5081,      // CouchDB 0.8.1
        5984,      // CouchDB 0.9.0 (default port)
        5090,      // CouchDB 0.9.0
        5091,      // CouchDB 0.9.1
        5092,      // CouchDB 0.9.2
        50100,     // CouchDB 0.10.0
        50101      // CouchDB 0.10.1
    };
    
    @Parameterized.Parameters
    public static Collection<CouchDb[]> testDatabases() {
        Collection<CouchDb[]> instancesRunning = new ArrayList<CouchDb[]>();
        for (int port : PORTS) {
            CouchDb server = CouchDbClient.newInstance("localhost", port);
            try {
                server.getDatabase("couch4j");
                instancesRunning.add(new CouchDb[]{server});
            } catch (Couch4JException ce) {
                logger.warn("Ignoring {} - connection failed.", server);
            }
        }
        if(instancesRunning.size() < 1) {
            throw new AssertionError("Unable to run tests without at least one running CouchDB instance.");
        }
        return instancesRunning;
    }

}
