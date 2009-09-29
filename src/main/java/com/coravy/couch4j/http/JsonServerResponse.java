package com.coravy.couch4j.http;

import com.coravy.couch4j.ServerResponse;
import com.google.gson.Gson;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class JsonServerResponse extends ServerResponse {

    private String id;
    private String rev;

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.ServerResponse#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see com.coravy.couch4j.ServerResponse#getRev()
     */
    @Override
    public String getRev() {
        return rev;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this); // TODO replace with json attribute
    }

}
