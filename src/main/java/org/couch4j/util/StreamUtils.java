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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Stream utility methods.
 * <p>
 * Wherever possible, the methods in this class do <em>not</em> flush or close
 * the stream. This is to avoid making non-portable assumptions about the
 * streams' origin and further use. Thus the caller is still responsible for
 * closing streams after use.
 * <p>
 * If a method does close the stream it is explicitly stated in the method
 * description.
 * <p>
 * 
 * @author Stefan Saasen
 */
public final class StreamUtils {

    /**
     * The default buffer size to use.
     */
    private static final int BUFFER_SIZE = 4 * 1024;

    private StreamUtils() {
        throw new AssertionError("Do not instantiate StreamUtils");
    }

    /**
     * Closes an InputStream silently.
     * 
     * @param is
     *            InputStream to be closed.
     */
    public static void closeSilently(final InputStream is) {
        closeSilently((Closeable) is);
    }

    /**
     * Closes a Channel silently.
     * 
     * @param channel
     *            Channel to be closed.
     */
    public static void closeSilently(final Channel channel) {
        closeSilently((Closeable) channel);
    }

    /**
     * Closes an OutputStream silently.
     * 
     * @param os
     *            OutputStream to be closed.
     */
    public static void closeSilently(final OutputStream os) {
        closeSilently((Closeable) os);
    }

    /**
     * Closes a Reader silently.
     * 
     * @param inputReader
     *            Reader to be closed.
     */
    public static void closeSilently(final Reader inputReader) {
        closeSilently((Closeable) inputReader);
    }

    /**
     * Closes a Closeable silently.
     * 
     * @param os
     *            Closeable to be closed.
     */
    public static void closeSilently(final Closeable os) {
        if (null != os) {
            try {
                os.close();
            } catch (IOException ignore) {
                // swallow - hence silently
            }
        }
    }

    /**
     * Copy input to output stream.
     * <p>
     * This method does close the input and output stream!
     * 
     * @param is
     *            InputStream to copy from.
     * @param os
     *            OutputStream to copy to.
     * @throws IOException
     * @see {@link #copyAndClose(InputStream, OutputStream)}
     */
    public static void copyAndClose(InputStream is, OutputStream os) throws IOException {
        // get an channel from the stream
        final ReadableByteChannel inputChannel = Channels.newChannel(is);
        final WritableByteChannel outputChannel = Channels.newChannel(os);
        // copy the channels
        try {
            fastChannelCopy(inputChannel, outputChannel);
            // closing the channels
        } finally {
            closeSilently(inputChannel);
            closeSilently(outputChannel);
        }
    }

    /**
     * Copy input to output stream.
     * <p>
     * This method does <strong>NOT</strong> close the input and output stream!
     * 
     * @param is
     *            InputStream to copy from.
     * @param os
     *            OutputStream to copy to.
     * @throws IOException
     * @see {@link #copyAndClose(InputStream, OutputStream)}
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        // get an channel from the stream
        final ReadableByteChannel inputChannel = Channels.newChannel(is);
        final WritableByteChannel outputChannel = Channels.newChannel(os);
        // copy the channels
        fastChannelCopy(inputChannel, outputChannel);
    }

    /**
     * Copy input stream to output writer.
     * <p>
     * This method does close the input stream or out writer!
     * 
     * @param is
     *            InputStream to copy from.
     * @param os
     *            Writer to copy to.
     * @throws IOException
     */
    public static void copy(InputStream is, Writer out) throws IOException {
        InputStreamReader in = new InputStreamReader(is);
        copy(in, out);
    }

    /**
     * Copy input reader to output writer.
     * <p>
     * This method does close the input reader and the writer!
     * 
     * @param r
     *            Reader to copy from.
     * @param w
     *            Writer to copy to.
     * @throws IOException
     * @throws IOException
     */
    public static void copyAndClose(Reader r, Writer w) throws IOException {
        try {
            copy(r, w);
        } finally {
            closeSilently(r);
            closeSilently(w);
        }
    }

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p>
     * Large streams (over 2GB) will return a chars copied value of
     * <code>-1</code> after the copy has completed since the correct number of
     * chars cannot be returned as an int. For large streams use the
     * <code>copyLarge(Reader, Writer)</code> method.
     * 
     * @param input
     *            the <code>Reader</code> to read from
     * @param output
     *            the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException
     *             if the input or output is null
     * @throws IOException
     *             if an I/O error occurs
     * @throws ArithmeticException
     *             if the character count is too large
     * @since Commons IO 1.1
     */
    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a
     * <code>Writer</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * 
     * @param input
     *            the <code>Reader</code> to read from
     * @param output
     *            the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException
     *             if the input or output is null
     * @throws IOException
     *             if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Performs a fast channel copy from {@code src} to {@code dest}.
     * <p>
     * Does <strong>NOT</strong> close the channels!
     * 
     * @param src
     *            ReadableByteChannel
     * @param dest
     *            WritableByteChannel
     * @throws IOException
     */
    public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest)
            throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(4 * BUFFER_SIZE);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    /**
     * Returns a list of Strings that represent lines in the fiven InputStream
     * {@code is}.
     * <p>
     * The InputStream will <strong>NOT</strong> be closed.
     * 
     * @see #toLinesAndClose(InputStream)
     * @param is
     * @return List<String> List of Strings for each line that is read from the
     *         given InputStream.
     * @throws IOException
     */
    public static List<String> toLines(InputStream is) throws IOException {
        final ArrayList<String> lines = new ArrayList<String>();
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String line;
        while (null != (line = br.readLine())) {
            lines.add(line);
        }
        return lines;
    }

    /**
     * Returns a list of Strings that represent lines in the fiven InputStream
     * {@code is}.
     * <p>
     * The InputStream will be closed automatically.
     * 
     * @see #toLines(InputStream)
     * @param is
     * @return List<String> List of Strings for each line that is read from the
     *         given InputStream.
     * @throws IOException
     */
    public static List<String> toLinesAndClose(InputStream is) throws IOException {
        try {
            return toLines(is);
        } finally {
            closeSilently(is);
        }
    }

    /**
     * Returns a String representation of the given Reader.
     * <p>
     * The Reader will be closed.
     * 
     * @param is
     * @return
     * @throws IOException
     * @throws IOException
     */
    public static String toString(Reader r) throws IOException {
        Writer sw = new StringWriter();
        copyAndClose(r, sw);
        return sw.toString();
    }

}
