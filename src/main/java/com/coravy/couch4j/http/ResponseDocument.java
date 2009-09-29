package com.coravy.couch4j.http;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.coravy.couch4j.Document;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public class ResponseDocument extends Document {
    private String _id;
    private String _rev;

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public String getRev() {
        return _rev;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
