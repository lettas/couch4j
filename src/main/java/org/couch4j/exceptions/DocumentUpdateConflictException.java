package org.couch4j.exceptions;

import net.sf.json.JSONObject;

/**
 * @author Stefan Saasen
 */
public class DocumentUpdateConflictException extends Couch4JException {

    private static final long serialVersionUID = 4794216023065374422L;

    public DocumentUpdateConflictException(JSONObject jsonObject) {
        super(jsonObject);
        // TODO Auto-generated constructor stub
    }
}
