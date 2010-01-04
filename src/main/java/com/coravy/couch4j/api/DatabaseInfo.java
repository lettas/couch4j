package com.coravy.couch4j.api;

import java.util.Date;

/**
 * @author Stefan Saasen
 */
public interface DatabaseInfo {
    String getName();

    int getDocCount();

    int getDocDelCountr();

    int getUpdateSeq();

    int getPurgeSeq();

    boolean isCompactRunning();

    int getDiskSize();

    Date getInstanceStartTime();

    int getDiskFormatVersion();
}
