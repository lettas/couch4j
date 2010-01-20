package org.couch4j.exceptions;

import net.sf.json.JSONObject;

/**
 * @author Stefan Saasen
 */
public class Couch4JException extends RuntimeException {

    private static final long serialVersionUID = 6667383029339359662L;

    private int statusCode;
    private String error;
    private String reason;

    public Couch4JException(String error, String reason) {
        this.error = error;
        this.reason = reason;
    }

    public Couch4JException(Throwable t) {
        super(t);
    }

    public Couch4JException(JSONObject jsonObject) {
        if (null != jsonObject) {
            if (jsonObject.has("error")) {
                error = jsonObject.getString("error");
            }
            if (jsonObject.has("reason")) {
                reason = jsonObject.getString("reason");
            }
        }
    }

    public Couch4JException(JSONObject jsonObject, int statusCode) {
        this(jsonObject);
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return this.getClass().getSimpleName() + " [error=" + error + ", reason=" + reason + ", statusCode=" + statusCode
                + ", message: " + super.getMessage() + "]";
    }

    @Override
    public String getLocalizedMessage() {
        return this.getMessage();
    }

}
