package com.coravy.couch4j;

/**
 * @author Stefan Saasen
 */
public class DocumentUpdateConflictException extends Couch4JException {

    private static final long serialVersionUID = 4794216023065374422L;

    private final String error;
    private final String reason;

    /**
     * 
     */
    public DocumentUpdateConflictException(String error, String reason) {
        this.error = error;
        this.reason = reason;
    }

    public String getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }

}
