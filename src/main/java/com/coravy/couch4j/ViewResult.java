package com.coravy.couch4j;

import java.util.List;


/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public interface ViewResult extends Iterable<ViewResultRow>, JsonExportable {
    int getTotalRows();

    int getOffset();

    List<ViewResultRow> getRows();
}
