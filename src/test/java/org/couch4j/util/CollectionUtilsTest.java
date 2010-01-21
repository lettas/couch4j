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
package org.couch4j.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Saasen
 */
public class CollectionUtilsTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testMap() {
        Map<String, String> s = CollectionUtils.map("s1", "s2", "s2", "s3");
        assertEquals(2, s.size());
        assertEquals("s2", s.get("s1"));
        assertEquals("s3", s.get("s2"));
    }   
    
    @Test(expected=IllegalArgumentException.class)
    public final void testInvalidMap() {
        CollectionUtils.map("s1", "s2", "s2");
    }       

    @Test
    public final void testEmptyMap() {
        Map<String, String> s = CollectionUtils.map(new String[]{});
        assertTrue(s.isEmpty());
    }
    
    @Test
    public final void testNullMap() {
        Map<String, String> s = CollectionUtils.map((String[])null);
        assertTrue(s.isEmpty());
    }
    
    @Test
    public final void testList() {
        List<String> s = CollectionUtils.list("s1", "s2", "s2", "s3");
        assertEquals(4, s.size());
        assertEquals("s1", s.get(0));
        assertEquals("s2", s.get(1));
        assertEquals("s2", s.get(2));
        assertEquals("s3", s.get(3));
    }
    
    
    /**
     * Test method for {@link com.coravy.lib.web.core.util.CollectionUtils#set(T[])}.
     */
    @Test
    public final void testSet() {
        Set<String> s = CollectionUtils.set("s1", "s2", "s2", "s3");
        assertEquals(3, s.size());
    }

    /**
     * Test method for {@link com.coravy.lib.web.core.util.CollectionUtils#ary(T[])}.
     */
    @Test
    public final void testAry() {
        Integer[] intAry = CollectionUtils.ary(1,2,3,4);
        assertEquals(4, intAry.length);
    }

    
    @Test
    public final void testIncludesInt() {
        Integer[] intAry = CollectionUtils.ary(1,2,3,4);
        assertTrue(CollectionUtils.includes(intAry, 1));
        assertTrue(CollectionUtils.includes(intAry, 2));
        assertTrue(CollectionUtils.includes(intAry, 3));
        assertTrue(CollectionUtils.includes(intAry, 4));
        
        assertFalse(CollectionUtils.includes(intAry, 0));
        assertFalse(CollectionUtils.includes(intAry, 5));
        assertFalse(CollectionUtils.includes(intAry, 10));
    }
    
    @Test
    public final void testIncludesString() {
        String[] rigs = CollectionUtils.ary("javelin", "atom", "mirage");
        assertTrue(CollectionUtils.includes(rigs, "javelin"));
        assertTrue(CollectionUtils.includes(rigs, "atom"));
        assertTrue(CollectionUtils.includes(rigs, "mirage"));
        
        assertFalse(CollectionUtils.includes(rigs, "miragE"));
        assertFalse(CollectionUtils.includes(rigs, ""));
        assertFalse(CollectionUtils.includes(rigs, null));
        
        assertFalse(CollectionUtils.includes(CollectionUtils.ary(null, "test", "bla"), "t"));
        assertTrue(CollectionUtils.includes(CollectionUtils.ary(null, "test", "bla"), "test"));
    }    
    
    @Test(expected=IllegalArgumentException.class)
    public final void testIncludesInvalidAry() {
        CollectionUtils.includes(null, "miragE");
    }
    
}
