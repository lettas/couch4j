package com.coravy.couch4j;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class DocumentNotFoundException extends Couch4JException {

    private static final long serialVersionUID = 2164358807772048808L;

    public DocumentNotFoundException() {
        super();
    }

    public DocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public DocumentNotFoundException(Throwable cause) {
        super(cause);
    }

}
