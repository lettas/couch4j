package com.coravy.couch4j;

import java.util.List;


/**
 * 
 * 
 * @author Stefan Saasen (stefan@coravy.com)
 */
public interface ViewResult<T> extends Iterable<ViewResultRow<T>>, JsonExportable {
    int getTotalRows();

    int getOffset();

    List<ViewResultRow<T>> getRows();
}
