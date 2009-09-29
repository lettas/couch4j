package com.coravy.couch4j;

import java.util.List;

/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public interface ViewResult extends Iterable<ViewResultRow> {
    int getTotalRows();

    int getOffset();

    List<ViewResultRow> getRows();
    
    /**
     * @return JSON string representation of the viewresult
     */
    @Override
    String toString();
}
