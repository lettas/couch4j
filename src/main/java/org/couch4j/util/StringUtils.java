package org.couch4j.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * String related helper/utility methods.
 * 
 * @author Stefan Saasen
 */
public final class StringUtils {

    /**
     * Do not instantiate StringUtils.
     */
    private StringUtils() {
    }

    /**
     * Determines if the given String <code>s</code> has Text (not whitespace).
     * <p>
     * This is the case if:
     * <ul>
     * <li>The String is not null</li>
     * <li>The lenght is greater than 0</li>
     * <li>The String contains at least one non-whitespace char.</li>
     * </ul>
     * 
     * @param s
     *            String to Test.
     * @return true if the String s is not null and has text other than
     *         whitespace.
     */
    public static boolean hasText(String s) {
        if (null == s || s.length() < 1) {
            return false;
        }
        int strLen = s.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Join the Array elements using the given delimiter.
     * 
     * @param ary
     *            Array to Join
     * @param delimiter
     *            Element delimiter
     * @return String with joined array elements.
     */
    public static String join(final Object[] ary, final String delimiter) {
        if (null == ary || ary.length == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < ary.length; i++) {
            if (i > 0) {
                buffer.append(delimiter);
            }
            buffer.append(ary[i]);
        }
        return buffer.toString();
    }

    /**
     * Join the Collection elements using the given delimiter.
     * 
     * @param coll
     *            Collection to Join
     * @param delimiter
     *            Element delimiter
     * @return String with joined Collection elements.
     */
    public static String join(final Collection<?> coll, final String delimiter) {
        if (null == coll || coll.size() == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        Iterator<?> iter = coll.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    /**
     * Returns all exception messages of this exception chain.
     * 
     * @param t
     *            the Throwable
     * @return String with all exception messages separated by '\n'
     */
    public static String getChainedExceptionMessages(Throwable t) {
        return getChainedExceptionMessages(t, '\n');
    }

    /**
     * Returns all exception messages of this exception chain.
     * 
     * @param t
     *            the Throwable
     * @param msgSeparator
     *            char that is used as a msgSeparator
     * @return String with all exception messages separated by msgSeparator
     */
    public static String getChainedExceptionMessages(final Throwable t,
            char msgSeparator) {
        final StringBuilder m = new StringBuilder();
        m.append(t.getClass().getName());
        m.append(": ");
        m.append(t.getLocalizedMessage());
        Throwable c = t;
        int count = 0;
        while (null != (c = c.getCause())) {
            m.append(msgSeparator);
            m.append('\t');
            m.append(++count);
            m.append("-> ");
            m.append(c.getClass().getName());
            m.append(": ");
            m.append(c.getLocalizedMessage());
        }
        return m.toString();
    }

    /**
     * Replaces the following characters & < > " withthe respective html
     * entities.
     * 
     * @param in
     *            Input
     * @return String
     */
    public static String escapeXml(final String in) {
        final StringTokenizer tok = new StringTokenizer(in, "<>&\"", true);
        final StringBuffer result = new StringBuffer();
        String token;
        while (tok.hasMoreTokens()) {
            token = tok.nextToken();
            if ("<".equals(token)) {
                result.append("&lt;");
            } else if (">".equals(token)) {
                result.append("&gt;");
            } else if ("&".equals(token)) {
                result.append("&amp;");
            } else if ("\"".equals(token)) {
                result.append("&quot;");
            } else {
                result.append(token);
            }
        }
        return result.toString();
    }

}
