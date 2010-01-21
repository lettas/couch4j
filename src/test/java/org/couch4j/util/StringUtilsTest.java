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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilsTest {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(StringUtilsTest.class);
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testHasTextEmpty() {
		String empty = "";
		assertFalse(StringUtils.hasText(empty));
	}
	
    @Test
    public final void testHasText() {
        String empty = " a   ";
        assertTrue(StringUtils.hasText(empty));
    }

	@Test
	public final void testHasTextNull() {
		String empty = null;
		assertFalse(StringUtils.hasText(empty));
	}
	
	@Test
	public final void testHasTextAllWhitespace() {
		String empty = "    ";
		assertFalse(StringUtils.hasText(empty));
	}
	
	@Test
	public final void testGetChainedExceptionMessages() {
		IOException root = new IOException("root msg");
		Exception e = new Exception("my msg", root);
		String msg = StringUtils.getChainedExceptionMessages(e, '\n');
		assertEquals("java.lang.Exception: my msg\n\t1-> java.io.IOException: root msg", msg);
	}
	
    @Test
    public final void testGetChainedExceptionMessages2() {
        IOException root = new IOException("root msg");
        Exception e = new Exception("my msg", root);
        String msg = StringUtils.getChainedExceptionMessages(e);
        assertEquals("java.lang.Exception: my msg\n\t1-> java.io.IOException: root msg", msg);
    }

    @Test
    public void testJoinObjectArrayString() {
        String[] first = {"1", "2", "3"};
        assertEquals("1,2,3", StringUtils.join(first, ","));
        
        assertEquals("", StringUtils.join(new Object[]{}, ","));
        Object[] n = null;
        assertEquals("", StringUtils.join(n, ","));
    }

    @Test
    public void testJoinCollectionString() {
        Collection<String> c = new ArrayList<String>();
        c.add("1");
        c.add("2");
        c.add("3");
        assertEquals("1,2,3", StringUtils.join(c, ","));
        assertEquals("", StringUtils.join(new ArrayList<String>(), ","));
        Collection<String> nc = null;
        assertEquals("", StringUtils.join(nc, ","));
    }	
	
    @Test
    public final void testEscapeXml() {
        String test = "Kompetente \"<>Beratung";
        assertEquals("Kompetente &quot;&lt;&gt;Beratung", StringUtils
                .escapeXml(test));
        assertEquals("&quot;&quot;", StringUtils.escapeXml("\"\""));
        assertEquals("&lt;&gt;&lt;&lt;", StringUtils.escapeXml("<><<"));
        assertEquals("&amp;lt;&amp;gt;&amp;lt;&amp;lt;", StringUtils.escapeXml("&lt;&gt;&lt;&lt;"));
    }

    
}
