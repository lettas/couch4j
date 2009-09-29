package com.coravy.couch4j.http;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2164358807772048808L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

}
