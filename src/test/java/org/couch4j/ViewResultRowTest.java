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

//import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.test.C;

public class ViewResultRowTest extends Couch4jBase {

    public ViewResultRowTest(CouchDbClient server) {
        super(server);
    }

    private Database database;

    @Before
    public void setUp() throws Exception {
        database = server.getDatabase("couch4j");
        assertNotNull(database);
    }

    @After
    public void teardown() {
        server.disconnect();
    }

    @Test
    public void testBooleanValues() throws Exception {
        ViewQuery v = ViewQuery.builder("boolean_value").document("viewresultrow").build();
        ViewResult res = database.fetchView(v);

        for (ViewResultRow row : res) {
            assertTrue(row.getValueAsBoolean());
        }
    }

    @Test
    public void testIntValues() throws Exception {
        ViewQuery v = ViewQuery.builder("int_value").document("viewresultrow").build();
        ViewResult res = database.fetchView(v);

        for (ViewResultRow row : res) {
            assertTrue(row.getValueAsInt() >= Integer.MIN_VALUE && row.getValueAsInt() <= Integer.MAX_VALUE);
        }
    }
    
    @Test
    public void testObjValues() throws Exception {
        ViewQuery v = ViewQuery.builder("object_value").document("viewresultrow").build();
        ViewResult res = database.fetchView(v);

        for (ViewResultRow row : res) {
            C test = row.getObject(C.class);
            assertNotNull(test);
        }
    }

}
