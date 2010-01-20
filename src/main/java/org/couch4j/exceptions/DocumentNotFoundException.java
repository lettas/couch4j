package org.couch4j.exceptions;

import net.sf.json.JSONObject;

/**
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class DocumentNotFoundException extends Couch4JException {

    private static final long serialVersionUID = 2164358807772048808L;

    public DocumentNotFoundException(JSONObject jsonObject) {
        super(jsonObject);
    }

    public DocumentNotFoundException(Throwable t) {
        super(t);
    }

}
