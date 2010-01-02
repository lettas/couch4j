package com.coravy.couch4j.http;

import com.coravy.couch4j.ServerResponse;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class JsonServerResponse extends ServerResponse {

    private String id;
    private String rev;
    private boolean ok;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

}
