package org.couch4j.util;

import eu.medsea.mimeutil.MimeUtil2;

public class MimeUtil {
    private final static ThreadLocal<MimeUtil2> MIME_UTIL = new ThreadLocal<MimeUtil2>() {
        @Override
        protected MimeUtil2 initialValue() {
            MimeUtil2 mimeUtil = new MimeUtil2();
            mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
            return mimeUtil;
        }
    };

    public static String mostSpecificContentType(byte[] binaryData) {
        return MimeUtil2.getMostSpecificMimeType(MIME_UTIL.get().getMimeTypes(binaryData)).toString();
    }
}
