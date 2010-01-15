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
import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coravy.couch4j.exceptions.Couch4JException;

/**
 * @author Stefan Saasen
 */
@RunWith(Parameterized.class)
public abstract class Couch4jBase {

    protected final static Logger logger = LoggerFactory.getLogger(Couch4jBase.class);
    
    protected CouchDB server;

    public Couch4jBase(CouchDB server) {
        this.server = server;
    }

    @Parameterized.Parameters
    public static Collection<CouchDB[]> testDatabases() {
        Collection<CouchDB[]> toTest = Arrays.asList(new CouchDB[][] { { CouchDB.localServerInstance() },
                { new CouchDB("localhost", 59810) } });

        Collection<CouchDB[]> instancesRunning = new ArrayList<CouchDB[]>();
        for (CouchDB[] param : toTest) {
            CouchDB server = param[0];
            try {
                server.getDatabase("couch4j");
                instancesRunning.add(param);
            } catch (Couch4JException ce) {
                logger.warn("Ignoring {} - connection failed.", server);
            }
        }
        return instancesRunning;
    }

}
