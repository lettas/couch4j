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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Saasen
 */
public class StreamUtilsTest {

    private class Marker {
        boolean flag;
    }

    private final static String EXPECTED_CONTENT = "Line1\nLine2\nLine3\nLine4";
    private File testFile;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        testFile = new File(StreamUtilsTest.class.getResource(
                "/fixtures/testfile1.txt").toURI());
        assertTrue(testFile.exists());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testToLinesInputStream() throws IOException {
        final List<String> expected = CollectionUtils.list("Line1", "Line2",
                "Line3", "Line4");
        final FileInputStream fin = new FileInputStream(testFile);
        try {
            final List<String> res = StreamUtils.toLines(fin);
            assertEquals(expected.size(), res.size());
            assertTrue("Not all bytes were read", fin.available() == 0);
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), res.get(i));
            }
        } finally {
            fin.close();
        }
    }

    @Test
    public final void testCopy() throws Exception {
        final FileInputStream fin = new FileInputStream(testFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            StreamUtils.copy(fin, bos);
            assertEquals(EXPECTED_CONTENT, new String(bos.toByteArray()));
        } finally {
            StreamUtils.closeSilently(fin);
        }
    }
    
    
    @Test
    public final void testCopyAndClose() throws Exception {
        final FileInputStream fin = new FileInputStream(testFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamUtils.copyAndClose(fin, bos);
        assertEquals(EXPECTED_CONTENT, new String(bos.toByteArray()));
    }

    @Test
    public final void testFastChannelCopy() throws Exception {
        final FileInputStream fin = new FileInputStream(testFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            StreamUtils.fastChannelCopy(Channels.newChannel(fin), Channels
                    .newChannel(bos));
            assertEquals(EXPECTED_CONTENT, new String(bos.toByteArray()));
        } finally {
            StreamUtils.closeSilently(fin);
        }
    }

    @Test
    public final void testCloseSilentlyChannel() {
        StreamUtils.closeSilently((Channel) null);
    }

    @Test
    public final void testCloseSilentlyNullInputStream() {
        StreamUtils.closeSilently((InputStream) null);
    }

    @Test
    public final void testCloseSilentlyInputStream() {
        final Marker m = new Marker();
        m.flag = false;

        InputStream test = new InputStream() {
            @Override
            public void close() throws IOException {
                m.flag = true;
            }

            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        StreamUtils.closeSilently(test);
        assertTrue(m.flag);
    }

    @Test
    public final void testCloseSilentlyNullOutputStream() {
        StreamUtils.closeSilently((OutputStream) null);
    }

    @Test
    public final void testCloseSilentlyOutputStream() {
        final Marker m = new Marker();
        m.flag = false;

        OutputStream test = new OutputStream() {
            @Override
            public void close() throws IOException {
                m.flag = true;
            }

            @Override
            public void write(int b) throws IOException {
            }
        };

        StreamUtils.closeSilently(test);
        assertTrue(m.flag);
    }

}
