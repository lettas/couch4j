package com.coravy.couch4j;

/**
 * @author Stefan Saasen
 */
public class Couch4JException extends RuntimeException {

    private static final long serialVersionUID = 6667383029339359662L;

    public Couch4JException() {
        super();
    }

    public Couch4JException(String message, Throwable cause) {
        super(message, cause);
    }

    public Couch4JException(String message) {
        super(message);
    }

    public Couch4JException(Throwable cause) {
        super(cause);
    }

}
